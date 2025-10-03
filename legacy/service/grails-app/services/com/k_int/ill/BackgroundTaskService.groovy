package com.k_int.ill;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.transaction.annotation.Propagation;
import com.k_int.GenericResult;
import com.k_int.ill.constants.ErrorCodes;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.timers.AbstractTimer;
import com.k_int.institution.Institution;

import grails.gorm.transactions.Transactional;
import grails.util.Holders;

/**
 * This handles the background tasks, these are triggered by the folio 2 minute timer
 *
 */
public class BackgroundTaskService {

    LockService lockService;
    OkapiSettingsService okapiSettingsService;

    // Holds the services that we have discovered that perform tasks for the timers
    private static Map serviceTimers = [ : ];

    def performIllTasks(String tenant) {

        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();

        ContextLogging.setValue(ContextLogging.FIELD_MEMORY_FREE, format.format(freeMemory / 1024));
        ContextLogging.setValue(ContextLogging.FIELD_MEMORY_ALLOCATED, format.format(allocatedMemory / 1024));
        ContextLogging.setValue(ContextLogging.FIELD_MEMORY_MAX, format.format(maxMemory / 1024));
        ContextLogging.setValue(ContextLogging.FIELD_MEMORY_TOTAL_FREE, format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
        ContextLogging.setValue(ContextLogging.FIELD_JVM_UPTIME, format.format(jvmUpTime));
        log.debug(ContextLogging.MESSAGE_ENTERING + " performIllTasks");

        // Only want these in the context logging once
        ContextLogging.remove(ContextLogging.FIELD_MEMORY_FREE);
        ContextLogging.remove(ContextLogging.FIELD_MEMORY_ALLOCATED);
        ContextLogging.remove(ContextLogging.FIELD_MEMORY_MAX);
        ContextLogging.remove(ContextLogging.FIELD_MEMORY_TOTAL_FREE);
        ContextLogging.remove(ContextLogging.FIELD_JVM_UPTIME);

        // We need a transaction in order to obtain the lock
        PatronRequest.withTransaction {
	        // We do not want to do any processing if we are already performing the background processing
	        // We have a distributed lock for when there are multiple mod-ill processes running
	        if (!lockService.performWorkIfLockObtained(tenant, LockIdentifier.BACKGROUND_TASKS, 0) {
				doBackgroundTasks(tenant);
	        }) {
	            // Failed to obtain the lock
	            log.info("Skiping background tasks as unable to obtain lock");
	        }
        }
        log.debug(ContextLogging.MESSAGE_EXITING + " performIllTasks");
    }

    private void doBackgroundTasks(String tenant) {
        // Everything should now be in a timer
        try {
            // Process any timers for sending pull slip notification emails
            // Refactor - lastExcecution now contains the next scheduled execution or 0
            // log.debug("Checking timers ready for execution");

            long current_systime = System.currentTimeMillis();

            log.debug("Checking timers");
            Timer[] timers = Timer.executeQuery('select t from Timer as t where ( ( t.nextExecution is null ) OR ( t.nextExecution < :now ) ) and t.enabled=:en',
                                                [now:current_systime, en: true]);
            if ((timers != null) && (timers.size()> 0)) {
                timers.each { timer ->
                    try {
                        ContextLogging.setValue(ContextLogging.FIELD_ID, timer.id);
                        ContextLogging.setValue(ContextLogging.FIELD_TIMER, timer.taskCode);
                        ContextLogging.setValue(ContextLogging.FIELD_JSON, timer.taskConfig);
                        log.debug("** Timer task firing....");

                        TimeZone tz;
                        try {
                            Map tenant_locale = okapiSettingsService.getLocaleSettings();
                            log.debug("Got system locale settings : ${tenant_locale}");
                            String localeTimeZone = tenant_locale?.timezone;
                            if (localeTimeZone == null) {
                                // Time zone not set, so use UTC
                                tz = TimeZone.getTimeZone('UTC');
                            } else {
                                tz = TimeZone.getTimeZone(localeTimeZone);
                            }
                        } catch ( Exception e ) {
                            log.debug("Failure getting locale to determine timezone, processing timer in UTC:", e);
                            tz = TimeZone.getTimeZone('UTC');
                        }

                        // The date we start processing this in the local time zone
                        timer.lastExecution = new DateTime(tz, System.currentTimeMillis()).getTimestamp();

                        if ( ( timer.nextExecution == 0 ) || ( timer.nextExecution == null ) ) {
                            // First time we have seen this timer - we don't know when it is next due - so work that out
                            // as though we just run the timer.
                        } else {
                            runTimer(timer, tenant)
                        }

                        // The timer has completed its work
                        log.debug("** Timer task completed");

                        String rule_to_parse = timer.rrule.startsWith('RRULE:') ? timer.rrule.substring(6) : timer.rrule;

                        // Calculate the next due date
                        RecurrenceRule rule = new RecurrenceRule(rule_to_parse);
                        // DateTime start = DateTime.now()
                        // DateTime start = new DateTime(current_systime)
                        // DateTime start = new DateTime(TimeZone.getTimeZone("UTC"), current_systime)

                        DateTime start = new DateTime(tz, current_systime);
                        // If we are to be executed at the beginning of the day, then clear the time element
                        if (timer.executeAtDayStart) {
                            // Set it to the start of the day, otherwise we will have jobs happening during the day
                            start = start.startOfDay();
                        }

                        // Now work out what the next execution time will be
                        RecurrenceRuleIterator rrule_iterator = rule.iterator(start);
                        def nextInstance = null;

                        // Cycle forward to the next occurrence after this moment
                        int loopcount = 0;
                        while ( ( ( nextInstance == null ) || ( nextInstance.getTimestamp() < current_systime ) ) &&
                                ( loopcount++ < 10 ) ) {
                            nextInstance = rrule_iterator.nextDateTime();
                        }
                        log.debug("Calculated next event for ${timer.id}/${timer.taskCode}/${timer.rrule} as ${nextInstance} (remaining=${nextInstance.getTimestamp()-System.currentTimeMillis()})");
                        log.debug(" -> selected as timestamp ${nextInstance.getTimestamp()}");
                        timer.nextExecution = nextInstance.getTimestamp();
                        timer.save(flush:true, failOnError:true)
                    } catch ( Exception e ) {
                        log.error("Unexpected error processing timer tasks ${e.message} - rule is \"${timer.rrule}\"");
                    } finally {
                        log.debug("Completed timer task");
                    }
                }
            }
        } catch ( Exception e ) {
            log.error("Exception running background tasks",e);
        } finally {
            log.debug("BackgroundTaskService::performIllTasks exiting");
        }
    }

    /**
     * Runs a specific timer now without updating the nextExecution timestamp, either the id or code must be supplied
     * @param id The id of the timer to be executed
     * @param code The code of the timer to be executed
     * @param tenant The tenant the the timer is to be run against
     * @return A result object
     */
    public List executeTimer(String id, String code, String tenant) {
        List result = [ ];

        // Must have been supplier either the id or code and the the tenant
        if ((id || code) && tenant) {
            Timer timer = null;

            // First try by the id
            if (id) {
                timer = Timer.get(id);
            }

            // If we have not found it by the id try using the code
            if ((timer == null) && code) {
                timer = Timer.findByCode(code);
            }

            // Have we found a Timer instance
            if (timer == null) {
                // We have not
                InstitutionResult institutionResult = new InstitutionResult();
                institutionResult.id = (id == null) ? code : id;
                institutionResult.error("Unable to find timer with code " + code, ErrorCodes.TIMER_UNABLE_TO_LOCATE);
                result.add(institutionResult);
            } else {
                // We do not want to do any processing if we are already performing the background processing
                // We have a distributed lock for when there are multiple mod-ill processes running
                if (!lockService.performWorkIfLockObtained(tenant, LockIdentifier.BACKGROUND_TASKS, 0) {
                    // Try and execute the timer
                    result = runTimer(timer, tenant);
                }) {
                    // Failed to obtain the lock
                    InstitutionResult institutionResult = new InstitutionResult();
                    institutionResult.id = (id == null) ? code : id;
                    institutionResult.error("Unable to obtain lock, background tasks must be running", ErrorCodes.TIMER_UNABLE_TO_LOCK);
                    result.add(institutionResult);
                }
            }
        } else {
            // No code or id and tenant supplied
            InstitutionResult institutionResult = new InstitutionResult();
            institutionResult.error("Must supply either the timer id or code and the tenant id to execute a timer", ErrorCodes.TIMER_UNABLE_TO_LOCATE);
            result.add(institutionResult);
        }

        return(result);
    }

    /**
     * Runs the supplied timer for the given tenant
     * @param timer The timer that is to be run
     * @param tenant The tenant the timer is to be run against
     * @return The result of running the timer
     */
    private List runTimer(Timer timer, String tenant) {
        List result = [ ];

        // If the institution on the timer is null then we need to need to run it for all the institutions
        if (timer.institution == null) {
            // Execute the timer for each institution
            Institution.findAll().each { Institution institution ->
                result.add(runTimer(timer, institution, tenant));
            }
        } else {
            // Only needs to runagainst the institution specified against the timer
            result.add(runTimer(timer, timer.institution, tenant));
        }
        return(result);
    }

    /**
     * Runs the supplied timer for the given tenant and institution
     * @param timer The timer that is to be run
     * @param institution The institution the timer is to be run for
     * @param tenant The tenant the timer is to be run against
     * @return The result of running the timer
     */
  	private InstitutionResult runTimer(Timer timer, Institution institution, String tenant) {
        InstitutionResult result = new InstitutionResult(timer.id, institution.id, institution.name);
		try {
			if (timer.taskCode != null) {
				// Get hold of the bean and store it in our map, if we previously havn't been through here
				if (serviceTimers[timer.taskCode] == null) {
					// We capitalise the task code and then prefix it with "timer" and postfix with "Service"
					String beanName = "timer" + timer.taskCode.capitalize() + "Service";

					// Now setup the link to the service action that actually does the work
					try {
						serviceTimers[timer.taskCode] = Holders.grailsApplication.mainContext.getBean(beanName);
					} catch (Exception e) {
                        logError(result, ErrorCodes.TIMER_UNABLE_TO_LOCATE, "Unable to locate timer bean: " + beanName, e);
					}
				}

				// Did we find the bean
				AbstractTimer timerBean = serviceTimers[timer.taskCode];
				if (timerBean == null) {
                    logError(result, ErrorCodes.TIMER_BEAN_IS_NULL, "Unhandled timer, task code ${timer.taskCode}");
				} else {
                    // Start a new session and transaction for each timer, so that everything is not in the same transaction
                    // As the transactions in the different timers should not be related in any form
                    Timer.withNewSession { session ->
                        try {
                            // Start a new transaction
                            Timer.withNewTransaction {
    							// just call the performTask method, with the timers config
    							timerBean.performTask(tenant, institution, timer.taskConfig);
                            }
                        } catch(Exception e) {
                            logError(result, ErrorCodes.TIMER_EXCEPTION_IN_BEAN, "Exception thrown by timer " + timer.code + ", institution id: " + institution.id, e);
                        }
                    }
				}
			} else {
                logError(result, ErrorCodes.TIMER_NO_TASK_SET, "Timer has no task code configured, timer code: " + timer.code);
            }
		} catch ( Exception e ) {
            logError(result, ErrorCodes.TIMER_EXCEPTION, "ERROR running timer", e);
		}

        // Return the result to the caller
        return(result);
    }

    private void logError(GenericResult result, String errorCode, String message, Exception exception = null) {
        // Output the message to the log file first
        String errorMessage = errorCode + ":" + message;
        if (exception == null) {
            log.error(errorMessage, exception);
        } else {
            log.error(errorMessage, exception);
        }

        // Now add it to the Result object
        result.error(message, errorCode, exception);
    }

}

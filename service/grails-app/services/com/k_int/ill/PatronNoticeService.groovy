package com.k_int.ill;

import org.hibernate.LockMode;

import com.k_int.ill.constants.Template;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.ill.templating.TemplateContainer;
import com.k_int.ill.templating.TemplatingService;
import com.k_int.institution.Institution;
import com.k_int.web.toolkit.refdata.RefdataValue;

import groovy.json.JsonBuilder;

/**
 * This service adds generates and processes the notices events
 * @author Chas
 *
 */
public class PatronNoticeService {

    static private final String NOTICE_EVENT_QUERY =  '''
select ne, npn
from NoticeEvent ne, NoticePolicyNotice npn, NoticePolicy np
where ne.sent = false and
      npn.trigger.id = ne.trigger.id and
      np.institution = ne.institution and
      np.id = npn.noticePolicy
''';

    EmailService emailService
    TemplatingService templatingService

    public void triggerNotices(PatronRequest pr, RefdataValue trigger) {
        log.debug("triggerNotices(${pr.patronEmail}, ${trigger.value})")

        // The values from the request we are interested in for the notices
        Map values = requestValues(pr);
        triggerNotices(new JsonBuilder(values).toString(), trigger, pr, pr.institution);
    }

    public void triggerNotices(HostLMSPatronProfile hostLMSPatronProfile) {
        // We need to find the email address for the institution

        Map values = [
            email: getAdminEmail(hostLMSPatronProfile.institution),
            user: [
                patronProfile: hostLMSPatronProfile.name
            ]
        ];

        // There is only one type of notice that can be sent for patron profiles, so it is hard coded here
        triggerNotices(
            new JsonBuilder(values).toString(),
            RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_NOTICE_TRIGGERS, RefdataValueData.NOTICE_TRIGGER_NEW_PATRON_PROFILE),
            null,
            hostLMSPatronProfile.institution
        );
    }

    public void triggerNotices(HostLMSLocation hostLMSLocation) {
        // We need to find the email address for the institution

        Map values = [
            email: getAdminEmail(hostLMSLocation.institution),
            item: [
                location: hostLMSLocation.name
            ]
        ];

        // There is only one type of notice that can be sent for patron profiles, so it is hard coded here
        triggerNotices(
            new JsonBuilder(values).toString(),
            RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_NOTICE_TRIGGERS, RefdataValueData.NOTICE_TRIGGER_NEW_HOST_LMS_LOCATION),
            null,
            hostLMSLocation.institution
        );
    }

    public void triggerNotices(HostLMSShelvingLocation hostLMSShelvingLocation) {
        // We need to find the email address for the institution

        Map values = [
            email: getAdminEmail(hostLMSShelvingLocation.institution),
            item: [
                shelvingLocation: hostLMSShelvingLocation.name
            ]
        ];

        // There is only one type of notice that can be sent for patron profiles, so it is hard coded here
        triggerNotices(
            new JsonBuilder(values).toString(),
            RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_NOTICE_TRIGGERS, RefdataValueData.NOTICE_TRIGGER_NEW_HOST_LMS_SHELVING_LOCATION),
            null,
            hostLMSShelvingLocation.institution
        );
    }

    public void triggerNotices(String jsonData, RefdataValue trigger, PatronRequest pr, Institution institution) {
        NoticeEvent ne = new NoticeEvent(
            patronRequest: pr,
            jsonData: jsonData,
            trigger: trigger,
            institution: institution
        );
        ne.save(flush:true, failOnError:true)
    }

    public void processQueue() {
        log.debug("Processing patron notice queue")
        try {
            NoticeEvent.withSession { sess ->
                NoticeEvent.executeQuery(NOTICE_EVENT_QUERY).each { selectedObjects ->
                    NoticeEvent noticeEvent = selectedObjects[0];
                    // Now see if we can lock the NoticeEvent record
                    if (lockNoticeEvent(noticeEvent)) {
                        NoticePolicyNotice noticePolicyNotice = selectedObjects[1];
                        NoticePolicy noticePolicy = noticePolicyNotice.noticePolicy;
                        Map values = null;
                        if (noticeEvent.jsonData == null) {
                            // This shouldn't happen, it will be for those requests that occurred, just before the upgrade to when we started using jsonData
                            values = requestValues(noticeEvent.patronRequest);
                        } else {
                            // The new way of doing it
                            values = new groovy.json.JsonSlurper().parseText(noticeEvent.jsonData);
                        }

                        // If we do not have an email address or the policy is not active then do not attempt to send it
                        if ((values != null) &&
						    (values.email != null) &&
							(noticePolicy.active == true)) {
                            TemplateContainer template = noticePolicyNotice.template;
                            log.debug("Generating patron notice corresponding to trigger ${noticeEvent.trigger.value} for policy ${noticePolicy.name} and template ${template.name}")
                            try {
                                Map tmplResult = templatingService.performTemplate(template, values, Template.LOCALITY_ENGLISH);
                                if (tmplResult.result.body && tmplResult.result.body.trim()) {
                                    Map emailParams = [
                                        notificationId: noticePolicyNotice.id,
                                        to: values.email,
                                        header: tmplResult.result.header,
                                        body: tmplResult.result.body,
                                        outputFormat: 'text/html'
                                    ];
                                    emailService.sendEmail(emailParams);
                                }
                            } catch (Exception e) {
                                log.error("Problem sending notice", e);
                            }
                        }

                        // "sent" in this case is more like processed -- not all events necessarily result in notices
                        noticeEvent.sent = true;
                        noticeEvent.save(flush:true, failOnError:true);
                    }
                }
            }
            NoticeEvent.executeUpdate('delete NoticeEvent ne where ne.sent = true');
        } catch (Exception e) {
            log.error("Problem processing notice triggers", e);
        } finally {
            log.debug("Completed processing of patron notice triggers");
        }
    }

    private boolean lockNoticeEvent(NoticeEvent noticeEvent) {
        boolean lockObtained = false;
        try {
            NoticeEvent.withCriteria {
                eq "id", noticeEvent.id
                delegate.criteria.lockMode = LockMode.UPGRADE_NOWAIT
            };

            // the noticeEvent record is now locked
            lockObtained = true;
        } catch (Exception e) {
            log.error("Failed to lock NoticeEvent record with id: " + noticeEvent.id);
        }

        return(lockObtained);
    }

    private Map requestValues(PatronRequest pr) {
        // The values from the request we are interested in for the notices
        Map values = [
            email: pr.patronEmail,
            user: [
                id: pr.patronIdentifier,
                givenName: pr?.patronGivenName ?: '',
                surname: pr.patronSurname,
                patronProfile: pr?.resolvedPatron?.userProfile ?: ''
            ],
            request: [
                id: pr.hrid,
                pickupLocation: pr?.pickupLocation ?: '',
                pickupURL: pr?.pickupURL ?: '',
                neededBy: pr?.neededBy?.toString() ?: '',
                cancellationReason: pr?.cancellationReason?.label ?: ''
            ],
            item: [
                barcode: pr?.selectedItemBarcode ?: '',
                title: pr.title,
                materialType: pr?.publicationType?.label ?: '',
                location: pr?.pickLocation?.name,
                shelvingLocation: pr?.pickShelvingLocation?.name
            ]
        ];
        return(values);
    }

    /**
     * returns the administrators email address for the institution
	 * @param institution the institution we want the email address for
     * @return The administrators email address or null if it does not find one
     */
    private String getAdminEmail(Institution institution) {
        String institutionEmail = null;
		if (institution == null) {
			log.trace("No institution supplied to lookup institution admin email for notices");
		} else {
			if (institution.directoryEntry == null) {
				log.trace("No directory entry specified for institution, to lookup institution admin email for notices: " + institution.name);
			} else {
				// Just take the email associated with the directory entry
				institutionEmail = institution.directoryEntry.emailAddress;
			}
		}

        // Return the email address of the institution to the caller
        return(institutionEmail);
    }
}

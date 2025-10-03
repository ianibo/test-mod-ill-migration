package com.k_int.ill;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.statemodel.ActionEvent;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionService;
import com.k_int.settings.SystemSettingsService;
import grails.gorm.multitenancy.CurrentTenant;
import grails.gorm.multitenancy.Tenants;
import groovy.json.JsonSlurper;

/**
 * This service handles various things that are specific to a remote action
 */
@CurrentTenant
public class RemoteActionService {

	private static final String TENANT_POST_FIX = "_mod_ill";
	private static final int TENANT_POST_FIX_LENGTH = TENANT_POST_FIX.length();

	ActionService actionService;
	SystemSettingsService systemSettingsService;

	/**
	 * Gets hold of a specific remote action record
	 * @param remoteActionId the id of the remote action
	 * @return The remote action record if the record existed otherwise null
	 */
	public RemoteAction get(String remoteActionId) {
		RemoteAction remoteAction = null;
		if (remoteActionId != null) {
			remoteAction = RemoteAction.get(remoteActionId);
		}
		return(remoteAction);
	}

	/**
	 * Performs the desired action that the remote action requests
	 * @param remoteAction the remote action object that defines the action to be performed
	 * @return A map that defines if we were successful or not
	 */
	public Map perform(RemoteAction remoteAction) {

		def result = [ : ]
		if (remoteAction == null) {
            log.error("Perform was not supplied a remote action");
            result.actionResult = ActionResult.INVALID_PARAMETERS;
		} else {
			PatronRequest.withTransaction { tstatus ->
				if (isValid(remoteAction)) {
					Map parameters = null
					if (remoteAction.parameters != null) {
						parameters = new JsonSlurper().parseText(remoteAction.parameters);
					}
	
					// Now attempt to perform the action				
					result = actionService.executeAction(remoteAction.patronRequest.id, remoteAction.actionEvent.code, parameters, true);
				} else {
					log.error("Perform was supplied an invalid remote action");
					result.actionResult = ActionResult.INVALID_PARAMETERS;
				}
	
				// If we have been successful, delete the remote action
				if (result.actionResult == ActionResult.SUCCESS) {
					remoteAction.delete();
				} else {
					// Set the last accessed date
					remoteAction.lastAccessed = new Date();
					remoteAction.save(flush:true, failOnError:true);
				}
			}
		}

		// Perform the result		
		return(result);
	}

	/**
	 * Determines if the remote action is still valid, it becomes invalid through one of the following scenarios
	 * 		1. The expiry date for the  remote action is in the past
	 * 		2. The request has moved on to another location in the rota
	 * @param remoteAction the remote action that we are checking to see if it is still valid or not
	 * @return true if it is valid otherwise false
	 */
	private boolean isValid(RemoteAction remoteAction) {
		boolean result = false;
		if (remoteAction != null) {
			// First of all we check the expiry
			if (remoteAction.expires == null) {
				// No expiry set
				result = true;
			} else {
				// Check we havn't expired
				result = (remoteAction.expires.getTime() > determineStartOfDay().getTime());
			}

			// If it has not expired, we need to check the rota position is valid
			if (result) {
				// Is the action for the current rota position
				result = (remoteAction.patronRequest.rotaPosition == remoteAction.rotaPosition);
			}
		}
		return(result);
	}

	/**
	 * Creates a new RemoteAction object
	 * @param patronRequest the patron request the remote action is for
	 * @param actionEventCode the action event code
	 * @param parameters
	 * @param daysToExpire
	 * @return
	 */
	public RemoteAction create(
		PatronRequest patronRequest,
		String actionEventCode,
		String parameters,
		int daysToExpire
	) {
		RemoteAction remoteAction = null;

		try {
			// Lookup the action event
			ActionEvent actionEvent = ActionEvent.lookup(actionEventCode);
	
			// Must have a patron request and an action event		
			if ((patronRequest == null) || (actionEvent == null)) {
				log.error("Cannot create a RemoteAction if we have not been supplied a patron request (" + patronRequest?.id + ") or a valid action event code (" + actionEventCode  + ")");
			} else {
				remoteAction = new RemoteAction();
				remoteAction.patronRequest = patronRequest;
				remoteAction.rotaPosition = patronRequest.rotaPosition;
				remoteAction.actionEvent = actionEvent;
				remoteAction.parameters = parameters;
				remoteAction.dateCreated = new Date();
	
				// Do we need to calculate the expiry day
				if (daysToExpire > 0) {
					// We add 1 so that they get at least 1 full day				
					remoteAction.expires = Date.from(determineZonedStartOfDay().plusDays(daysToExpire + 1).toInstant());
				} 
	
				// Save the remote action			
				remoteAction.save(flush:true, failOnError:true);
			}
		} catch(Exception e) {
			log.error("Caught exception while trying to create a RemoteAction", e);
		}

		return(remoteAction);
	}

	/**
	 * Generates the url that will trigger the action
	 * @param remoteAction the remote action that will trigger the action to be performed
	 * @return The url that will trigger the action to be performed or null if the proxy url setting is not set
	 */
	public String getUrl(RemoteAction remoteAction) {
		// Determine the tenant
		String url = null;
		if (remoteAction == null) {
			log.error("RemoteActionService.getUrl was not supplied a remote action");
		} else {
			String modIllProxyUrl = systemSettingsService.getSettingValue(SettingsData.SETTING_GENERAL_EMAIL_RESPONSE_URL);
			if (modIllProxyUrl == null) {
				log.error("The institution setting \"" + SettingsData.SETTING_GENERAL_EMAIL_RESPONSE_URL + "\" has not been set, but is being used for a remote action");
			} else {
				String fullTenantId = Tenants.currentId();
				String tenantId = fullTenantId.substring(0, fullTenantId.length() - TENANT_POST_FIX_LENGTH);
	
				// Build the url
				StringBuilder urlBuilder = new StringBuilder();
				urlBuilder.append(modIllProxyUrl);
				if (!modIllProxyUrl.endsWith("/")) {
					urlBuilder.append("/");
				}
				urlBuilder.append("_/invoke/tenant/");
				urlBuilder.append(tenantId);
				urlBuilder.append("/ill/remoteAction/");
				urlBuilder.append(remoteAction.id);
				urlBuilder.append("/perform");
				url = urlBuilder.toString();  
			}
		}

		// Return the url to the caller
		return(url);
	}
	
	/**
	 * Determine the start of the day in UTC returning it as a ZonedDateTime object
	 * @return A ZonedDateTime object that represents the start of today with regards to UTC
	 */
	public ZonedDateTime determineZonedStartOfDay() {
		ZoneId zoneId = ZoneId.of("UTC");
		ZonedDateTime utcNow = ZonedDateTime.ofInstant(Instant.now() , zoneId);
		return(utcNow.toLocalDate().atStartOfDay(zoneId));
	}

	/**
	 * Determine the start of the day in UTC returning it as a Date object
	 * @return A Date object that represents the start of today with regards to UTC
	 */
	public Date determineStartOfDay() {
		return(Date.from(determineZonedStartOfDay().toInstant()));
	}
}

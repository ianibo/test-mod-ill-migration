package com.k_int.ill;

import com.k_int.ill.constants.Counter;
import com.k_int.ill.hostlms.holdings.BaseHoldingsHostLmsService;
import com.k_int.ill.hostlms.holdings.HoldingsHostLms;
import com.k_int.ill.hostlms.z3950.BaseZ3950HostLmsService;
import com.k_int.ill.hostlms.z3950.Z3950HostLms;
import com.k_int.ill.lms.HostLMSActions;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.logging.INcipLogDetails;
import com.k_int.ill.logging.ProtocolAuditService;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService;

import grails.core.GrailsApplication;

/**
 * Return the right HostLMSActions for the tenant config
 *
 */
public class HostLmsService {

    private static final Map resultHostLMSNotConfigured = [
        result : false,
        problems : "Host LMS integration not configured: Choose Host LMS in settings or deconfigure host LMS integration in settings."
    ];

  GrailsApplication grailsApplication;
  ProtocolAuditService protocolAuditService;
  BaseHoldingsHostLmsService baseHoldingsHostLmsService;
  BaseZ3950HostLmsService baseZ3950HostLmsService;
  CounterService counterService;
  IllApplicationEventHandlerService illApplicationEventHandlerService;
  InstitutionSettingsService institutionSettingsService;

  	public Z3950HostLms getZ3950HostLms(String lms) {
		Z3950HostLms z3950HostLms = baseZ3950HostLmsService;

		// Were we supplied with an lms		
		if (lms == null) {
			log.error("No lms supplied to getZ3950HostLms.");
		} else {
			// See if we can find the service 
			z3950HostLms = grailsApplication.mainContext."${lms}Z3950HostLmsService";

			// Did we manage to find the service
			if (z3950HostLms == null) {
				// There are no specific overrides for the lms, so just return the base service
				z3950HostLms = baseZ3950HostLmsService;
			}
		}
		
		// Return the host lms z3950 service to the caller		  
		return(z3950HostLms);
	}

  	public HoldingsHostLms getHoldingsHostLms(String lms) {
		HoldingsHostLms holdingsHostLms = baseHoldingsHostLmsService;
		
		// Were we supplied with an lms		
		if (lms == null) {
			log.error("No lms supplied to getHoldingsHostLms.");
		} else {
			// See if we can find the service 
			holdingsHostLms = grailsApplication.mainContext."${lms}HoldingsHostLmsService";

			// Did we manage to find the service
			if (holdingsHostLms == null) {
				// There are no specific overrides for the lms, so just return the base service
				holdingsHostLms = baseHoldingsHostLmsService;
			}
		}

		// Return the hoat lms holdings service to the caller		  
		return(holdingsHostLms);
	}

  public HostLMSActions getHostLMSActionsFor(String lms) {
    log.debug("HostLMSService::getHostLMSActionsFor(${lms})");
    HostLMSActions result = grailsApplication.mainContext."${lms}HostLmsService"

    if ( result == null ) {
      log.warn("Unable to locate HostLMSActions for ${lms}. Did you fail to configure the app_setting \"host_lms_integration\". Current options are aleph|alma|FOLIO|Koha|Millennium|Sierra|Symphony|Voyager|wms|manual|default");
    }

    return result;
  }

  public HostLMSActions getHostLMSActions(Institution institution) {
    HostLMSActions result = null;
    String v = institutionSettingsService.getSettingValue(
        institution,
        SettingsData.SETTING_HOST_LMS_INTEGRATION
    );
    log.debug("Return host lms integrations for : ${v} - query application context for bean named ${v}HostLMSService");
    result = getHostLMSActionsFor(v);
    return result;
  }

  /*
   *  Utility function to handle checking in volumes for a request
   */
  public Map checkInRequestVolumes(PatronRequest request) {

    boolean result = false;
    def resultMap = [:];
    resultMap.checkInList = [];
    resultMap.hostLMS = false;
    resultMap.errors = [];
    resultMap.complete = [:];
    /*
    Since we don't throw errors for checking in already-checked-in items there's no
    reason not to just try and check in all of the volumes
    */
    //def volumesNotCheckedIn = request.volumes.findAll { rv -> rv.status.value == 'awaiting_lms_check_in'; }
    def volumesNotCheckedIn = request.volumes.findAll();
    def totalVolumes = volumesNotCheckedIn.size();
    HostLMSActions host_lms = getHostLMSActions(request.institution);
    if (host_lms) {
      resultMap.hostLMS = true;
      for( def vol : volumesNotCheckedIn ) {
        def checkInMap = [:];
        INcipLogDetails ncipLogDetails = protocolAuditService.getNcipLogDetails(request.institution);
        def check_in_result = host_lms.checkInItem(
            request.institution,
            institutionSettingsService,
            vol.temporaryItemBarcode,
            ncipLogDetails
        );
        protocolAuditService.save(request, ncipLogDetails);
        checkInMap.result = check_in_result;
        checkInMap.volume = vol;
        String message;
        if(check_in_result?.result == true) {
          if(check_in_result?.already_checked_in == true) {
            message = "NCIP CheckinItem call succeeded for item: ${vol.temporaryItemBarcode}. ${check_in_result.reason=='spoofed' ? '(No host LMS integration configured for check in item call)' : 'Host LMS integration: CheckinItem not performed because the item was already checked in.'}"
          } else {
            message = "NCIP CheckinItem call succeeded for item: ${vol.temporaryItemBarcode}. ${check_in_result.reason=='spoofed' ? '(No host LMS integration configured for check in item call)' : 'Host LMS integration: CheckinItem call succeeded.'}"
          }
          checkInMap.success = true;
          illApplicationEventHandlerService.auditEntry(request, request.state, request.state, message, null);
          def newVolStatus = check_in_result.reason=='spoofed' ? vol.lookupStatus('lms_check_in_(no_integration)') : vol.lookupStatus('completed')
          vol.status = newVolStatus
          vol.save(failOnError: true)
        } else {
          request.needsAttention=true;
          checkInMap.success = false;
          message = "Host LMS integration: NCIP CheckinItem call failed for item: ${vol.temporaryItemBarcode}. Review configuration and try again or deconfigure host LMS integration in settings. "+check_in_result.problems?.toString();
          illApplicationEventHandlerService.auditEntry(
            request,
            request.state,
            request.state,
            "Host LMS integration: NCIP CheckinItem call failed for item: ${vol.temporaryItemBarcode}. Review configuration and try again or deconfigure host LMS integration in settings. "+check_in_result.problems?.toString(),
            null);
        }
        checkInMap.message = message;
        checkInMap.state = request.state;
        resultMap.checkInList.add(checkInMap);
      }
    } else {
      def errorMap = [:];
      String message = 'Host LMS integration not configured: Choose Host LMS in settings or deconfigure host LMS integration in settings.';
      errorMap.message = message;
      errorMap.state = request.state;
      resultMap.errors.add(errorMap);
      illApplicationEventHandlerService.auditEntry(
        request,
        request.state,
        request.state,
        message,
        null);
      request.needsAttention=true;
    }
    //Make sure we don't have any hanging volumes
    volumesNotCheckedIn = request.volumes.findAll { rv -> rv.status.value == 'awaiting_lms_check_in'; }

    if(volumesNotCheckedIn.size() == 0) {
      counterService.decrementCounter(request.institution, Counter.COUNTER_ACTIVE_LOANS);
      request.needsAttention = false;
      request.activeLoan = false;

      if(totalVolumes > 0) {
        String message = "Complete request succeeded. Host LMS integration: CheckinItem call succeeded for all items.";
        resultMap.complete.message = message;
        resultMap.complete.state = request.state;
        illApplicationEventHandlerService.auditEntry(request, request.state, request.state, message, null);
      } else {
        log.debug("No items found to check in for request ${request.id}");
      }
      result = true;
    } else {
      def errorMap = [:];
      String message = "Host LMS integration: NCIP CheckinItem calls failed for some items."
      illApplicationEventHandlerService.auditEntry(request,
        request.state,
        request.state,
        message,
        null);
      errorMap.message = message;
      errorMap.state = request.state;
      resultMap.errors.add(errorMap);
      request.needsAttention = true;
    }
    resultMap.result = result;
    return resultMap;
  }

    /**
     * looks up the patron using NCIP and records the messages are stored in the protocol audit table if enabled
     * @param institution The institution the call is being made for
     * @param request the request associated with the patron lookup (maybe null)
     * @param patronIdentifier the id of the patron to be looked up
     * @return a map containing the result of the lookup
     */
    public Map lookupPatron(Institution institution, PatronRequest request, String patronIdentifier) {
        Map patronDetails;
        HostLMSActions hostLMSActions = getHostLMSActions(institution);
        if (hostLMSActions) {
            INcipLogDetails ncipLogDetails = protocolAuditService.getNcipLogDetails(institution);
            patronDetails = hostLMSActions.lookupPatron(
                institution,
                institutionSettingsService,
                patronIdentifier, ncipLogDetails
            );
            protocolAuditService.save(request, ncipLogDetails);
        } else {
            patronDetails = resultHostLMSNotConfigured;
        }
        return(patronDetails);
    }

    /**
     * looks to see if the requested item is available using Z3950 and records the messages are stored in the protocol audit table if enabled
     * @param request the request this lookup is for
     * @param protocolType The protocol type it is for
     * @return The location holding this item where it is available or null if it has not been found or it is not available
     */
    public ItemLocation determineBestLocation(PatronRequest request, ProtocolType protocolType) {
        ItemLocation location = null;
        HostLMSActions hostLMSActions = getHostLMSActions(request.institution);
        if (hostLMSActions) {
            IHoldingLogDetails holdingLogDetails = protocolAuditService.getHoldingLogDetails(request.institution, protocolType);
            location = hostLMSActions.determineBestLocation(institutionSettingsService, request, holdingLogDetails);
            protocolAuditService.save(request, holdingLogDetails);
        }
        return(location);
    }

    /**
     * Checks out the specified item using NCIP and records the messages are stored in the protocol audit table if enabled
     * @param request the request this item is associated with
     * @param itemId the id of the item to be checked out
     * @param institutionalPatronIdValue the patron that the item should be checked out to
     * @return A Map containg the result of the checkout
     */
    public Map checkoutItem(PatronRequest request, String itemId, String institutionalPatronIdValue) {
        Map checkoutResult;
        HostLMSActions hostLMSActions = getHostLMSActions(request.institution);
        if (hostLMSActions) {
            INcipLogDetails ncipLogDetails = protocolAuditService.getNcipLogDetails(request.institution);
            checkoutResult = hostLMSActions.checkoutItem(
                request.institution,
                institutionSettingsService,
                request.hrid,
                itemId,
                institutionalPatronIdValue,
                ncipLogDetails
            );
            protocolAuditService.save(request, ncipLogDetails);
        } else {
            checkoutResult = resultHostLMSNotConfigured;
            request.needsAttention = true;
        }
        return(checkoutResult);
    }

    /**
     * Creates a temporary item in the local lms using NCIP and records the messages are stored in the protocol audit table if enabled
     * @param request the request triggering the accept item message
     * @param temporaryItemBarcode the id of the temporary item to be created
     * @param requestedAction the action to be performed (no idea what actions can be performed)
     * @return a map containing the outcome of the accept item call
     */
    public Map acceptItem(PatronRequest request, String temporaryItemBarcode, String requestedAction) {
        Map acceptResult;
        HostLMSActions hostLMSActions = getHostLMSActions(request.institution);
        if (hostLMSActions) {
        INcipLogDetails ncipLogDetails = protocolAuditService.getNcipLogDetails(request.institution);
            acceptResult = getHostLMSActions(request.institution).acceptItem(
                request.institution,
                institutionSettingsService,
                temporaryItemBarcode,
                request.hrid,
                request.patronIdentifier, // user_idA
                request.author, // author,
                request.title, // title,
                request.isbn, // isbn,
                request.localCallNumber, // call_number,
                request.resolvedPickupLocation?.lmsLocationCode, // pickup_location,
                requestedAction, // requested_action
                ncipLogDetails
            );
            protocolAuditService.save(request, ncipLogDetails);
        } else {
            acceptResult = resultHostLMSNotConfigured;
            request.needsAttention = true;
        }
        return(acceptResult);
    }
}

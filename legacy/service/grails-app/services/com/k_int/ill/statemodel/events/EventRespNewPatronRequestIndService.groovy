package com.k_int.ill.statemodel.events;

import com.k_int.ill.HostLmsService;
import com.k_int.ill.IllActionService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.ProtocolType;
import com.k_int.ill.SharedIndexService;
import com.k_int.ill.iso18626.codes.closed.Status;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.EventFetchRequestMethod;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.ill.statemodel.Events;
import com.k_int.settings.InstitutionSettingsService

/**
 * This event service takes a new responder patron request and attempts to locate the item if enabled
 * @author Chas
 */
public class EventRespNewPatronRequestIndService extends AbstractEvent {

    HostLmsService hostLmsService;
    // PatronNoticeService patronNoticeService;
    IllActionService illActionService;
    InstitutionSettingsService institutionSettingsService;
    SharedIndexService sharedIndexService;

    @Override
    String name() {
        return(Events.EVENT_RESPONDER_NEW_PATRON_REQUEST_INDICATION);
    }

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    // Notify us of a new responder patron request in the database
    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        if (request != null) {
            try {
                log.debug('Launch auto responder for request');
                String autoRespondSetting = institutionSettingsService.getSettingValue(
                    request.institution,
                    SettingsData.SETTING_AUTO_RESPONDER_STATUS
                );
                if (autoRespondSetting?.toLowerCase().startsWith('on')) {
                    autoRespond(request, autoRespondSetting.toLowerCase(), eventResultDetails);
                } else {
                    eventResultDetails.auditMessage = "Auto responder is ${autoRespondSetting} - manual checking needed";
                    request.needsAttention = true;
                }
            } catch (Exception e) {
                log.error("Problem in auto respond: ${e.getMessage()}", e);
            }
        } else {
            log.warn("Unable to locate request for ID ${eventData.payload.id}} isRequester=${request?.isRequester}");
        }

        return(eventResultDetails);
    }

    private void autoRespond(PatronRequest request, String autoRespondVariant, EventResultDetails eventResultDetails) {
        log.debug('autoRespond....');

        // Use the hostLmsService to determine the best location to send a pull-slip to
        ItemLocation location = hostLmsService.determineBestLocation(request, ProtocolType.Z3950_RESPONDER);
        log.debug("result of determineBestLocation = ${location}");

        // Were we able to locate a copy?
        boolean unfilled = false;
        if (location != null) {
            // set localCallNumber to whatever we managed to look up
            if (illApplicationEventHandlerService.routeRequestToLocation(request, location)) {
                eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_LOCATED;
                eventResultDetails.auditMessage = 'autoRespond will-supply, determine location=' + location;
                log.debug("Send ExpectToSupply response to ${request.requestingInstitutionSymbol}");
				eventResultDetails.sendProtocolMessage = true;
//                illActionService.sendResponse(request, Status.EXPECT_TO_SUPPLY, [:], eventResultDetails)
            } else {
                unfilled = true;
                eventResultDetails.auditMessage = 'AutoResponder Failed to route to location ' + location;
            }
        } else {
            // No - is the auto responder set up to sent not-supplied?
            if (autoRespondVariant == 'on:_will_supply_and_cannot_supply') {
                unfilled = true;
                eventResultDetails.auditMessage = 'AutoResponder cannot locate a copy.';
            }
        }

        // If it was unfilled then send a response
        if (unfilled == true) {
            log.debug("Send unfilled(No copy) response to ${request.requestingInstitutionSymbol}");
			eventResultDetails.sendProtocolMessage = true;
//            illActionService.sendResponse(request,  'Unfilled', ['reason':'No copy'], eventResultDetails);
            eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_UNFILLED;
        }
    }
}

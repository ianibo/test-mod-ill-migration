package com.k_int.ill.statemodel.events;

import com.k_int.ill.IllActionService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.EventFetchRequestMethod;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.ill.statemodel.Events;
import com.k_int.ill.statemodel.StatusStage;
import com.k_int.settings.InstitutionSettingsService

/**
 * Event triggered when a cancel request is received from the requester
 * @author Chas
 *
 */
public class EventStatusResCancelRequestReceivedIndService extends AbstractEvent {

    IllActionService illActionService;
    InstitutionSettingsService institutionSettingsService;

    @Override
    String name() {
        return(Events.EVENT_STATUS_RES_CANCEL_REQUEST_RECEIVED_INDICATION);
    }

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        String autoCancel = institutionSettingsService.getSettingValue(
            request.institution,
            SettingsData.SETTING_AUTO_RESPONDER_CANCEL
        );
        if (autoCancel?.toLowerCase().startsWith('on')) {
            log.debug('Auto cancel is on');

            // System has auto-respond cancel on
            if (request.state?.stage == StatusStage.ACTIVE_SHIPPED) {
                // Revert the state to it's original before the cancel request was received - previousState
                eventResultDetails.auditMessage = 'AutoResponder:Cancel is ON - but item is SHIPPED. Responding NO to cancel, revert to previous state';
                eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_SHIPPED;
                illActionService.sendSupplierCancelResponse(request, [cancelResponse : 'no'], eventResultDetails);
            } else {
                // Just respond YES
                eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_CANCELLED;
                eventResultDetails.auditMessage =  'AutoResponder:Cancel is ON - responding YES to cancel request';
                illActionService.sendSupplierCancelResponse(request, [cancelResponse : 'yes'], eventResultDetails);
            }
        } else {
            // Set needs attention=true
            eventResultDetails.auditMessage = 'Cancellation Request Received';
            request.needsAttention = true;
        }

        return(eventResultDetails);
    }
}

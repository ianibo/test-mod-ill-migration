package com.k_int.ill.statemodel.events;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.EventFetchRequestMethod;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.ill.statemodel.Events;
import com.k_int.ill.statemodel.StatusStage;

/**
 * Event that is triggered when a request is cancelled with a supplier
 * @author Chas
 *
 */
public class EventStatusReqCancelledWithSupplierIndService extends AbstractEvent {

    @Override
    String name() {
        return(Events.EVENT_STATUS_REQ_CANCELLED_WITH_SUPPLIER_INDICATION);
    }

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        // We must have found the request, and it has to be in a stage of completed
        if (request.state?.stage == StatusStage.COMPLETED) {
            if (request.requestToContinue == true) {
                eventResultDetails.auditMessage = 'Request to continue, sending to next lender';
                eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_CONTINUE;
                log.debug(eventResultDetails.auditMessage);
            } else {
                log.debug('Cancelling request')
                eventResultDetails.auditMessage = 'Request cancelled';
            }
        } else {
            log.warn('Request not in the correct state ' + " (${request?.state?.code}).");
        }
        return(eventResultDetails);
    }
}

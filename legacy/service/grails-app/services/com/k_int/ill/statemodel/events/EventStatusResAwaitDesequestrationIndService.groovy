package com.k_int.ill.statemodel.events;

import com.k_int.ill.IllActionService
import com.k_int.ill.PatronRequest
import com.k_int.ill.iso18626.codes.closed.Status;
import com.k_int.ill.statemodel.AbstractEvent
import com.k_int.ill.statemodel.EventFetchRequestMethod
import com.k_int.ill.statemodel.EventResultDetails
import com.k_int.ill.statemodel.Events

public class EventStatusResAwaitDesequestrationIndService extends AbstractEvent {

    @Override
    String name() {
        return(Events.EVENT_STATUS_RES_AWAIT_DESEQUESTRATION_INDICATION);
    }

    IllActionService illActionService;

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        // We only deal with responder
        if (request.isRequester) {
            log.debug("pr ${request.id} is requester, not sending protocol message");
        } else {
            log.debug("Sending protocol message with overdue status change from PatronRequest ${request.id}");
			eventResultDetails.sendProtocolMessage = true;
//            illActionService.sendStatusChange(request, Status.OVERDUE, eventResultDetails, 'Request has reached the due date and loan has expired')
        }

        return(eventResultDetails);
    }
}

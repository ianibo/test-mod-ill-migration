package com.k_int.ill.statemodel.events;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.EventFetchRequestMethod;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.ill.statemodel.Events;

/**
 * This event service id for those events that have not been implemented
 * @author Chas
 *
 */
public class EventNoImplementationService extends AbstractEvent {

    @Override
    String name() {
        return(Events.EVENT_NO_IMPLEMENTATION);
    }

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        // We assume anything that is not implemented will have the request id in the payload
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        // There is nothing to do
        //log.error('Event ' + eventData.event + ' has not been implemented');

        // No need to save the request or add an audit entry
        eventResultDetails.saveData = false;
        return(eventResultDetails);
    }
}

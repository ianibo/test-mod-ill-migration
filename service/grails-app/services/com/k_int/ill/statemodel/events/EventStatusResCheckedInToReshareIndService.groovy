package com.k_int.ill.statemodel.events;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.IllActionService;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.EventFetchRequestMethod;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.ill.statemodel.Events;

/**
 * This event is triggered when the item is checked in to ill
 * @author Chas
 *
 */
public class EventStatusResCheckedInToIllIndService extends AbstractEvent {

    IllActionService illActionService;

    @Override
    String name() {
        return(Events.EVENT_STATUS_RES_CHECKED_IN_TO_ILL_INDICATION);
    }

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        /**
         * It's not clear if the system will ever need to differentiate between the status of checked in and
         * await shipping, so for now we leave the 2 states in place and just automatically transition  between them
         * this method exists largely as a place to put functions and workflows that diverge from that model
         */
        eventResultDetails.auditMessage = 'Request awaits shipping';

        return(eventResultDetails);
    }
}

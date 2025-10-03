package com.k_int.ill.statemodel.events;

import com.k_int.web.toolkit.refdata.RefdataValue
import com.k_int.ill.PatronRequest
import com.k_int.ill.referenceData.RefdataValueData
import com.k_int.ill.statemodel.EventFetchRequestMethod
import com.k_int.ill.statemodel.EventResultDetails
import com.k_int.ill.statemodel.Events

/**
 * Event service triggered when a digital loan is initiated for a requested item
 */
public class EventStatusReqLoanedDigitallyIndService extends EventTriggerNoticesService {

    @Override
    String name() {
        return(Events.EVENT_STATUS_REQ_LOANED_DIGITALLY_INDICATION);
    }

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        // all we need to do is call triggerNotice
        triggerNotice(request, RefdataValueData.NOTICE_TRIGGER_LOANED_DIGITALLY);

        return(eventResultDetails);
    }
}

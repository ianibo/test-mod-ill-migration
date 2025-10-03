package com.k_int.ill.statemodel.events;

import com.k_int.ill.statemodel.Events;
import com.k_int.web.toolkit.refdata.RefdataValue;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronNoticeService;

/**
 * Event raised when a supplier has been identified
 * @author Chas
 *
 */
public class EventStatusReqSupplierIdentifiedIndService extends EventSendToNextLenderService {

    PatronNoticeService patronNoticeService;

    @Override
    String name() {
        return(Events.EVENT_STATUS_REQ_SUPPLIER_IDENTIFIED_INDICATION);
    }

    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        patronNoticeService.triggerNotices(request, RefdataValue.lookupOrCreate('noticeTriggers', 'New request'));
        return super.processEvent(request, eventData, eventResultDetails);
    }
}

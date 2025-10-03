package com.k_int.ill.statemodel.events;

import com.k_int.ill.PatronNoticeService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.EventFetchRequestMethod;

import com.k_int.web.toolkit.refdata.RefdataValue;

/**
 * Base event class for notices
 * @author Chas
 *
 */
public abstract class EventTriggerNoticesService extends AbstractEvent {

    PatronNoticeService patronNoticeService;

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    public void triggerNotice(PatronRequest request, String trigger) {
        patronNoticeService.triggerNotices(request, RefdataValue.lookupOrCreate('noticeTriggers', trigger));
    }
}

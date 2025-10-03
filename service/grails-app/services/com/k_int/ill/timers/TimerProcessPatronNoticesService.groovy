package com.k_int.ill.timers;

import com.k_int.ill.PatronNoticeService;
import com.k_int.institution.Institution;

/**
 * Processes patron notices
 *
 * @author Chas
 *
 */
public class TimerProcessPatronNoticesService extends AbstractTimer {

    def grailsApplication;
    PatronNoticeService patronNoticeService;

	@Override
	public void performTask(String tenant, Institution institution, String config) {
        // Are the patron notices enabled
        if ( grailsApplication.config?.ill?.patronNoticesEnabled == true ) {
            // They are so process the queue
            patronNoticeService.processQueue()
        }
	}
}

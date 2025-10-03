package com.k_int.ill.referenceData;

import com.k_int.ill.CounterService;
import com.k_int.ill.constants.Counter;

import grails.util.Holders;
import groovy.util.logging.Slf4j;

@Slf4j
public class CounterData {

	public void load() {
		log.info("Adding counter data to the database");

        // Get hold of the counter service
        CounterService counterService = Holders.grailsApplication.mainContext.getBean('counterService');

        counterService.ensureCounter(null, Counter.COUNTER_ACTIVE_BORROWING);
        counterService.ensureCounter(null, Counter.COUNTER_ACTIVE_LOANS);
	}

	public static void loadAll() {
		(new CounterData()).load();
	}
}

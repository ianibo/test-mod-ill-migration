package com.k_int.ill.referenceData;

import com.k_int.ill.CopyrightMessageService;
import com.k_int.ill.constants.CountryCodes;
import com.k_int.ill.iso18626.codes.open.CopyrightCompliance;

import grails.util.Holders;
import groovy.util.logging.Slf4j;

/**
 * Loads the copyright message data required for the system to process requests
 */
@Slf4j
public class CopyrightMessageData {

	private void load() {
		log.info("Adding copyright messages to the database");
		
		// Get hold of the copyright message service
		CopyrightMessageService copyrightMessageService = Holders.grailsApplication.mainContext.getBean('copyrightMessageService');

		if (copyrightMessageService == null) {
			log.error("Unable to locate the copright message service");	
		} else {
			copyrightMessageService.ensure(
				CopyrightCompliance.AU_COPYR_CAT_S183_COMW,
				"Copyright Cat S183 – Commonwealth (Australia)",
				CountryCodes.AUSTRALIA,
				"To be determined"
			);
			copyrightMessageService.ensure(
				CopyrightCompliance.AU_COPYR_CAT_S183_STATE,
				"Copyright Act S183 – State (Australia)",
				CountryCodes.AUSTRALIA,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.AU_COPYRIGHT_ACT_S49,
				"Copyright Act S49  (Australia)",
				CountryCodes.AUSTRALIA,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.AU_COPYRIGHT_ACT_S50_1,
				"Copyright Act S50[1] (Australia)",
				CountryCodes.AUSTRALIA,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.AU_COPYRIGHT_ACT_S50_7A,
				"Copyright Act S50[7]A (Australia)",
				CountryCodes.AUSTRALIA,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.AU_COPYRIGHT_CLEARED,
				"Copyright Cleared (Australia)",
				CountryCodes.AUSTRALIA,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.AU_GENBUS,
				"General Business (Australia)",
				CountryCodes.AUSTRALIA,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.NZ_COPYRIGHT_ACT_S54,
				"Copyright Act S54 (New Zealand)",
				CountryCodes.NEW_ZEALAND,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.NZ_COPYRIGHT_ACT_S55,
				"Copyright Act S55 (New Zealand)",
				CountryCodes.NEW_ZEALAND,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.OTHER,
				"Other copyright, refer to the notes",
				CountryCodes.UNITED_KINGDOM,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.UK_COPYR_FEE_PAID,
				"Copyright Fee Paid (UK)",
				CountryCodes.UNITED_KINGDOM,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.UK_FAIR_DEALING,
				"Fair Dealing (UK)",
				CountryCodes.UNITED_KINGDOM,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.US_CCG,
				"CCG (US)",
				CountryCodes.UNITED_STATES_OF_AMERICA,
				"To be determined"
			);

			copyrightMessageService.ensure(
				CopyrightCompliance.US_CCL,
				"CCL (US code)",
				CountryCodes.UNITED_STATES_OF_AMERICA,
				"To be determined"
			);
		}
	}

	public static void loadAll() {
		(new CopyrightMessageData()).load();
	}
}

package com.k_int.ill.hostlms;

import org.olf.rs.circ.client.CirculationClient;
import org.olf.rs.circ.client.NCIPClientWrapper

import com.k_int.ill.hostlms.holdings.MillenniumHoldingsHostLmsService;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

/**
 * The interface between mod-ill and any host Library Management Systems
 *
 */
public class MillenniumHostLmsService extends BaseHostLmsService {

	MillenniumHoldingsHostLmsService millenniumHoldingsHostLmsService;

	public CirculationClient getCirculationClient(Institution institution, ISettings settings, String address) {
		// This wrapper creates the circulationClient we need
		return new NCIPClientWrapper(address, [protocol: "NCIP2"]).circulationClient;
	}

	@Override
	public List<ItemLocation> extractAvailableItemsFromOpacRecord(opacRecord, String reason=null) {
		return(millenniumHoldingsHostLmsService.extractAvailableItemsFromOpacRecord(opacRecord, reason));
	}
}

package com.k_int.ill.hostlms;

import org.olf.rs.circ.client.CirculationClient;
import org.olf.rs.circ.client.NCIPClientWrapper;

import com.k_int.ill.hostlms.holdings.SierraHoldingsHostLmsService;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

/**
 * The interface between mod-ill and any host Library Management Systems
 *
 */
public class SierraHostLmsService extends BaseHostLmsService {

	SierraHoldingsHostLmsService sierraHoldingsHostLmsService;
	
	List<String> NOTES_CONSIDERED_AVAILABLE = ['AVAILABLE', 'CHECK SHELVES'];

	public CirculationClient getCirculationClient(Institution institution, ISettings settings, String address) {
		// This wrapper creates the circulationClient we need
		return new NCIPClientWrapper(address, [protocol: "NCIP2"]).circulationClient;
	}

	@Override
	public boolean isNCIP2() {
		return(true);
	}

	@Override
	public List<ItemLocation> extractAvailableItemsFromOpacRecord(opacRecord, String reason=null) {
		return(sierraHoldingsHostLmsService.extractAvailableItemsFromOpacRecord(opacRecord, reason));
	}
}

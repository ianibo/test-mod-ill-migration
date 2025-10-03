package com.k_int.ill.hostlms;

import org.olf.rs.circ.client.CirculationClient;
import org.olf.rs.circ.client.NCIPClientWrapper;

import com.k_int.ill.hostlms.holdings.HorizonHoldingsHostLmsService;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

public class HorizonHostLmsService extends BaseHostLmsService {

	HorizonHoldingsHostLmsService horizonHoldingsHostLmsService;
	
	public CirculationClient getCirculationClient(Institution institution, ISettings settings, String address) {
		// This wrapper creates the circulationClient we need
		return new NCIPClientWrapper(address, [protocol: "NCIP1"]).circulationClient;
	}

	@Override
	//We need to also eliminate any holdings of type "Internet"
	protected List<ItemLocation> extractAvailableItemsFrom(z_response, String reason, IHoldingLogDetails holdingLogDetails) {
		return(horizonHoldingsHostLmsService.extractAvailableItemsFrom(z_response, reason, holdingLogDetails));
	}
}

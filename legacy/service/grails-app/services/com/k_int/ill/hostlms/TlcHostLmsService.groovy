package com.k_int.ill.hostlms;

import org.olf.rs.circ.client.CirculationClient;
import org.olf.rs.circ.client.NCIPClientWrapper;

import com.k_int.ill.hostlms.holdings.TlcHoldingsHostLmsService;
import com.k_int.ill.hostlms.z3950.TlcZ3950HostLmsService;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

public class TlcHostLmsService extends BaseHostLmsService {

	TlcHoldingsHostLmsService tlcHoldingsHostLmsService;
	TlcZ3950HostLmsService tlcZ3950HostLmsService;
	
	@Override
	protected String getHoldingsQueryRecsyn() {
		return(tlcZ3950HostLmsService.recordSyntax());
	}

	public CirculationClient getCirculationClient(Institution institution, ISettings settings, String address) {
		// This wrapper creates the circulationClient we need
		return new NCIPClientWrapper(address, [protocol: "NCIP1"]).circulationClient;
	}

	@Override
	protected List<ItemLocation> extractAvailableItemsFrom(zResponse, String reason, IHoldingLogDetails holdingLogDetails) {
		return(tlcHoldingsHostLmsService.extractAvailableItemsFrom(zResponse, reason, holdingLogDetails));
	}

	@Override
	protected String getNCIPTemplatePrefix() {
		return "tlc";
	}

	@Override
	public List<ItemLocation> extractAvailableItemsFromMARCXMLRecord(record, String reason, IHoldingLogDetails holdingLogDetails) {
		return(tlcHoldingsHostLmsService.extractAvailableItemsFromMARCXMLRecord(record, reason, holdingLogDetails));
	}
}

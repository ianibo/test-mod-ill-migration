package com.k_int.ill.hostlms;

import org.olf.rs.circ.client.CirculationClient;
import org.olf.rs.circ.client.NCIPClientWrapper;

import com.k_int.ill.hostlms.holdings.KohaHoldingsHostLmsService;
import com.k_int.ill.hostlms.z3950.KohaZ3950HostLmsService;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

/**
 * The interface between mod-ill and any host Library Management Systems
 *
 */
public class KohaHostLmsService extends BaseHostLmsService {

	KohaHoldingsHostLmsService kohaHoldingsHostLmsService;
	KohaZ3950HostLmsService kohaZ3950HostLmsService;
	
	public CirculationClient getCirculationClient(Institution institution, ISettings settings, String address) {
		// This wrapper creates the circulationClient we need
		return new NCIPClientWrapper(address, [protocol: "NCIP2", useNamespace: false]).circulationClient;
	}

	@Override
	public boolean isNCIP2() {
		return(true);
	}

	@Override
	protected List<ItemLocation> extractAvailableItemsFrom(z_response, String reason, IHoldingLogDetails holdingLogDetails) {
		return(kohaHoldingsHostLmsService.extractAvailableItemsFrom(z_response, reason, holdingLogDetails));
	}

	@Override
	protected String getHoldingsQueryRecsyn() {
		return(kohaZ3950HostLmsService.recordSyntax());
	}

	@Override
	public List<ItemLocation> extractAvailableItemsFromMARCXMLRecord(record, String reason, IHoldingLogDetails holdingLogDetails) {
		return(kohaHoldingsHostLmsService.extractAvailableItemsFromMARCXMLRecord(record, reason, holdingLogDetails));
	}
}

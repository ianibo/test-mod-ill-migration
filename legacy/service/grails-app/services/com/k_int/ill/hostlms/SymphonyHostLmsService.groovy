package com.k_int.ill.hostlms;

import org.olf.rs.circ.client.CirculationClient;
import org.olf.rs.circ.client.NCIPClientWrapper;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.hostlms.holdings.SymphonyHoldingsHostLmsService;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

/**
 * The interface between mod-ill and any host Library Management Systems
 *
 * Sirsi Z3950 behaves a little differently when looking for available copies.
 * The format of the URL for metaproxy needs to be
 * http://mpserver:9000/?x-target=http://unicornserver:2200/UNICORN&x-pquery=@attr 1=1016 @attr 3=3 water&maximumRecords=1&recordSchema=marcxml
 *
 */
public class SymphonyHostLmsService extends BaseHostLmsService {

	SymphonyHoldingsHostLmsService symphonyHoldingsHostLmsService;
	
	public CirculationClient getCirculationClient(Institution institution, ISettings settings, String address) {
		// This wrapper creates the circulationClient we need
		return new NCIPClientWrapper(address, [protocol: "NCIP1"]).circulationClient;
	}

	@Override
	protected String getHoldingsQueryRecsyn() {
		return 'marcxml';
	}

	//Override to search on attribute 1016, and prepend '^C' to search string
	@Override
	public List<ItemLocation> z3950ItemsByIdentifier(PatronRequest pr, ISettings settings, IHoldingLogDetails holdingLogDetails) {

		List<ItemLocation> result = [];

		String search_id = pr.supplierUniqueRecordId;
		String prefix_query_string = "@attr 1=1016 ${search_id}";
		def z_response = z3950Service.query(
			pr.institution,
			settings,
			prefix_query_string,
			1,
			getHoldingsQueryRecsyn(),
			holdingLogDetails
		);
		log.debug("Got Z3950 response: ${z_response}");

		if ( z_response?.numberOfRecords == 1 ) {
			// Got exactly 1 record
			List<ItemLocation> availability_summary = extractAvailableItemsFrom(z_response,"Match by @attr 1=1016 ${search_id}", holdingLogDetails)
			if ( availability_summary.size() > 0 ) {
				result = availability_summary;
			}

			log.debug("At end, availability summary: ${availability_summary}");
		}

		return result;
	}

	@Override
	protected List<ItemLocation> extractAvailableItemsFrom(z_response, String reason, IHoldingLogDetails holdingLogDetails) {
		return(symphonyHoldingsHostLmsService.extractAvailableItemsFrom(z_response, reason, holdingLogDetails));
	}

	@Override
	public List<ItemLocation> extractAvailableItemsFromMARCXMLRecord(record, String reason, IHoldingLogDetails holdingLogDetails) {
		return(symphonyHoldingsHostLmsService.extractAvailableItemsFromMARCXMLRecord(record, reason, holdingLogDetails));
	}
}

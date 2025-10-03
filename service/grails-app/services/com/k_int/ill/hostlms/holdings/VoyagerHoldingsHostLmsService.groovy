package com.k_int.ill.hostlms.holdings;

import com.k_int.ill.lms.ItemLocation;

/**
 * For interpreting the response from a Voyager z3950 server 
 *
 */
public class VoyagerHoldingsHostLmsService extends BaseHoldingsHostLmsService {

	@Override
	public List<ItemLocation> extractAvailableItemsFromOpacRecord(opacRecord, String reason=null) {

		List<ItemLocation> availability_summary = [];

		opacRecord?.holdings?.holding?.each { hld ->
			log.debug("VoyagerHostLMSService holdings record :: ${hld}");
			hld.circulations?.circulation?.each { circ ->
				def loc = hld?.localLocation?.text()?.trim();
				if (loc && circ?.availableNow?.@value == '1') {
					log.debug("Available now");
					ItemLocation il = new ItemLocation(
						reason: reason,
						location: loc,
						shelvingLocation: hld?.shelvingLocation?.text()?.trim() ?: null,
						temporaryLocation: circ?.temporaryLocation?.text()?.trim() ?: null,
						itemLoanPolicy: circ?.availableThru?.text()?.trim() ?: null,
						itemId: circ?.itemId?.text()?.trim() ?: null,
						callNumber: hld?.callNumber?.text()?.trim() ?: null
					);
					availability_summary << il;
				}
			}
		}

		return(availability_summary);
	}
}

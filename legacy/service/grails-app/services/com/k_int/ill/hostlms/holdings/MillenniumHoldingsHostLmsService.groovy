package com.k_int.ill.hostlms.holdings;

import com.k_int.ill.lms.ItemLocation;

/**
 * For interpreting the response from a Millennium z3950 server 
 *
 */
public class MillenniumHoldingsHostLmsService extends BaseHoldingsHostLmsService {

	@Override
	public List<ItemLocation> extractAvailableItemsFromOpacRecord(opacRecord, String reason=null) {

		List<ItemLocation> availability_summary = [];

		opacRecord?.holdings?.holding?.each { hld ->
			log.debug("${hld}");
			if ( hld.publicNote?.toString() == 'AVAILABLE' ) {
				log.debug("Available now");
				ItemLocation il = new ItemLocation(
					reason: reason,
					location: hld.localLocation?.toString(),
					shelvingLocation:hld.localLocation?.toString(),
					callNumber:hld.callNumber?.toString()
				);
				availability_summary << il;
			}
		}

		return(availability_summary);
	}
}

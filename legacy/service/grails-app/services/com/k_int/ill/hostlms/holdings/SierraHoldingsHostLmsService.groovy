package com.k_int.ill.hostlms.holdings;

import com.k_int.ill.lms.ItemLocation;

/**
 * For interpreting the response from a Sierra z3950 server 
 *
 */
public class SierraHoldingsHostLmsService extends BaseHoldingsHostLmsService {

  List<String> NOTES_CONSIDERED_AVAILABLE = ['AVAILABLE', 'CHECK SHELVES'];

  	/**
   	 * III Sierra doesn't provide an availableNow flag in it's holdings record - instead the XML looks as followS:
  	 * <holdings>
   	 *   <holding>
   	 *     <localLocation>Gumberg Silverman Phen General - 1st Floor</localLocation>
   	 *     <callNumber>B3279.H94 T756 2021 </callNumber>
   	 *     <publicNote>AVAILABLE</publicNote>
   	 *   </holding>
   	 * </holdings>
   	 *
   	 * We are taking publicNote==AVAILABLE as an indication of an available copy
   	 */
  	@Override
	public List<ItemLocation> extractAvailableItemsFromOpacRecord(opacRecord, String reason=null) {

		List<ItemLocation> availability_summary = [];

		opacRecord?.holdings?.holding?.each { hld ->
			log.debug("Process sierra OPAC holdings record:: ${hld}");
			def note = hld?.publicNote?.toString();
			if ( note && NOTES_CONSIDERED_AVAILABLE.contains(note) ) {
				log.debug("SIERRA OPAC Record: Item Available now");
				ItemLocation il = new ItemLocation(
					reason: reason,
					location: hld.localLocation?.toString(),
					shelvingLocation:hld.localLocation?.toString(),
					callNumber:hld?.callNumber?.toString()
				);
				availability_summary << il;
			}
		}

		return(availability_summary);
	}
}

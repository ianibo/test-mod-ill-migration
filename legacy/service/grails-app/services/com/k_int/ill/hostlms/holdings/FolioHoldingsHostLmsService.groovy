package com.k_int.ill.hostlms.holdings;

import com.k_int.ill.lms.ItemLocation;

/**
 * For interpreting the response from a Folio z3950 server 
 *
 */
public class FolioHoldingsHostLmsService extends BaseHoldingsHostLmsService {

  /*

  Use temporaryLocation field in circulation record

  <holding>
   <typeOfRecord>c</typeOfRecord>
   <encodingLevel>3</encodingLevel>
   <receiptAcqStatus>1</receiptAcqStatus>
   <generalRetention />
   <completeness>n</completeness>
   <dateOfReport>00</dateOfReport>
   <nucCode>Cornell University</nucCode>
   <localLocation>Olin Library</localLocation>
   <shelvingLocation>Olin</shelvingLocation>
   <callNumber>CB19 .G69 2021</callNumber>
   <copyNumber>2</copyNumber>
   <circulations>
      <circulation>
         <availableNow value="1" />
         <itemId>31924128478918</itemId>
         <renewable value="0" />
         <onHold value="0" />
         <temporaryLocation>Olin Reserve</temporaryLocation>
      </circulation>
   </circulations>
  </holding>
  */

	@Override
	public List<ItemLocation> extractAvailableItemsFromOpacRecord(opacRecord, String reason=null) {

		List<ItemLocation> availability_summary = [];

		opacRecord?.holdings?.holding?.each { hld ->
			log.debug("BaseHostLMSService holdings record:: ${hld}");
			hld.circulations?.circulation?.each { circ ->
				def loc = hld?.localLocation?.text()?.trim();
				if (loc && circ?.availableNow?.@value == '1') {
					log.debug("Folio extractAvailableItemsFromOpacRecord Available now");
					ItemLocation il = new ItemLocation(
						reason: reason,
						location: loc,
						shelvingLocation: hld?.shelvingLocation?.text()?.trim() ?: null,
						temporaryShelvingLocation: circ?.temporaryLocation?.text()?.trim() ?: null,
						itemLoanPolicy: circ?.availableThru?.text()?.trim() ?: null,
						itemId: circ?.itemId?.text()?.trim() ?: null,
						callNumber: hld?.callNumber?.text()?.trim() ?: null)
					availability_summary << il;
				}
			}
		}

		return(availability_summary);
  	}
}

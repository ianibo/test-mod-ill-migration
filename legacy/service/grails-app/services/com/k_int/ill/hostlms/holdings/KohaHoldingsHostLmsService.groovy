package com.k_int.ill.hostlms.holdings;

import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;

/**
 * For interpreting the response from a Koha z3950 server 
 *
 */
public class KohaHoldingsHostLmsService extends BaseHoldingsHostLmsService {

	// Given the record syntax above, process response records as Opac recsyn. If you change the recsyn string above
	// you need to change the handler here. SIRSI for example needs to return us marcxml with a different location for the holdings
	@Override
	public List<ItemLocation> extractAvailableItemsFrom(z_response, String reason, IHoldingLogDetails holdingLogDetails) {
		log.debug("Extract holdings from Koha marcxml record ${z_response}");
		if ( z_response?.numberOfRecords != 1 ) {
			log.warn("Multiple records seen in response from Koha Z39.50 server, unable to extract available items. Record: ${z_response}");
			return null;
		}

		List<ItemLocation> availability_summary = null;
		if ( z_response?.records?.record?.recordData?.record != null ) {
			availability_summary = extractAvailableItemsFromMARCXMLRecord(z_response?.records?.record?.recordData?.record, reason, holdingLogDetails);
		}
		return availability_summary;
	}

	@Override
	public List<ItemLocation> extractAvailableItemsFromMARCXMLRecord(record, String reason, IHoldingLogDetails holdingLogDetails) {
		// <zs:searchRetrieveResponse>
		//   <zs:numberOfRecords>9421</zs:numberOfRecords>
		//   <zs:records>
		//     <zs:record>
		//       <zs:recordSchema>marcxml</zs:recordSchema>
		//       <zs:recordXMLEscaping>xml</zs:recordXMLEscaping>
		//       <zs:recordData>
		//         <record>
		//           <leader>02370cam a2200541Ii 4500</leader>
		//           <controlfield tag="008">140408r20141991nyua j 001 0 eng d</controlfield>
		//           <datafield tag="040" ind1=" " ind2=" ">
		//           </datafield>
		//           <datafield tag="926" ind1=" " ind2=" ">
		//             <subfield code="a">WEST</subfield>
		//             <subfield code="b">RESERVES</subfield>
		//             <subfield code="c">QL737 .C23 C58 2014</subfield>
		//             <subfield code="d">BOOK</subfield>
		//             <subfield code="f">2</subfield>
		//           </datafield>
		log.debug("KohaHoldingsHostLMSService extracting available items from record ${record}");
		List<ItemLocation> availability_summary = []
		holdingLogDetails.newRecord();
		record.datafield.each { df ->
			if ( df.'@tag' == "952" ) {
				holdingLogDetails.holdings(df);
				Map<String,String> tag_data = [:];
				df.subfield.each { sf ->
					if ( sf.'@code' != null ) {
						tag_data[ sf.'@code'.toString().trim() ] = sf.text().trim();
					}
				}

				log.debug("Found holdings tag : ${df} ${tag_data}");

				try {
					if ( tag_data['7'] != null ) {
						if ( tag_data['7'] == '0' ) {
							log.debug("Assuming ${tag_data['7']}");
							availability_summary << new ItemLocation(
								location: tag_data['b'],
								shelvingLocation: tag_data['c'],
								callNumber:tag_data['o']
							);
						} else {
							log.debug("Subfield '7' is not zero (${tag_data['7']})");
						}
					} else {
						log.debug("No subfield '7' present - unable to determine number of copies available");
					}
				} catch ( Exception e ) {
					// All kind of odd strings like 'NONE' that mean there aren't any holdings available
					log.debug("Unable to parse number of copies: ${e.message}");
				}
			}
		}
		log.debug("MARCXML availability: ${availability_summary}");
		return(availability_summary);
	}
}

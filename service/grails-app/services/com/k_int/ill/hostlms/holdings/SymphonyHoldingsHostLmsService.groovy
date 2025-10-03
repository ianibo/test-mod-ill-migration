package com.k_int.ill.hostlms.holdings;

import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;

/**
 * For interpreting the response from a Sierra z3950 server 
 *
 * Sirsi Z3950 behaves a little differently when looking for available copies.
 * The format of the URL for metaproxy needs to be
 * http://mpserver:9000/?x-target=http://unicornserver:2200/UNICORN&x-pquery=@attr 1=1016 @attr 3=3 water&maximumRecords=1&recordSchema=marcxml
 *
 */
public class SymphonyHoldingsHostLmsService extends BaseHoldingsHostLmsService {

	@Override
	public List<ItemLocation> extractAvailableItemsFrom(z_response, String reason, IHoldingLogDetails holdingLogDetails) {
		log.debug("Extract holdings from Symphony marcxml record ${z_response}");
		if ( z_response?.numberOfRecords != 1 ) {
			log.warn("Multiple records seen in response from Symphony Z39.50 server, unable to extract available items. Record: ${z_response}");
			return null;
		}

		List<ItemLocation> availability_summary = null;
		if ( z_response?.records?.record?.recordData?.record != null ) {
			availability_summary = extractAvailableItemsFromMARCXMLRecord(z_response?.records?.record?.recordData?.record, reason, holdingLogDetails);
		}
		return(availability_summary);
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
		log.debug("extractAvailableItemsFromMARCXMLRecord (SymphonyHostLMSService)");
		List<ItemLocation> availability_summary = [];
		holdingLogDetails.newRecord();
		record.datafield.each { df ->
			if ( df.'@tag' == "926" ) {
				holdingLogDetails.holdings(df);
				Map<String,String> tag_data = [:]
				df.subfield.each { sf ->
					if ( sf.@code != null ) {
						tag_data[ sf.'@code'.toString() ] = sf.toString()
					}
				}
				log.debug("Found holdings tag : ${df} ${tag_data}");
				try {
					if ( tag_data['b'] != null ){
						if ( [ 'RESERVES', 'CHECKEDOUT', 'MISSING', 'DISCARD'].contains(tag_data['b']) ) {
							// $b contains a string we think implies non-availability
						} else {
							log.debug("Assuming ${tag_data['b']} implies available - update extractAvailableItemsFromMARCXMLRecord if not the case");
							availability_summary << new ItemLocation(
								location: tag_data['a'],
								shelvingLocation: tag_data['b'],
								callNumber: tag_data['c'],
								itemLoanPolicy: tag_data?.d
							);
						}
					} else {
						log.debug("No subfield b present - unable to determine number of copies available");
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

package com.k_int.ill.hostlms.holdings;

import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;

public class BaseHoldingsHostLmsService implements HoldingsHostLms {

    /**
     * Record the details of the holdings for each of the records in the collection
     * @param records The records that have been found
     * @param holdingLogDetails Where we are recording the details
     */
    protected void logOpacHoldings(Iterable records, IHoldingLogDetails holdingLogDetails) {
        // Have we been supplied any records
        if (records != null) {
            // We have so process them
            records.each { record ->
                holdingLogDetails.newRecord();
                holdingLogDetails.holdings(record?.recordData?.opacRecord?.holdings);
            }
        }
    }

    // Given the record syntax above, process response records as Opac recsyn. If you change the recsyn string above
    // you need to change the handler here. SIRSI for example needs to return us marcxml with a different location for the holdings
    public List<ItemLocation> extractAvailableItemsFrom(
		Object z3950Response,
		String reason,
		IHoldingLogDetails holdingLogDetails
	) {
        List<ItemLocation> availability_summary = null;
        if (z3950Response?.records?.record?.recordData?.opacRecord != null) {
            Iterable withHoldings = z3950Response.records.record.findAll { it?.recordData?.opacRecord?.holdings?.holding?.size() > 0 };

            // Log the holdings
            logOpacHoldings(withHoldings, holdingLogDetails);

            if (withHoldings.size() < 1) {
                log.warn("BaseHostLMSService failed to find an OPAC record with holdings");
                return(null);
            } else if (withHoldings.size() > 1) {
                log.warn("BaseHostLMSService found multiple OPAC records with holdings");
              return(null);
            }
            log.debug("[BaseHostLMSService] Extract available items from OPAC record ${z3950Response}, reason: ${reason}");
            availability_summary = extractAvailableItemsFromOpacRecord(withHoldings?.first()?.recordData?.opacRecord, reason);
        } else {
            log.warn("BaseHostLMSService expected the response to contain an OPAC record, but none was found");
        }
        return(availability_summary);
    }

	/**
	 * Override this method if the server returns opac records but does something dumb like cram availability status into a public note
	 */
	public List<ItemLocation> extractAvailableItemsFromOpacRecord(opacRecord, String reason=null) {
  
	    List<ItemLocation> availability_summary = [ ];
  
	    opacRecord?.holdings?.holding?.each { hld ->
  		    log.debug("BaseHostLMSService holdings record:: ${hld}");
		    hld.circulations?.circulation?.each { circ ->
		        def loc = hld?.localLocation?.text()?.trim();
		        if (loc && circ?.availableNow?.@value == '1') {
			        log.debug("BASE extractAvailableItemsFromOpacRecord Available now");
			        ItemLocation il = new ItemLocation(
					    reason: reason,
					    location: loc,
					    shelvingLocation: hld?.shelvingLocation?.text()?.trim() ?: null,
					    itemLoanPolicy: circ?.availableThru?.text()?.trim() ?: null,
					    itemId: circ?.itemId?.text()?.trim() ?: null,
					    callNumber: hld?.callNumber?.text()?.trim() ?: null);
			        availability_summary << il;
		        }
		    }
	    }
  
	    return(availability_summary);
	}

    /**
     * N.B. this method may be overriden in the LMS specific subclass - check there first - this is the default implementation
     */
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
        List<ItemLocation> availability_summary = [ ];
        holdingLogDetails.newRecord();
        record.datafield.each { df ->
            if ( df.'@tag' == "926" ) {
                holdingLogDetails.holdings(df);
                Map<String,String> tag_data = [ : ];
                df.subfield.each { sf ->
                    if ( sf.'@code' != null ) {
                        tag_data[ sf.'@code'.toString().trim() ] = sf.text().trim();
                    }
                }
                log.debug("Found holdings tag : ${df} ${tag_data}");
                try {
                    if ( tag_data['b'] != null ) {
                        if ( [ 'RESERVES', 'CHECKEDOUT', 'MISSING', 'DISCARD'].contains(tag_data['b']) ) {
                            // $b contains a string we think implies non-availability
                        } else {
                            log.debug("Assuming ${tag_data['b']} implies available - update extractAvailableItemsFromMARCXMLRecord if not the case");
                            availability_summary << new ItemLocation(
                                location: tag_data['a'],
                                shelvingLocation: tag_data['b'],
                                callNumber:tag_data['c'],
                                reason: reason
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

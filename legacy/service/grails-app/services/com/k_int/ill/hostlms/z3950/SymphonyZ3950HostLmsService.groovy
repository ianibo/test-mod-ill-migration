package com.k_int.ill.hostlms.z3950;

import com.k_int.ill.constants.Z3950;
import com.k_int.ill.referenceData.RefdataValueData;

/**
 * For defining z3950 requests that are specific to a Symphony z3950 server 
 *
 */
public class SymphonyZ3950HostLmsService extends BaseZ3950HostLmsService {

	static private final Map useAttributes = [
		(RefdataValueData.SEARCH_ATTRIBUTE_SUPPLIER_UNIQUE_RECORD_ID) : Z3950.USE_ANY
	];
	
	@Override
	public String recordSyntax() {
		return(Z3950.RECORD_SYNTAX_MARC_XML);
	}

	public Map overrideUseAttributes() {
		// We have use attributes to override
		return(useAttributes);
	}
}

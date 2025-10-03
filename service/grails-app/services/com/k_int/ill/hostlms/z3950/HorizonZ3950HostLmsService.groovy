package com.k_int.ill.hostlms.z3950;

import com.k_int.ill.constants.Z3950;
import com.k_int.ill.referenceData.RefdataValueData;

/**
 * For defining z3950 requests that are specific to a Horizon z3950 server 
 *
 */
public class HorizonZ3950HostLmsService extends BaseZ3950HostLmsService {

	static private final Map useAttributes = [
		(RefdataValueData.SEARCH_ATTRIBUTE_SUPPLIER_UNIQUE_RECORD_ID) : Z3950.USE_HORIZON_UNIQUE_ID
	];
	
	public Map overrideUseAttributes() {
		// We have use attributes to override
		return(useAttributes);
	}
}

package com.k_int.ill.hostlms.z3950;

import com.k_int.ill.constants.Z3950;

/**
 * For defining z3950 requests that are specific to a Koha z3950 server 
 *
 */
public class KohaZ3950HostLmsService extends BaseZ3950HostLmsService {

	@Override
	public String recordSyntax() {
		return(Z3950.RECORD_SYNTAX_MARC_XML);
	}
}

package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.complexTypes.AgencyId;
import com.k_int.ill.iso18626.complexTypes.RequestingAgencyAuthentication;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestingAgencyHeader extends SenderHeader {

	public RequestingAgencyAuthentication requestingAgencyAuthentication;

	// Redundant for 2021
	public AgencyId consortialId;
	
	public RequestingAgencyHeader() {
	}

	public RequestingAgencyHeader(
			AgencyId supplyingAgencyId,
			AgencyId requestingAgencyId,
			String requestingAgencyRequestId,
			String supplyingAgencyRequestId,
			RequestingAgencyAuthentication requestingAgencyAuthentication
	) {
		// Constructor for ISO-18626 version 2017
		this(
			supplyingAgencyId,
			requestingAgencyId,
			requestingAgencyRequestId,
			null,
			supplyingAgencyRequestId,
			requestingAgencyAuthentication
		);
	}
	
	public RequestingAgencyHeader(
		AgencyId supplyingAgencyId,
		AgencyId requestingAgencyId,
		String requestingAgencyRequestId,
		AgencyId consortialId,
		String supplyingAgencyRequestId,
		RequestingAgencyAuthentication requestingAgencyAuthentication
	) {
		// Constructor for ISO-18626 version 2021
        super(supplyingAgencyId, requestingAgencyId, requestingAgencyRequestId, supplyingAgencyRequestId);
        this.consortialId = consortialId;
        this.requestingAgencyAuthentication = requestingAgencyAuthentication;
	}
}

package com.k_int.ill.iso18626.types;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.complexTypes.AgencyId;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Header {

	public AgencyId supplyingAgencyId;
	public AgencyId requestingAgencyId;
	public String timestamp;
	public String requestingAgencyRequestId;

	public Header() {
	}

	public Header(
		AgencyId supplyingAgencyId,
		AgencyId requestingAgencyId,
		String requestingAgencyRequestId
	) {
		this.supplyingAgencyId = supplyingAgencyId;
		this.requestingAgencyId = requestingAgencyId;
		this.timestamp = Instant.now().toString();
		this.requestingAgencyRequestId = requestingAgencyRequestId;
	}
}

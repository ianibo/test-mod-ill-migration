package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.complexTypes.AgencyId;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SenderHeader extends Header {

	public String supplyingAgencyRequestId;

	public SenderHeader() {
	}

	public SenderHeader(
		AgencyId supplyingAgencyId,
		AgencyId requestingAgencyId,
		String requestingAgencyRequestId,
		String supplyingAgencyRequestId
	) {
		super(supplyingAgencyId, requestingAgencyId, requestingAgencyRequestId);
		this.supplyingAgencyRequestId = supplyingAgencyRequestId;
	}
}

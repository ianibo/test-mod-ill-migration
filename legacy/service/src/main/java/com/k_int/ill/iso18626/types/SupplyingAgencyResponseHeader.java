package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.closed.ReasonForMessage;
import com.k_int.ill.iso18626.complexTypes.AgencyId;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SupplyingAgencyResponseHeader extends ResponseHeader {

	public ReasonForMessage reasonForMessage;

	public SupplyingAgencyResponseHeader() {
	}

    public SupplyingAgencyResponseHeader(
        AgencyId supplyingAgencyId,
        AgencyId requestingAgencyId,
        String requestingAgencyRequestId,
        ReasonForMessage reasonForMessage
    ) {
        super(supplyingAgencyId, requestingAgencyId, requestingAgencyRequestId);
        this.reasonForMessage = reasonForMessage;
    }
}

package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.closed.Action;
import com.k_int.ill.iso18626.complexTypes.AgencyId;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestingAgencyResponseHeader extends ResponseHeader {

	public Action action;

	public RequestingAgencyResponseHeader() {
	}

    public RequestingAgencyResponseHeader(
        AgencyId supplyingAgencyId,
        AgencyId requestingAgencyId,
        String requestingAgencyRequestId,
        Action action
    ) {
        super(supplyingAgencyId, requestingAgencyId, requestingAgencyRequestId);
        this.action = action;
    }
}

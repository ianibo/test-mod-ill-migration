package com.k_int.ill.iso18626;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.k_int.ill.iso18626.types.RequestingAgencyResponseHeader;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestingAgencyMessageConfirmation {

    @JsonProperty("confirmationHeader")
	public RequestingAgencyResponseHeader header;

	public RequestingAgencyMessageConfirmation() {
	}

    public RequestingAgencyMessageConfirmation(RequestingAgencyMessage requestingAgencyMessage) {
        this.header = new RequestingAgencyResponseHeader(
            requestingAgencyMessage.header.supplyingAgencyId,
            requestingAgencyMessage.header.requestingAgencyId,
            requestingAgencyMessage.header.requestingAgencyRequestId,
            requestingAgencyMessage.findAction()
        );
    }
}

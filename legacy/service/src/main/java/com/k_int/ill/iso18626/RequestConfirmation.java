package com.k_int.ill.iso18626;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.k_int.ill.iso18626.types.ResponseHeader;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestConfirmation {

    @JsonProperty("confirmationHeader")
	public ResponseHeader header;

	public RequestConfirmation() {
	}

    public RequestConfirmation(Request requestMessage) {
        this.header = new ResponseHeader(
            requestMessage.header.supplyingAgencyId,
            requestMessage.header.requestingAgencyId,
            requestMessage.header.requestingAgencyRequestId
        );
    };
}

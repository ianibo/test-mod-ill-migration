package com.k_int.ill.iso18626;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.k_int.ill.iso18626.types.SupplyingAgencyResponseHeader;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SupplyingAgencyMessageConfirmation {

    @JsonProperty("confirmationHeader")
	SupplyingAgencyResponseHeader header;

	public SupplyingAgencyMessageConfirmation() {
	};

    public SupplyingAgencyMessageConfirmation(SupplyingAgencyMessage supplyingAgencyMessage) {
        this.header = new SupplyingAgencyResponseHeader(
            supplyingAgencyMessage.header.supplyingAgencyId,
            supplyingAgencyMessage.header.requestingAgencyId,
            supplyingAgencyMessage.header.requestingAgencyRequestId,
            (supplyingAgencyMessage.messageInfo == null ? null : supplyingAgencyMessage.messageInfo.reasonForMessage)
        );
    }
}

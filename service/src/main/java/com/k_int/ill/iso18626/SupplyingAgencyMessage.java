package com.k_int.ill.iso18626;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.types.DeliveryInfo;
import com.k_int.ill.iso18626.types.MessageInfo;
import com.k_int.ill.iso18626.types.RetryInfo;
import com.k_int.ill.iso18626.types.ReturnInfo;
import com.k_int.ill.iso18626.types.SenderHeader;
import com.k_int.ill.iso18626.types.ShippingInfo;
import com.k_int.ill.iso18626.types.StatusInfo;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SupplyingAgencyMessage {

	public SenderHeader header;
	public MessageInfo messageInfo;
	public StatusInfo statusInfo;
	public DeliveryInfo deliveryInfo;
	public ReturnInfo returnInfo;

	// New for 2021
	public RetryInfo retryInfo;
	public ShippingInfo shippingInfo;
	
	public SupplyingAgencyMessage() {
	}

	public SupplyingAgencyMessage(
		SenderHeader header,
		MessageInfo messageInfo,
		StatusInfo statusInfo,
		DeliveryInfo deliveryInfo,
		ReturnInfo returnInfo
	) {
		// Constructor for ISO-18626 version 2017
		this(
			header,
			messageInfo,
			statusInfo,
			null,
			deliveryInfo,
			null,
			returnInfo
		);
	}

	public SupplyingAgencyMessage(
		SenderHeader header,
		MessageInfo messageInfo,
		StatusInfo statusInfo,
		RetryInfo retryInfo,
		DeliveryInfo deliveryInfo,
		ShippingInfo shippingInfo,
		ReturnInfo returnInfo
	) {
		// Constructor for ISO-18626 version 2021
		this.header = header;
		this.messageInfo = messageInfo;
		this.statusInfo = statusInfo;
		this.retryInfo = retryInfo;
		this.deliveryInfo = deliveryInfo;
		this.shippingInfo = shippingInfo;
		this.returnInfo = returnInfo;
	}
}

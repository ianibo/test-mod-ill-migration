package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.complexTypes.Address;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestedDeliveryInfo {

	public Integer sortOrder;
	public Address address;

	public RequestedDeliveryInfo() {
	}

	public RequestedDeliveryInfo(
		Integer sortOrder,
		Address address
	) {
		this.sortOrder = sortOrder;
		this.address = address;
	}
}

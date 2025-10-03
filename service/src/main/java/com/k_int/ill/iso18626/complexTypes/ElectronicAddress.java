package com.k_int.ill.iso18626.complexTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.open.ElectronicAddressType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ElectronicAddress {

	public ElectronicAddressType electronicAddressType;
	public String electronicAddressData;

	public ElectronicAddress() {
	}

	public ElectronicAddress(
		String electronicAddressTypeCode,
		String electronicAddressData
	) {
		if (electronicAddressTypeCode != null) {
			electronicAddressType = new ElectronicAddressType(electronicAddressTypeCode);
		}
		this.electronicAddressData = electronicAddressData;
	}
}

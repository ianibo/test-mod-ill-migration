package com.k_int.ill.iso18626.complexTypes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Address {

	public ElectronicAddress electronicAddress;
	public PhysicalAddress physicalAddress;

	public Address() {
	}

	public Address(ElectronicAddress electronicAddress) {
		this.electronicAddress = electronicAddress;
	}

	public Address(PhysicalAddress physicalAddress) {
		this.physicalAddress = physicalAddress;
	}
}

package com.k_int.ill.iso18626.complexTypes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PhysicalAddress {

	public String line1;
	public String line2;
	public String locality;
	public String postalCode;
	public String region;
	public String country;

	public PhysicalAddress() {
	}

	public PhysicalAddress(
		String line1,
		String line2,
		String locality,
		String postalCode,
		String region,
		String country
	) {
		this.line1 = line1;
		this.line2 = line2;
		this.locality = locality;
		this.postalCode = postalCode;
		this.region = region;
		this.country = country;
	}
}

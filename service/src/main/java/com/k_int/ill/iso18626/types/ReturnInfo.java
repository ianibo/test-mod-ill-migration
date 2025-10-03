package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.complexTypes.AgencyId;
import com.k_int.ill.iso18626.complexTypes.PhysicalAddress;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReturnInfo {

	public AgencyId returnAgencyId;
	public String name;
	public PhysicalAddress physicalAddress;

	public ReturnInfo() {
	}

	public ReturnInfo(
		AgencyId returnAgencyId,
		String name,
		PhysicalAddress physicalAddress
	) {
		this.returnAgencyId = returnAgencyId;
		this.name = name;
		this.physicalAddress = physicalAddress;
	}
}

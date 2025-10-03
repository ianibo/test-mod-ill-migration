package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class AgencyIdType extends Code {
	// The initial valid agency id type codes
	public static final String ISIL = "ISIL";

	static {
		add(ISIL);
	}

	public AgencyIdType() {
	}

	public AgencyIdType(String agencyIdType) {
		super(agencyIdType);
	}
}

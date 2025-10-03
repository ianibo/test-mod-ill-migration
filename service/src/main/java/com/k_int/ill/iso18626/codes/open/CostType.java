package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class CostType extends Code {
	// The initial valid agency id type codes
	public static final String INSURANCE          = "Insurance";
	public static final String INSURANCE_COVERAGE = "InsuranceCoverage";
	public static final String PACKAGING          = "Packaging";
	public static final String SERVICE            = "Service";
	public static final String SHIPPING           = "Shipping";
	public static final String TAX                = "Tax";

	static {
		add(INSURANCE);
		add(INSURANCE_COVERAGE);
		add(PACKAGING);
		add(SERVICE);
		add(SHIPPING);
		add(TAX);
	}

	public CostType() {
	}

	public CostType(String costType) {
		super(costType);
	}
}

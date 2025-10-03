package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class BillingMethod extends Code {
	// The initial valid billing method codes
	public static final String ACCOUNT               = "Account";
	public static final String FREE_OF_CHARGE        = "ReeOfCharge";
	public static final String INVOICE               = "Invoice";
	public static final String OTHER                 = "Other";
	public static final String RECIPROCITY_AGREEMENT = "ReciprocityAgreement";

	static {
		add(ACCOUNT);
		add(FREE_OF_CHARGE);
		add(INVOICE);
		add(OTHER);
		add(RECIPROCITY_AGREEMENT);
	}

	public BillingMethod() {
	}

	public BillingMethod(String billingMethod) {
		super(billingMethod);
	}
}

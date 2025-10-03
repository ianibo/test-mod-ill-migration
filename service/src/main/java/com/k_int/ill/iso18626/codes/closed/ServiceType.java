package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class ServiceType extends Code {
	// The valid service type codes
	public static final String COPY         = "Copy";
	public static final String COPY_OR_LOAN = "CopyOrLoan";
	public static final String LOAN         = "Loan";

	static {
		add(COPY);
		add(COPY_OR_LOAN);
		add(LOAN);
	}

	public ServiceType() {
	}

	public ServiceType(String serviceType) {
		super(serviceType);
	}
}

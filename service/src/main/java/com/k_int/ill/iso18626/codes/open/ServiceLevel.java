package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class ServiceLevel extends Code {
	// The initial valid service level codes
	public static final String EXPRESS        = "Express";
	public static final String NORMAL         = "Normal";
	public static final String RUSH           = "Rush";
	public static final String SECONDARY_MAIL = "SecondaryMail";
	public static final String STANDARD       = "Standard";
	public static final String URGENT         = "Urgent";

	static {
		add(EXPRESS);
		add(NORMAL);
		add(RUSH);
		add(SECONDARY_MAIL);
		add(STANDARD);
		add(URGENT);
	}

	public ServiceLevel() {
	};

	public ServiceLevel(String serviceLevel) {
		super(serviceLevel);
	};
}

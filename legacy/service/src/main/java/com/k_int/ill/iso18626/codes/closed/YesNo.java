package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class YesNo extends Code {
	// The valid action codes
	public static final String NO  = "N";
	public static final String YES = "Y";

	static {
		add(NO);
		add(YES);
	}

	public YesNo() {
	}

	public YesNo(String yesNo) {
		super(yesNo);
	}
}

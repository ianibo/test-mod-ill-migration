package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class RequestType extends Code {
	// The valid request type codes
	public static final String NEW      = "New";
	public static final String REMINDER = "Reminder";
	public static final String RETRY    = "Retry";

	static {
		add(NEW);
		add(REMINDER);
		add(RETRY);
	}

	public RequestType() {
	}

	public RequestType(String requestType) {
		super(requestType);
	}
}

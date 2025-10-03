package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class Action extends Code {
	// The valid action codes
	public static final String CANCEL          = "Cancel";
	public static final String NOTIFICATION    = "Notification";
	public static final String RECEIVED        = "Received";
	public static final String RENEW           = "Renew";
	public static final String SHIPPED_FORWARD = "ShippedForward";
	public static final String SHIPPED_RETURN  = "ShippedReturn";
	public static final String STATUS_REQUEST  = "StatusRequest";

	static {
		add(CANCEL);
		add(NOTIFICATION);
		add(RECEIVED);
		add(RENEW);
		add(SHIPPED_FORWARD);
		add(SHIPPED_RETURN);
		add(STATUS_REQUEST);
	}

	public Action() {
	}

	public Action(String action) {
		super(action);
	}
}

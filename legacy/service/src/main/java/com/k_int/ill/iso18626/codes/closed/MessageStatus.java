package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class MessageStatus extends Code {
	// The valid message status codes
	public static final String ERROR = "ERROR";
	public static final String OK    = "OK";

	static {
		add(OK);
		add(ERROR);
	}

	public MessageStatus() {
	}

	public MessageStatus(String messageStatus) {
		super(messageStatus);
	}
}

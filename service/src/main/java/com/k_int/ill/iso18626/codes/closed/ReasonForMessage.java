package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class ReasonForMessage extends Code {

    // The valid cancel reason codes
    public static final String MESSAGE_REASON_CANCEL_RESPONSE         = "CancelResponse";
    public static final String MESSAGE_REASON_NOTIFICATION            = "Notification";
    public static final String MESSAGE_REASON_RENEW_RESPONSE          = "RenewResponse";
    public static final String MESSAGE_REASON_REQUEST_RESPONSE        = "RequestResponse";
    public static final String MESSAGE_REASON_STATUS_CHANGE           = "StatusChange";
    public static final String MESSAGE_REASON_STATUS_REQUEST_RESPONSE = "StatusRequestResponse";

	static {
		add(MESSAGE_REASON_CANCEL_RESPONSE);
		add(MESSAGE_REASON_NOTIFICATION);
		add(MESSAGE_REASON_RENEW_RESPONSE);
		add(MESSAGE_REASON_REQUEST_RESPONSE);
		add(MESSAGE_REASON_STATUS_CHANGE);
		add(MESSAGE_REASON_STATUS_REQUEST_RESPONSE);
	}

	public ReasonForMessage() {
	}

	public ReasonForMessage(String reasonForMessage) {
		super(reasonForMessage);
	}
}

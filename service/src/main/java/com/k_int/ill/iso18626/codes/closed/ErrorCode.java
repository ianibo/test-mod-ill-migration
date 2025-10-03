package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class ErrorCode extends Code {
	// The valid error codes
	public static final String BADLY_FORMED_MESSAGE                = "BadlyFormedMessage";
    public static final String INVALID_CANCEL_VALUE                = "InvalidCancelValue";
    public static final String NO_CANCEL_VALUE                     = "NoCancelValue";
    public static final String NO_ERROR                            = "NoError";
    public static final String NO_XML_SUPPLIED                     = "NoXMLSupplied";
    public static final String NO_CONFIRMATION_ELEMENT_IN_RESPONSE = "NoconfirmationElementInResponse";
	public static final String UNRECONISED_DATA_ELEMENT            = "UnrecognisedDataElement";
	public static final String UNRECONISED_DATA_VALUE              = "UnrecognisedDataValue";
	public static final String UNSUPPORTED_ACTION_TYPE             = "UnsupportedActionType";
	public static final String UNSUPPORTED_REASON_FOR_MESSAGE_TYPE = "UnsupportedReasonForMessageType";

	static {
		add(BADLY_FORMED_MESSAGE);
		add(UNRECONISED_DATA_ELEMENT);
		add(UNRECONISED_DATA_VALUE);
		add(UNSUPPORTED_ACTION_TYPE);
		add(UNSUPPORTED_REASON_FOR_MESSAGE_TYPE);
	}

	public ErrorCode() {
	}

	public ErrorCode(String errorCode) {
		super(errorCode);
	}
}

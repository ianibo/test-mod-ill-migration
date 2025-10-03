package com.k_int.ill;


/**
 * Holds the response of sending a protocol message 
 */
public class ProtocolSendResponse {
	/** The status of the response */
    public String messageStatus;
	
	/** The error if there was one */
	public String errorData = null;
	
	/** The raw data that was returned */
	public String rawData;

	public ProtocolSendResponse(String messageStatus) {
		this.messageStatus = messageStatus;
	}

	public String toString() {
		return(
			"messageStatus: " + messageStatus +
			"\nerrorData: " + errorData +
			"\nrawData: " + rawData
		);
	}
}

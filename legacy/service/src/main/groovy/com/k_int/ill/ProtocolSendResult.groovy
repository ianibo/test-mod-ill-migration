package com.k_int.ill;

import com.k_int.ill.logging.IBaseAuditDetails;

/**
 * Holds the outcome of trying to send a protocol message 
 */
public class ProtocolSendResult {
    /** The result of trying to send the message */
    public ProtocolResultStatus status = ProtocolResultStatus.Error;
	
	// The actual response to the message we sent
	public ProtocolSendResponse response = null;
	
	// The object used for auditing
	public IBaseAuditDetails auditDetails = null;
	
	public String toString() {
		return(
			"Status: " + status.toString() +
			"\nResponse: " + ((response == null) ? "" : response.toString())
		);
	}
}

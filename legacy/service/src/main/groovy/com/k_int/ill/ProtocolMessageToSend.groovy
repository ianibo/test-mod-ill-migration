package com.k_int.ill;

/**
 * Base class for holding the details about a message to be sent
 */
public class ProtocolMessageToSend {
	/** The message to be sent */
	public String message;
	
	public ProtocolMessageToSend(String message) {
		this.message = message;
	}

	public String toString() {
		return("message: " + message);
	}
}

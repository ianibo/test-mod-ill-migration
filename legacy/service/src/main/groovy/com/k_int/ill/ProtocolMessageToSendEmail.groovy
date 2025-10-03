package com.k_int.ill;

/**
 * Class for holding the details about a message to be sent for email
 */
public class ProtocolMessageToSendEmail extends ProtocolMessageToSend {

	/** Subject to be used for the email */
	public String subject;

	/** The request the message is for */
	PatronRequest patronRequest;
	
	public ProtocolMessageToSendEmail(String subject, String body, PatronRequest patronRequest) {
		super(body);
		this.subject = subject;
		this.patronRequest = patronRequest;
	}

	public String toString() {
		return("Request: " + patronRequest.hrid + "\nSubject: " + subject + "\n" + super.toString());
	}
}

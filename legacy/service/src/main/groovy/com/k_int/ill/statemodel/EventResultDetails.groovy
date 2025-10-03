package com.k_int.ill.statemodel;

/**
 * Holds the outcome from processing an event
 */
public class EventResultDetails {
	/** The result of performing the event */
	ActionResult result;

	/** If set this means the status is specifically being over ridden in the code (eg. undo) */
	Status overrideStatus;

	/** The message that will be set in the audit record */
	String auditMessage;

	/** Any data that should be stored with the audit record */
	Map auditData;

	/** Do we add an audit record and Save the request at the end of processing */
	boolean saveData = true;

	/** Contains data that we may want to pass back to the caller */
	Map responseResult = [ : ];

    /** The qualifier for looking up the result record for setting the new status */
    String qualifier;

    /** If a message was sent to the other side of the transaction, this is the sequence number of the message sent */
    Integer messageSequenceNo;

	/** Do we attempt to send a protocol message or not */
	boolean sendProtocolMessage = false;
}

package com.k_int.ill

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class MailQueue implements MultiTenant<MailQueue> {

    // internal ID of the mail queue entry
    String id;

    /** The date time the protocol action was processed */
    Date dateCreated;

    /** The request this queued mail entry belongs to */
    PatronRequest patronRequest;

	/** The email address to send the email to */
	String recipient;

	/** The subject line for the email */
	String subject;

	/** The body of the email */
	String body;

	/** The format of the email, if not set defaults to text/html */ 
	String format;

    static constraints = {
                 body (nullable : false)
          dateCreated (nullable : true) // Because this isn't set until after validation!
               format (nullable : true)
        patronRequest (nullable : true)
            recipient (nullable : false)
              subject (nullable : false)
    }

    static mapping = {
                   id column : 'mq_id', generator : 'uuid2', length : 36
                 body column : 'mq_body', type : 'text'
          dateCreated column : 'mq_date_created'
               format column : 'mq_format', length : 64, defaultValue: "'text/html'"
        patronRequest column : 'mq_patron_request'
            recipient column : 'mq_recipient', length : 256
              subject column : 'mq_subject', length : 256
    }
	
	String toString() {
		return("Id: " + (id == null ? "" : id) + "\n" +
			   "Date_created: " + (dateCreated == null ? "" : dateCreated.toString()) + "\n" +
			   "Patron request: " + (patronRequest == null ? "" : patronRequest.id) + "\n" +
			   "Format: " + format + "\n" +
			   "Recipient: " + (recipient == null ? "" : recipient) + "\n" +
			   "Subject: " + (subject == null ? "" : subject) + "\n" +
			   "Body: " + (body == null ? "" : body)
		);
	}
}

package com.k_int.ill

import org.springframework.web.context.request.RequestContextHolder;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * The documents that have been associated with the request
 */
@ExcludeFromGeneratedCoverageReport
class PatronRequestDocumentAudit implements MultiTenant<PatronRequestDocumentAudit> {

    // internal ID of the audit record
    String id

    /** The date time the record was created, we do not have an updated date, as we do not allow updates */
    Date dateCreated;

	/** The user who accessed the document */
	String userId;

	/** If access was denied to the document. the reason it was denied */ 
	String message;

    /** The request document this audit record belongs to */
    static belongsTo = [patronRequestDocument : PatronRequestDocument];

    static constraints = {
				  dateCreated (nullable : true) // Because this isn't set until after validation!
			 		  message (nullable : true)
			 		   userId (nullable : true)
    }

    static mapping = {
		  		  		   id column : 'prda_id', generator: 'uuid2', length: 36
					  version false
				  dateCreated column : 'prda_date_created'
				  	   userId column : 'prda_user_id', length: 64
				  	  message column : 'prda_message', length: 1024
    }

    def beforeInsert() {
		String userId;
        try {
            userId = RequestContextHolder.currentRequestAttributes().getHeader('X-Okapi-User-Id');
        } catch (Exception e) {
		}

		// If we do not have a user id, then we need to default it
        this.userId = (userId ? userId : "unknown");
    }
}

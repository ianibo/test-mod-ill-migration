package com.k_int.ill

import com.k_int.ill.files.FileDefinition;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * The documents that have been associated with the request
 */
@ExcludeFromGeneratedCoverageReport
class PatronRequestDocument implements MultiTenant<PatronRequestDocument> {

    // internal ID of the document
    String id

    /** The date time the record was created, we do not have an updated date, as we do not allow updates */
    Date dateCreated;

	/** The position of this document to be displayed if there is more than 1 to be displayed */
	int position;
 
    /** The definition of any files that have been uploaded */
    FileDefinition fileDefinition;

	/** For when we have just been supplied a link to the file */
	String uri;

    /** The request this document record belongs to */
    static belongsTo = [patronRequest : PatronRequest]

	static hasMany = [
		audit : PatronRequestDocumentAudit
	];

	static mappedBy = [
		audit: 'patronRequestDocument'
	];

    static constraints = {
      	   dateCreated (nullable : true) // Because this isn't set until after validation!
		 patronRequest (nullable : false)
		fileDefinition (nullable : true)
				   uri (nullable : true)
			  position (unique : 'patronRequest')
    }

    static mapping = {
        			id column : 'prd_id', generator: 'uuid2', length: 36
			   version false
           dateCreated column : 'prd_date_created'
		 patronRequest column : 'prd_patron_request'
			  position column : 'prd_position'
		fileDefinition column : 'prd_file_definition'
        	   	   uri column : 'prd_url', length: 512

				 audit cascade: 'all-delete-orphan'
    }
}

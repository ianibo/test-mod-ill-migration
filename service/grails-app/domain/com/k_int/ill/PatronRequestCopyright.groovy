package com.k_int.ill

import com.k_int.ill.files.FileDefinition;

import grails.gorm.MultiTenant;
import net.bytebuddy.asm.Advice.This
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * The copyright details for a request
 */
@ExcludeFromGeneratedCoverageReport
class PatronRequestCopyright implements MultiTenant<PatronRequestCopyright> {

    // internal ID of the document
    String id

    /** The date time the record was created, we do not have an updated date, as we do not allow updates */
    Date dateCreated;

	/** The reference to a copyright message */
	CopyrightMessage copyrightMessage;
 
    /** The date the copyright was agreed to */
    Date agreedDate;

	/** The user who agtreed to the message */
	String userId;

	/** The copyright text, when explicitly specified */
	String copyrightText;

    /** The request this copyright record belongs to */
    static belongsTo = [patronRequest : PatronRequest]

    static constraints = {
      	     dateCreated (nullable : true) // Because this isn't set until after validation!
		   patronRequest (nullable : false, unique : true)
		      agreedDate (nullable : true)
		copyrightMessage (nullable : true)
				  userId (nullable : true)
		   copyrightText (nullable : true)
    }

    static mapping = {
        			  id column : 'prc_id', generator : 'uuid2', length : 36
				 version false
			 dateCreated column : 'prc_date_created'
		   patronRequest column : 'prc_patron_request'
			  agreedDate column : 'prc_agreed_date'
		copyrightMessage column : 'prc_copyright_message'
        	   	  userId column : 'prc_user_id', length : 64
		   copyrightText column : 'prc_copyright_text', type : 'text'
    }
	
	def beforeUpdate() {
		// if the dateAgreed is set and the userId is not set then set the userId
		if ((this.userId == null) && (this.dateAgreed != null)) {
			try {
				this.userId = RequestContextHolder.currentRequestAttributes().getHeader('X-Okapi-User-Id');
			} catch (Exception e) {
			}
		}
	}
}

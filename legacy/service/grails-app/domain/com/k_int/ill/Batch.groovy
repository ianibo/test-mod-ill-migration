package com.k_int.ill;

import com.k_int.institution.Institution;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class Batch implements MultiTenant<Batch> {

    static public final String CONTEXT_PULL_SLIP = 'pullSlip';

    /** The id of the batch */
    String id;

    /** The description for the batch */
    String description;

    /** The context of the batch */
    String context;

    /** When this batch was created */
    Date dateCreated

    /** Is this a requester or responder batch */
    boolean isRequester;

    /** The institution the item loan policy belongs to */
    Institution institution;

    static hasMany = [patronRequests : PatronRequest]

    static constraints = {
        description (nullable: false, blank: false)
            context (nullable: false, blank: false)
        institution (nullable: true)
    }

    static mapping = {
        table 'batch'
                    id column : 'b_id', generator: 'uuid2', length: 36
               version column : 'b_version'
           description column : 'b_description', length: 256
               context column : 'b_context', length: 32
           dateCreated column : 'b_date_created'
           isRequester column : 'b_is_requester', defaultValue: '1'
           institution column : 'b_institution_id'
        patronRequests column : 'bpr_batch_id', joinTable : 'batch_patron_request'
    }
}


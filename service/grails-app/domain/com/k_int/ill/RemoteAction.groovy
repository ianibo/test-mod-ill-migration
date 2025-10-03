package com.k_int.ill;

import com.k_int.ill.statemodel.ActionEvent;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class RemoteAction implements MultiTenant<RemoteAction> {

    /** The id of the link */
    String id;

	/** The request this link is to action */
	PatronRequest patronRequest;

	/** The rota position this action applies to */
	Long rotaPosition;

    /** The action that is to be performed, on receiving this link */
    ActionEvent actionEvent;

    /** The parameters to pass into the action */
    String parameters;

    /** When this remote link was created */
    Date dateCreated;

    /** When this remote action expires */
    Date expires;

    /** Date accessed */
    Date lastAccessed;

    static constraints = {
        patronRequest (nullable: false)
         rotaPosition (nullable: false)
          actionEvent (nullable: false)
           parameters (nullable: true, blank: false)
          dateCreated (nullable: false)
              expires (nullable: true)
         lastAccessed (nullable: true)
    }

    static mapping = {
        table 'remote_action'
                   id column : 'ra_id', generator: 'uuid2', length: 36
              version false
        patronRequest column : 'ra_patron_request', length: 256
         rotaPosition column : 'ra_rota_position', defaultValue: "0"
          actionEvent column : 'ra_action'
           parameters column : 'ra_parameters'
          dateCreated column : 'ra_date_created'
              expires column : 'ra_expires'
           parameters column : 'ra_parameters', length: 10000
         lastAccessed column : 'ra_last_accessed'
    }
}

package com.k_int.ill;

import com.k_int.institution.Institution;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class NoticeEvent implements MultiTenant<NoticeEvent> {

    String id;
    PatronRequest patronRequest;
    RefdataValue trigger;
    boolean sent;
    Date dateCreated;
    String jsonData;

    /** The institution the event belongs to */
    Institution institution;

    static constraints = {
        patronRequest (nullable: true)
              trigger (nullable: false)
          dateCreated (nullable: true, bindable: false)
             jsonData (nullable: true);
          institution (nullable: false)
    }

    static mapping = {
                   id column : 'ne_id', generator: 'uuid2', length:36
        patronRequest column: 'ne_patron_request_fk'
             jsonData column: "ne_json_data", type: 'text'
              trigger column: 'ne_trigger_fk'
                 sent column: 'ne_sent', defaultValue: false
          institution column: 'ne_institution_id'
          dateCreated column: 'ne_date_created'
              version column: 'ne_version'
    }
}

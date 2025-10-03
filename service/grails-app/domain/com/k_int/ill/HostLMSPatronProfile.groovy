package com.k_int.ill;

import com.k_int.institution.Institution;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class HostLMSPatronProfile implements MultiTenant<HostLMSPatronProfile> {

  String id;
  String code;
  String name;
  Date dateCreated;
  Date lastUpdated;
  Boolean canCreateRequests;

  /** The hidden field if set to true, means they have tried to delete it but it is still linked to another record, so we just mark it as hidden */
  Boolean hidden;

  /** The institution the patron profile belongs to */
  Institution institution;

  static constraints = {
                 code (nullable: false)
                 name (nullable: true)
    canCreateRequests (nullable: true)
               hidden (nullable: true)
          institution (nullable: false, unique: 'code')
          dateCreated (nullable: true, bindable: false)
          lastUpdated (nullable: true, bindable: false)
  }

  static mapping = {
    table 'host_lms_patron_profile'
                               id column : 'hlpp_id', generator: 'uuid2', length:36
                          version column : 'hlpp_version'
                             code column : 'hlpp_code'
                             name column : 'hlpp_name'
                canCreateRequests column : 'hlpp_can_create_requests'
                           hidden column : 'hlpp_hidden', defaultValue: false
                      institution column : 'hlpp_institution_id'
                      dateCreated column : 'hlpp_date_created'
                      lastUpdated column : 'hlpp_last_updated'
  }

  public String toString() {
    return "HostLMSPatronProfile: ${code}".toString()
  }
}

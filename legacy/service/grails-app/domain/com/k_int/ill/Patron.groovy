package com.k_int.ill

import com.k_int.institution.Institution;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class Patron implements MultiTenant<Patron> {

  String id
  String hostSystemIdentifier
  String givenname
  String surname
  Date dateCreated
  Date lastUpdated
  String userProfile

  /** The institution the item loan policy belongs to */
  Institution institution;

  static constraints = {
    hostSystemIdentifier (nullable : false, blank: false)
               givenname (nullable : true,  blank: false)
                 surname (nullable : true,  blank: false)
             userProfile (nullable : true,  blank: false)
             institution (nullable: false, unique: 'hostSystemIdentifier')
  }

  static mapping = {
                      id column : 'pat_id', generator: 'uuid2', length:36
                 version column : 'pat_version'
             dateCreated column : 'pat_date_created'
             lastUpdated column : 'pat_last_updated'
    hostSystemIdentifier column : 'pat_host_system_identifier'
               givenname column : 'pat_given_name'
                 surname column : 'pat_surame'
             userProfile column : 'pat_user_profile'
             institution column : 'pat_institution_id'
  }
}

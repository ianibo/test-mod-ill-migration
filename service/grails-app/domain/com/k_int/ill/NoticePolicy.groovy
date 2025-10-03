package com.k_int.ill;

import com.k_int.institution.Institution;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class NoticePolicy implements MultiTenant<NoticePolicy> {

  String id
  String name
  String description
  Boolean active

  Date dateCreated
  Date lastUpdated

  /** The institution the policy belongs to */
  Institution institution;

  static hasMany = [notices: NoticePolicyNotice];

  static constraints = {
    description (nullable: true)
    dateCreated (nullable: true, bindable: false)
    lastUpdated (nullable: true, bindable: false)
    institution (nullable: false)
  }

  static mapping = {
             id column : 'np_id', generator: 'uuid2', length:36
        version column : 'np_version'
    dateCreated column : 'np_date_created'
    lastUpdated column : 'np_last_updated'
           name column : 'np_name'
    description column : 'np_description'
         active column : 'np_active'
    institution column : 'np_institution_id'
       notices cascade : 'all-delete-orphan'
  }
}

package com.k_int.ill;

import com.k_int.institution.Institution;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class HostLMSItemLoanPolicy implements MultiTenant<HostLMSItemLoanPolicy> {

  String id;
  String code;
  String name;
  Date dateCreated;
  Date lastUpdated;
  boolean lendable = Boolean.TRUE;

  /** The hidden field if set to true, means they have tried to delete it but it is still linked to another record, so we just mark it as hidden */
  boolean hidden = Boolean.FALSE;

  /** The institution the item loan policy belongs to */
  Institution institution;

  static constraints = {
           code (nullable: false)
           name (nullable: true)
       lendable (nullable: false)
         hidden (nullable: false)
    dateCreated (nullable: true, bindable: false)
    lastUpdated (nullable: true, bindable: false)
    institution (nullable: false, unique: 'code')
  }

  static mapping = {
    table 'host_lms_item_loan_policy'
                               id column : 'hlilp_id', generator: 'uuid2', length:36
                          version column : 'hlilp_version'
                             code column : 'hlilp_code'
                             name column : 'hlilp_name'
                         lendable column : 'hlilp_lendable', defaultValue: '1'
                           hidden column : 'hlilp_hidden', defaultValue: '0'
                      institution column : 'hlilp_institution_id'
                      dateCreated column : 'hlilp_date_created'
                      lastUpdated column : 'hlilp_last_updated'
  }

  public String toString() {
    return "HostLMSItemLoanPolicy: ${code}".toString()
  }
}

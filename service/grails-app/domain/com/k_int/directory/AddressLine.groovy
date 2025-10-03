package com.k_int.directory

import com.k_int.web.toolkit.refdata.Defaults;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * Largely inspired by OASIS CIQ TC Standard xAL
  */
@ExcludeFromGeneratedCoverageReport
class AddressLine  implements MultiTenant<AddressLine>  {

  String id
  Long seq
  @Defaults(['AdministrativeArea', 'Country', 'Department', 'Thoroughfare', 'Locality', 'PostalCode', 'PostBox', 'PostOffice', 'PostalCodeOrTown', 'Premise'])
  RefdataValue type
  String value

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static belongsTo = [
    owner: Address
  ]

  static mapping = {
                 id column:'al_id', generator: 'uuid2', length:36
                seq column:'al_seq'
               type column:'al_type_rv_fk'
              value column:'al_value'
  }

  static constraints = {
          seq(nullable:false, blank:false)
         type(nullable:false)
        value(nullable:false, blank:false)

  }
}

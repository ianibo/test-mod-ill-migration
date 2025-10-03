package com.k_int.directory

import com.k_int.web.toolkit.tags.Tag;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class Address  implements MultiTenant<Address>  {

  String id
  String addressLabel

  /*
   * For the purposes of properly internationalisable addresses, this field should be filled with
   * ISO 3166-2 standard country codes, such as DEU or GB-ENG.
   * The AddressLine 'country' would then be filled with 'Deutschland' or 'Germany' depending on sender's locale
   */
  String countryCode

  static hasMany = [
    lines: AddressLine,
    tags:Tag
  ]

  static mappedBy = [
    lines: 'owner'
  ]

  static belongsTo = [
    owner: DirectoryEntry
  ]

  static mapping = {
                 id column:'addr_id', generator: 'uuid2', length:36
       addressLabel column:'addr_label'
        countryCode column: 'addr_country_code'
               tags cascade:'save-update'
              lines cascade:'all-delete-orphan'
  }

  static constraints = {
    addressLabel(nullable:false, blank:false)
           owner(nullable:true)
  }
}

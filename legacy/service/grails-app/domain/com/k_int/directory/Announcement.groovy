package com.k_int.directory

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class Announcement  implements MultiTenant<Announcement>  {

  String id
  Date announceDate
  Date expiryDate
  String code
  String description

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static belongsTo = [
    owner: DirectoryEntry
  ]

  static mapping = {
                 id column:'ann_id', generator: 'uuid2', length:36
              owner column:'ann_owner_fk'
       announceDate column:'ann_announce_date'
         expiryDate column:'ann_expiry_date'
               code column:'ann_code'
        description column:'ann_description'
  }

  static constraints = {
             owner(nullable:true)
      announceDate(nullable:false, blank:false)
        expiryDate(nullable:true, blank:false)
              code(nullable:true, blank:false)
       description(nullable:true, blank:false)

  }
}

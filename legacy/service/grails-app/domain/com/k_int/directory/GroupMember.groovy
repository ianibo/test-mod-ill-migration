package com.k_int.directory

import com.k_int.web.toolkit.custprops.CustomProperties;
import com.k_int.web.toolkit.databinding.BindUsingWhenRef;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;


// Called when data binding wants to bind a variable of type DirectoryEntry to any domain
// class. obj will be an instance of that class, propName will be the property name which has
// type DirectoryEntry and source will be the source map.
@BindUsingWhenRef({ obj, propName, source, isCollection = false ->
  CustomBinders.bindGroupMember(obj, propName, source, isCollection)
})
@ExcludeFromGeneratedCoverageReport
class GroupMember  implements MultiTenant<GroupMember>,CustomProperties  {

  String id
  DirectoryEntry memberOrg

  static graphql = true

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static belongsTo = [
    groupOrg: DirectoryEntry
  ]

  static mapping = {
                 id column:'gm_id', generator: 'uuid2', length:36
           groupOrg column:'gm_group_fk'
          memberOrg column:'gm_member_fk'
  }

  static constraints = {
              groupOrg(nullable:false)
             memberOrg(nullable:false)
  }

}

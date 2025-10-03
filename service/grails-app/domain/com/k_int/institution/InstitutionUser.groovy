package com.k_int.institution;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class InstitutionUser implements MultiTenant<InstitutionUser>, Comparable {

    /** The id of the user */
    String id;

    /** The name for the user */
    String name;

    /** The folio user id */
    String folioUserId;

    /** The institution they are currently managing */
    Institution institutionManaging;

    /** By default we need to ensure the groups are sorted */
    SortedSet institutionGroups;

    static hasMany = [
        institutionGroups : InstitutionGroup
    ]

    static constraints = {
                       name (nullable: false, blank: false)
        institutionManaging (nullable: false)
                folioUserId (nullable: false, blank: false, unique: true)
    }

    static mapping = {
                      table 'institution_user'
                         id column : 'u_id', generator: 'uuid2', length: 36
                    version column : 'u_version'
                       name column : 'u_name', length: 256
        institutionManaging column : 'u_institution_managing', length: 36
                folioUserId column : 'u_folio_user_id', length: 36
          institutionGroups joinTable : [name : 'institution_group_user', key : 'igu_institution_user_id'],
                            cascade : 'save-update'

    }

    public int compareTo(Object object) {
        return(name.toLowerCase().compareTo(object.name.toLowerCase()));
    }
}

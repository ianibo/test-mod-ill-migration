package com.k_int.institution;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class InstitutionGroup implements MultiTenant<InstitutionGroup>, Comparable {

    /** The id of the user */
    String id;

    /** The name for the institution group */
    String name;

    /** The description for the institution group */
    String description;

    /** By default we need to ensure the institution are sorted */
    SortedSet institutions;

    /** By default we need to ensure the users are sorted */
    SortedSet institutionUsers;

    // The group can belong to multiple institutions and users
    static belongsTo = [ Institution, InstitutionUser ];

    static hasMany = [
        institutions : Institution,
        institutionUsers : InstitutionUser
    ]

    static constraints = {
               name (nullable: false, blank: false)
        description (nullable: false, blank: false)
    }

    static mapping = {
                   table 'institution_group'
                      id column : 'ig_id', generator: 'uuid2', length: 36
                 version column : 'ig_version'
                    name column : 'ig_name', length: 256
             description column : 'ig_description', length: 256
        institutionUsers joinTable : [ name : 'institution_group_user', key : 'igu_institution_group_id' ]
            institutions joinTable : [ name : 'institution_group_institution', key : 'igi_institution_group_id' ]
    }

    public int compareTo(Object object) {
        return(name.toLowerCase().compareTo(object.name.toLowerCase()));
    }
}


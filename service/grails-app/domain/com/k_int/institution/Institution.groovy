package com.k_int.institution;

import com.k_int.directory.DirectoryEntry;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class Institution implements MultiTenant<Institution>, Comparable {

    /** The id of the institution */
    String id;

    /** The name for the institution */
    String name;

    /** The description for the institution */
    String description;

    /** The associated directory entry for this institution so we know who to associate incoming requests to */
    DirectoryEntry directoryEntry;

    /** By default we need to ensure the groups are sorted */
    SortedSet institutionGroups;

    static hasMany = [
        institutionGroups : InstitutionGroup
    ]

    static constraints = {
                  name (nullable: false, blank: false, unique: true)
           description (nullable: false, blank: false)
        directoryEntry (nullable: true)
    }

    static mapping = {
                    table 'institution'
                       id column : 'i_id', generator: 'assigned', length: 36
                  version column : 'i_version'
                     name column : 'i_name', length: 256
              description column : 'i_description', length: 256
           directotyEntry column : 'i_directoryEntry', length: 36
        institutionGroups joinTable : [name : 'institution_group_institution', key : 'igi_institution_id'],
                          cascade : 'save-update'
    }

    def beforeValidate() {
        // if we do not have an id allocate it here
        // We do not automatically allocate one as we may create one with a specific id
        if (id == null) {
            // Not already assigned, so assign  a random uuid
            id = UUID.randomUUID().toString();
        }
    }

    public int compareTo(Object object) {
        return(name.toLowerCase().compareTo(object.name.toLowerCase()));
    }
}


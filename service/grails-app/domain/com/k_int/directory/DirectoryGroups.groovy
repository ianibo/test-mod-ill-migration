package com.k_int.directory;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class DirectoryGroups implements MultiTenant<DirectoryGroups> {

    String id;
    String code;
    String description;

    static hasMany = [
        members : DirectoryGroupsMember,
    ];

    static mappedBy = [
         members : 'directoryGroups'
    ];

    static constraints = {
               code (nullable: false, blank: false, unique: true)
        description (nullable: false, blank: false)
    }

    static mapping = {
                 id column: 'dgs_id', generator: 'uuid2', length:36
            version column: 'dgs_version'
               code column: 'dgs_code', length:32
        description column: 'dgs_description'
            members cascade: 'all-delete-orphan', sort: 'rank', order: 'asc'
    }
}

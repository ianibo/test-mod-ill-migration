package com.k_int.directory;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class DirectoryGroup implements MultiTenant<DirectoryGroup> {

    String id;
    String code;
    String description;

    static hasMany = [
        members : DirectoryGroupMember,
    ];

    static mappedBy = [
         members : 'directoryGroup'
    ];

    static constraints = {
               code (nullable: false, blank: false, unique: true)
        description (nullable: false, blank: false)
    }

    static mapping = {
                 id column: 'dg_id', generator: 'uuid2', length:36
            version column: 'dg_version'
               code column: 'dg_code', length:32
        description column: 'dg_description'
            members cascade: 'all-delete-orphan'
    }
}

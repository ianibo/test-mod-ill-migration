package com.k_int.ill;

import com.k_int.institution.Institution;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * Counters to track various system states - Specifically, current loan and borrow levels, but perhaps
 * other things too
 */
@ExcludeFromGeneratedCoverageReport
public class Counter implements MultiTenant<Counter> {

    String id
    String context
    String description
    Long value

    /** The institution the shelving location belongs to */
    Institution institution;

    static constraints = {
            context (nullable : false, blank: false)
        description (nullable : true,  blank: false)
              value (nullable : false)
        institution (nullable: false, unique: 'context')
    }

    static mapping = {
                 id column : 'ct_id', generator: 'uuid2', length:36
            version column : 'ct_version'
            context column : 'ct_context'
        description column : 'ct_description'
              value column : 'ct_value'
        institution column : 'ct_institution_id'
    }
}

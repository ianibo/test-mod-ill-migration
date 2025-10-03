package com.k_int.ill.itemSearch;

import com.k_int.web.toolkit.refdata.RefdataValue;
import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class SearchTree implements MultiTenant<SearchTree> {

    String id;
    String code;
    String description;
	SearchAttribute lhsSearchAttribute;
	SearchTree lhsSearchTree;
	RefdataValue operator;
	SearchAttribute rhsSearchAttribute;
	SearchTree rhsSearchTree;

    static constraints = {
                      code (nullable: false, blank: false, unique: true)
               description (nullable: false, blank: false)
        lhsSearchAttribute (nullable: true)
             lhsSearchTree (nullable: true)
                  operator (nullable: true)
        rhsSearchAttribute (nullable: true)
             rhsSearchTree (nullable: true)
    }

    static mapping = {
                        id column: 'st_id', generator: 'uuid2', length:36
                   version column: 'st_version'
                      code column: 'st_code', length:32
               description column: 'st_description'
        lhsSearchAttribute column: 'st_lhs_search_attribute'
             lhsSearchTree column: 'st_lhs_search_tree'
                  operator column: 'st_operator'
        rhsSearchAttribute column: 'st_rhs_search_attribute'
             rhsSearchTree column: 'st_rhs_search_tree'
    }
}

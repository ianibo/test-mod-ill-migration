package com.k_int.ill.itemSearch;

import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.web.toolkit.refdata.CategoryId;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class Search implements MultiTenant<Search> {

    String id;
    String code;
    String description;
	SearchTree searchTree;
	int maximumHits;
	
	// The host LMS type, that this search is only applicable to
	@CategoryId(RefdataValueData.VOCABULARY_HOST_LMS_INTEGRATION_ADAPTER)
	RefdataValue hostLmsType;
	
	static hasMany = [
		// The host LMS types to be excluded from this search
		excludeHostLmsTypes : SearchExcludeHostLmsType
	];
	
    static constraints = {
                       code (nullable: false, blank: false, unique: true)
                description (nullable: false, blank: false)
                 searchTree (nullable: true)
		        hostLmsType (nullable: true)
    }

    static mapping = {
                         id column: 's_id', generator: 'uuid2', length:36
                    version column: 's_version'
                       code column: 's_code', length:32
                description column: 's_description'
                 searchTree column: 's_search_tree'
                maximumHits column: 's_maximum_hits', defaultValue: "1"
                hostLmsType column: 's_host_lms_type'
		excludeHostLmsTypes cascade: 'all-delete-orphan'
    }
}

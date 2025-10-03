package com.k_int.ill.itemSearch;

import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.web.toolkit.refdata.CategoryId;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class SearchAttribute implements MultiTenant<SearchAttribute> {

    String id;
    String code;
    String description;

	// The search attribute that we are going to use to find the item
    @CategoryId(RefdataValueData.VOCABULARY_SEARCH_ATTRIBUTE)
    RefdataValue attribute;

	// The completeness of the field being searched
    @CategoryId(RefdataValueData.VOCABULARY_SEARCH_COMPLETENESS)
	RefdataValue completeness;
	
	// The position in the field being searched
    @CategoryId(RefdataValueData.VOCABULARY_SEARCH_POSITION)
	RefdataValue position;
	
	// The relationship that we want between the attribute and search attribute
    @CategoryId(RefdataValueData.VOCABULARY_SEARCH_RELATION)
	RefdataValue relation;
	
	// The structure of the search term
    @CategoryId(RefdataValueData.VOCABULARY_SEARCH_STRUCTURE)
	RefdataValue structure;
	
	// How should the attribute being searched be truncated
    @CategoryId(RefdataValueData.VOCABULARY_SEARCH_TRUNCATION)
	RefdataValue truncation;
	
	// The value from the request that is going to be supplied to the search attribute to find the item
    @CategoryId(RefdataValueData.VOCABULARY_SEARCH_ATTRIBUTE_REQUEST)
    RefdataValue requestAttribute;
	
    static constraints = {
                    code (nullable: false, blank: false, unique: true)
             description (nullable: false, blank: false)
               attribute (nullable: false)
            completeness (nullable: true)
                position (nullable: true)
                relation (nullable: true)
               structure (nullable: true)
              truncation (nullable: true)
        requestAttribute (nullable: false)
    }

    static mapping = {
                      id column: 'sa_id', generator: 'uuid2', length:36
                 version column: 'sa_version'
                    code column: 'sa_code', length:32
             description column: 'sa_description'
               attribute column: 'sa_attribute'
            completeness column: 'sa_completeness'
                position column: 'sa_position'
                relation column: 'sa_relation'
               structure column: 'sa_structure'
              truncation column: 'sa_truncation'
        requestAttribute column: 'sa_request_attribute'
    }
}

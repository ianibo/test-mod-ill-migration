package com.k_int.ill.itemSearch;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.k_int.ill.referenceData.RefdataValueData
import com.k_int.web.toolkit.refdata.CategoryId
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * Specifies the Host Lms type not to be used with a search
 *
 * @author Chas
 *
 */
@ExcludeFromGeneratedCoverageReport
class SearchExcludeHostLmsType implements Serializable, MultiTenant<SearchExcludeHostLmsType> {

    /** The search that this record belongs to */
    Search search;

	// The host LMS type, that this search is only applicable to
	@CategoryId(RefdataValueData.VOCABULARY_HOST_LMS_INTEGRATION_ADAPTER)
	RefdataValue hostLmsType;

    static belongsTo = [ search: Search ];

    static constraints = {
             search (nullable: false)
        hostLmsType (nullable: false)
    }

    static mapping = {
                 id composite : [ 'search', 'hostLmsType' ]
            version column : 'sehlt_version'
             search column : 'sehlt_search'
        hostLmsType column : 'sehlt_host_lms_type'
    }

    public boolean equals(other) {
        // If the object is not of the correct type then it can't be equal
        if (!(other instanceof SearchExcludeHostLmsType)) {
            return(false);
        }

        // So if the search and host lms type are the same
        return((other.search.id == search.id) && (other.hostLmsType.id == hostLmsType.id));
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(search?.id == null ? "null" : search.id);
        builder.append(hostLmsType.id);
        return(builder.toHashCode());
    }
}

package com.k_int.ill.itemSearch;

import grails.gorm.MultiTenant;
import org.apache.commons.lang3.builder.HashCodeBuilder
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class SearchGroupEntry implements Serializable, MultiTenant<SearchGroupEntry> {

	SearchGroup searchGroup;
	Search search;
	int rank;
	
    static belongsTo = [ searchGroup: SearchGroup ];

    static constraints = {
        searchGroup (nullable: false)
             search (nullable: false)
               rank (unique: 'searchGroup')
    }

    static mapping = {
                 id composite : [ 'searchGroup', 'search' ]
		    version false
        searchGroup column: 'sge_search_group_id'
             search column: 'sge_search'
               rank column: 'sge_rank'
    }

    public boolean equals(other) {
        // If the object is not of the correct type then it can't be equal
        if (!(other instanceof SearchGroupEntry)) {
            return(false);
        }

        // So are the fields the same
        return((other.searchGroup.id == searchGroup.id) &&
			   (other.search.id == search.id) &&
			   (other.rank == rank));
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(searchGroup?.id == null ? "null" : searchGroup.id);
        builder.append(search.id);
        builder.append(rank);
        return(builder.toHashCode());
    }
}

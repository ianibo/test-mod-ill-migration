package com.k_int.directory;

import grails.gorm.MultiTenant;
import org.apache.commons.lang3.builder.HashCodeBuilder
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class DirectoryGroupsMember implements Serializable, MultiTenant<DirectoryGroupsMember> {

    DirectoryGroups directoryGroups;
    DirectoryGroup directoryGroup;
	int rank;

    static belongsTo = [ directoryGroups: DirectoryGroups ];

    static constraints = {
        directoryGroups (nullable: false, blank: false)
        directoryGroup  (nullable: false, blank: false)
		           rank (unique: 'directoryGroups')
    }

    static mapping = {
                     id composite : [ 'directoryGroups', 'directoryGroup' ]
        directoryGroups column: 'dgsm_directory_groups_id'
         directoryGroup column: 'dgsm_directory_group_id'
                   rank column: 'dgsm_rank'
    }

    public boolean equals(other) {
        // If the object is not of the correct type then it can't be equal
        if (!(other instanceof DirectoryGroupMember)) {
            return(false);
        }

        // So are the fields the same
        return((other.directoryGroups.id == directoryGroups.id) &&
			   (other.directoryGroup.id == directoryGroup.id) &&
			   (other.rank == rank));
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(directoryGroups.id);
        builder.append(directoryGroup.id);
        return(builder.toHashCode());
    }
}

package com.k_int.directory;

import grails.gorm.MultiTenant;
import org.apache.commons.lang3.builder.HashCodeBuilder
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class DirectoryGroupMember implements Serializable, MultiTenant<DirectoryGroupMember> {

    DirectoryGroup directoryGroup;
    DirectoryEntry directoryEntry;

    static belongsTo = [ directoryGroup: DirectoryGroup ];

    static constraints = {
        directoryGroup (nullable: false, blank: false)
        directoryEntry (nullable: false, blank: false)
    }

    static mapping = {
                    id composite : [ 'directoryGroup', 'directoryEntry' ]
        directoryGroup column: 'dgm_directory_group_id'
        directoryEntry column: 'dgm_directory_entry_id'
    }

    public boolean equals(other) {
        // If the object is not of the correct type then it can't be equal
        if (!(other instanceof DirectoryGroupMember)) {
            return(false);
        }

        // So are the fields the same
        return((other.directoryGroup.id == directoryGroup.id) && (other.directoryEntry.id == directoryEntry.id));
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(directoryGroup.id);
        builder.append(directoryEntry.id);
        return(builder.toHashCode());
    }
}

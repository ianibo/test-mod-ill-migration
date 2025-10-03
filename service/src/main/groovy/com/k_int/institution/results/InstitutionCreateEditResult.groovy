package com.k_int.institution.results;

import com.k_int.LabelValue;
import com.k_int.directory.DirectoryEntry;
import com.k_int.institution.InstitutionGroup;

import groovy.transform.CompileStatic;

/**
 * Holds the result details for details required to create or edit an institution
 */
@CompileStatic
public class InstitutionCreateEditResult {

    /** List of groups that can be selected from */
    public List<InstitutionGroupLabelValue> groups = new ArrayList<InstitutionGroupLabelValue>();

    /** The list of directory entries that are institutions */
    public List<DirectoryEntryLabelValue> directoryEntries = new ArrayList<DirectoryEntryLabelValue>();

    public InstitutionCreateEditResult(
        Collection<InstitutionGroup> institutionGroups,
        List<DirectoryEntry> directoryEntries
    ) {
        // Add all the groups that have been supplied
        if (institutionGroups) {
            // Sort by name, before we add them to the list
            institutionGroups.sort{InstitutionGroup institutionGroup ->
                return(institutionGroup.name.toLowerCase());
            }.each { InstitutionGroup institutionGroup ->
                groups.add(new InstitutionGroupLabelValue(institutionGroup));
            }
        }

        // Add all the directory entries that have been supplied
        if (directoryEntries) {
            // Sort by name, before we add them to the list
            directoryEntries.sort{ DirectoryEntry directoryEntry ->
                return(directoryEntry.name.toLowerCase());
            }.each { DirectoryEntry directoryEntry ->
                this.directoryEntries.add(new DirectoryEntryLabelValue(directoryEntry));
            }
        }
    }
}

package com.k_int.institution.results;

import com.k_int.LabelValue;
import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionGroup;

import groovy.transform.CompileStatic;

/**
 * Holds the result details for creating or editing an institution user
 */
@CompileStatic
public class InstitutionUserCreateEditResult {

    /** List of institutions that can be selected from */
    public List<InstitutionLabelValue> institutions = new ArrayList<InstitutionLabelValue>();

    /** The list of institution groups that can be selected from */
    public List<InstitutionGroupLabelValue> groups = new ArrayList<InstitutionGroupLabelValue>();

    /** The list of folio user ids that can selected from */
    public List<FolioUserLabelValue> folioUsers;

    public InstitutionUserCreateEditResult(
        Collection<Institution> institutions,
        Collection<InstitutionGroup> institutionGroups,
        List<FolioUserLabelValue> folioUsers
    ) {
        // Add all the institutions that have been supplied
        if (institutions) {
            // Sort by name, before we add them to the list
            institutions.sort{Institution institution ->
                return(institution.name.toLowerCase());
            }.each { Institution institution ->
                this.institutions.add(new InstitutionLabelValue(institution));
            }
        }

        // Add all the groups that have been supplied
        if (institutionGroups) {
            // Sort by name, before we add them to the list
            institutionGroups.sort{InstitutionGroup institutionGroup ->
                return(institutionGroup.name.toLowerCase());
            }.each { InstitutionGroup institutionGroup ->
                groups.add(new InstitutionGroupLabelValue(institutionGroup));
            }
        }

        // Assign the valid folio user ids
        this.folioUsers = folioUsers;
    }
}

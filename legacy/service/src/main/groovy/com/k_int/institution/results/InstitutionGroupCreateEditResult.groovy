package com.k_int.institution.results;

import com.k_int.LabelValue;
import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionUser;

import groovy.transform.CompileStatic;

/**
 * Holds the result details for creating or editing an institution group
 */
@CompileStatic
public class InstitutionGroupCreateEditResult {

    /** List of institutions that can be selected from */
    public List<InstitutionLabelValue> institutions = new ArrayList<InstitutionLabelValue>();

    /** The list of institution users that can be selected from */
    public List<InstitutionUserLabelValue> users = new ArrayList<InstitutionUserLabelValue>();

    public InstitutionGroupCreateEditResult(
        Collection<Institution> institutions,
        Collection<InstitutionUser> institutionUsers
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

        // Add all the users that have been supplied
        if (institutionUsers) {
            // Sort by name, before we add them to the list
            institutionUsers.sort{InstitutionUser institutionUser ->
                return(institutionUser.name.toLowerCase());
            }.each { InstitutionUser institutionUser ->
                users.add(new InstitutionUserLabelValue(institutionUser));
            }
        }
    }
}

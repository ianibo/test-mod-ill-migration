package com.k_int.institution.results;

import com.k_int.LabelValue;
import com.k_int.institution.Institution;

import groovy.transform.CompileStatic;

/**
 * Holds the result details for the the institutions that can be managed by an institution user
 */
@CompileStatic
public class InstitutionUserManagableInstitutions {

    /** List of institutions that can be selected from */
    public List<InstitutionLabelValue> institutions = new ArrayList<InstitutionLabelValue>();

    /** The institution currently being managed */
    public String currentlyManaging;

    public InstitutionUserManagableInstitutions(String currentlyManaging, Collection<Institution> institutions) {
        // Set the institution currently being managed
        this.currentlyManaging = currentlyManaging;

        // Add all the institutions that have been supplied
        if (institutions) {
            // Sort by name, before we add them to the list
            institutions.sort{Institution institution ->
                return(institution.name.toLowerCase());
            }.each { Institution institution ->
                this.institutions.add(new InstitutionLabelValue(institution));
            }
        }
    }
}

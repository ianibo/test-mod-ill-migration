package com.k_int.institution.results;

import com.k_int.LabelValue;
import com.k_int.institution.Institution;

import groovy.transform.CompileStatic;

@CompileStatic
public class InstitutionLabelValue extends LabelValue {

    public InstitutionLabelValue(Institution institution) {
        super(institution.name, institution.id);
    }
}

package com.k_int.institution.results;

import com.k_int.LabelValue;
import com.k_int.institution.InstitutionGroup;

import groovy.transform.CompileStatic;

@CompileStatic
public class InstitutionGroupLabelValue extends LabelValue {

    public InstitutionGroupLabelValue(InstitutionGroup institutionGroup) {
        super(institutionGroup.name, institutionGroup.id);
    }
}

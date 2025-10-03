package com.k_int.institution.results;

import com.k_int.LabelValue;
import com.k_int.institution.InstitutionUser;

import groovy.transform.CompileStatic;

@CompileStatic
public class InstitutionUserLabelValue extends LabelValue {

    public InstitutionUserLabelValue(InstitutionUser institutionUser) {
        super(institutionUser.name, institutionUser.id);
    }
}

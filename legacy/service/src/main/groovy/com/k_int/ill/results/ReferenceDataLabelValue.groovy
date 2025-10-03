package com.k_int.ill.results;

import com.k_int.LabelValue;
import com.k_int.web.toolkit.refdata.RefdataValue;

import groovy.transform.CompileStatic;

@CompileStatic
public class ReferenceDataLabelValue extends LabelValue {

    public ReferenceDataLabelValue(RefdataValue refDataValue) {
        super(refDataValue.label, refDataValue.id);
    }
}

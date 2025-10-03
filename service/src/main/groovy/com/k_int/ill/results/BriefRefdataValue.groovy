package com.k_int.ill.results;

import com.k_int.ResultIdName;
import com.k_int.web.toolkit.refdata.RefdataValue

import groovy.transform.CompileStatic;

@CompileStatic
public class BriefRefdataValue extends ResultIdName {

    public BriefRefdataValue(RefdataValue refdataValue) {
		super(refdataValue.id, refdataValue.label);
    }
}

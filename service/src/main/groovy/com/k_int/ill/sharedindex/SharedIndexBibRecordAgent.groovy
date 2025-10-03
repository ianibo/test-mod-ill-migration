package com.k_int.ill.sharedindex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedIndexBibRecordAgent {

    public String label;
    public String subtype;

    public SharedIndexBibRecordAgent() {
    }

    public SharedIndexBibRecordAgent(String label, String subtype) {
        this.label = label;
        this.subtype = subtype;
    }
}

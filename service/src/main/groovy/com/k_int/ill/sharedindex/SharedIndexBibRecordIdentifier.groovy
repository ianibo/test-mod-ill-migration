package com.k_int.ill.sharedindex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedIndexBibRecordIdentifier {

    public String namespace;
    public String value;

    public SharedIndexBibRecordIdentifier() {
    }

    public SharedIndexBibRecordIdentifier(String namespace, String value) {
        this.namespace = namespace;
        this.value = value;
    }
}

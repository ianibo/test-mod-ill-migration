package com.k_int.ill.sharedindex;

import groovy.transform.CompileStatic;

@CompileStatic
public class SharedIndexAvailabilityAgency {

    public String code;
    public String description;

    public SharedIndexAvailabilityAgency() {
    }

    public SharedIndexAvailabilityAgency(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

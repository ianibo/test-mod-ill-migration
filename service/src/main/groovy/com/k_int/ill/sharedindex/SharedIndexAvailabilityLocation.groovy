package com.k_int.ill.sharedindex;

import groovy.transform.CompileStatic;

@CompileStatic
public class SharedIndexAvailabilityLocation {

    public String code;
    public String name;

    public SharedIndexAvailabilityLocation() {
    }

    public SharedIndexAvailabilityLocation(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

package com.k_int.ill.sharedindex;

import groovy.transform.CompileStatic;

@CompileStatic
public class SharedIndexAvailabilityStatus {

	static final public String AVAILABLE = "AVAILABLE";
	
    public String code;

    public SharedIndexAvailabilityStatus() {
    }

    public SharedIndexAvailabilityStatus(String code) {
        this.code = code;
    }
}

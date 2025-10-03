package com.k_int.ill.sharedindex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedIndexAvailabilityResult {

    public ArrayList<SharedIndexAvailability> itemList;
    public String clusteredBibId;

    public SharedIndexAvailabilityResult() {
    }

    public SharedIndexAvailabilityResult(String clusteredBibId) {
        this.clusteredBibId = clusteredBibId;
    }
}

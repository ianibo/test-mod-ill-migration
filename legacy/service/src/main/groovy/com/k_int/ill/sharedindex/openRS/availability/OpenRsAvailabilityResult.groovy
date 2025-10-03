package com.k_int.ill.sharedindex.openRS.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.k_int.ill.sharedindex.SharedIndexAvailability;
import com.k_int.ill.sharedindex.SharedIndexAvailabilityResult;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenRsAvailabilityResult {

    public ArrayList<OpenRsAvailability> itemList;
    public String clusteredBibId;

    public OpenRsAvailabilityResult() {
    }

    public SharedIndexAvailabilityResult toSharedIndexAvailabilityResult() {
        SharedIndexAvailabilityResult sharedIndexAvailabilityResult = new SharedIndexAvailabilityResult();
        sharedIndexAvailabilityResult.clusteredBibId = clusteredBibId;
        if (itemList != null) {
            sharedIndexAvailabilityResult.itemList = new ArrayList<SharedIndexAvailability>();
            itemList.each { OpenRsAvailability openRsAvailability ->
                sharedIndexAvailabilityResult.itemList.add(openRsAvailability.toSharedIndexAvailability());
            }
        }
        return(sharedIndexAvailabilityResult);
    }
}

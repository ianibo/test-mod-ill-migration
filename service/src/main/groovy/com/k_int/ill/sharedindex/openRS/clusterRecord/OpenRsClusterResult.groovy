package com.k_int.ill.sharedindex.openRS.clusterRecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.k_int.ill.sharedindex.SharedIndexResult;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenRsClusterResult {

    public long startPosition = 0;
    public long totalHits = 0;
    public long requestedHits = 0;
    public ArrayList<OpenRsClusterRecord> records = new ArrayList<OpenRsClusterRecord>();;

    public OpenRsClusterResult() {
    }

    public SharedIndexResult toSharedIndexResult() {
        SharedIndexResult sharedIndexResult = new SharedIndexResult(startPosition, totalHits, requestedHits);
        records.each { OpenRsClusterRecord openRsClusterRecord ->
            sharedIndexResult.results.add(openRsClusterRecord.toSharedIndexBibRecord());
        }
        return(sharedIndexResult);
    }
}

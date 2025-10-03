package com.k_int.ill.sharedindex.openRS.clusterRecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.k_int.ill.sharedindex.SharedIndexBibRecordIdentifier;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenRsClusterRecordIdentifier extends SharedIndexBibRecordIdentifier{

    public OpenRsClusterRecordIdentifier() {
    }
}

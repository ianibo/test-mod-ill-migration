package com.k_int.ill.sharedindex.openRS.clusterRecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenRsClusterRecordMember {

    public String bibId;
    public String sourceRecordId;
    public String sourceSystem;
    public String title;

    public OpenRsClusterRecordMember() {
    }
}

package com.k_int.ill.sharedindex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedIndexResult {

    public long pageSize = 10;
    public long page = 0;
    public long totalPages = 0;
    public long totalRecords = 0;
    public long getTotal() { return(totalRecords); }

    public ArrayList<SharedIndexBibRecord> results = new ArrayList<SharedIndexBibRecord>();;

    public SharedIndexResult() {
    }

    public SharedIndexResult(
        long startPosition,
        long totalRecords,
        long pageSize
    ) {
        this.totalRecords = totalRecords;
        if (pageSize > -1) {
            this.pageSize = pageSize;
            if ((totalRecords > 0) && (pageSize > 0)) {
                totalPages = (long)((totalRecords + pageSize - 1) / pageSize);
                page = ((long)(startPosition / pageSize)) + 1;
            }
        }
    }
}

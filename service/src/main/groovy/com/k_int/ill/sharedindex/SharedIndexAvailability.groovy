package com.k_int.ill.sharedindex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedIndexAvailability {

    public String id;
    public SharedIndexAvailabilityAgency agency;
	/** The availability date is expect to be in the format "YYYY-MM-DD:hh:mm:ss.millisZ" **/
	public String availabilityDate;
    public String barcode;
    public String callNumber;
    public String canonicalItemType;
    public int holdCount;
    public String hostLmsCode;
    public boolean isRequestable;
	public boolean isSuppressed;
    public String localItemType;
    public String localItemTypeCode;
    public SharedIndexAvailabilityLocation location;
    public SharedIndexAvailabilityStatus status;

    public SharedIndexAvailability() {
    }
}

package com.k_int.ill.sharedindex.openRS.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.k_int.ill.sharedindex.SharedIndexAvailability;
import com.k_int.ill.sharedindex.SharedIndexAvailabilityAgency;
import com.k_int.ill.sharedindex.SharedIndexAvailabilityLocation;
import com.k_int.ill.sharedindex.SharedIndexAvailabilityStatus;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenRsAvailability {

    public String id;
    public OpenRsAvailabilityStatus status;
    public OpenRsAvailabilityLocation location;
	/** The availability date is expect to be in the format "YYYY-MM-DD:hh:mm:ss.millisZ" **/
	public String availabilityDate;
    public String barcode;
    public String callNumber;
    public String hostLmsCode;
    public boolean isRequestable;
    public boolean isSuppressed;
    public int holdCount;
    public String localItemType;
    public String canonicalItemType;
    public String localItemTypeCode;
    public OpenRsAvailabilityAgency agency;

    public OpenRsAvailability() {
    }

    public SharedIndexAvailability toSharedIndexAvailability() {
        SharedIndexAvailability sharedIndexAvailability = new SharedIndexAvailability();
        sharedIndexAvailability.id = id;
        sharedIndexAvailability.availabilityDate = availabilityDate;
        sharedIndexAvailability.barcode = barcode;
        sharedIndexAvailability.callNumber = callNumber;
        sharedIndexAvailability.hostLmsCode = hostLmsCode;
        sharedIndexAvailability.isRequestable = isRequestable;
		sharedIndexAvailability.isSuppressed = isSuppressed;
        sharedIndexAvailability.holdCount = holdCount;
        sharedIndexAvailability.localItemType = localItemType;
        sharedIndexAvailability.canonicalItemType = canonicalItemType;
        sharedIndexAvailability.localItemTypeCode = localItemTypeCode;

        if (agency) {
            sharedIndexAvailability.agency = new SharedIndexAvailabilityAgency(agency.code, agency.description);
        }

        if (location) {
            sharedIndexAvailability.location = new SharedIndexAvailabilityLocation(location.code, location.name);
        }

        if (status) {
            sharedIndexAvailability.status = new SharedIndexAvailabilityStatus(status.code);
        }

        return(sharedIndexAvailability);
    }
}

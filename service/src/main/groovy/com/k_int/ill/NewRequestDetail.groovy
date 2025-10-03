package com.k_int.ill;

import com.k_int.directory.DirectoryEntry;
import com.k_int.ill.results.BriefCopyrightMessage;
import com.k_int.ill.results.BriefRefdataValue;
import com.k_int.web.toolkit.refdata.RefdataValue;

import groovy.transform.CompileStatic;

/**
 * Contains the details required to create a new request
 */
@CompileStatic
public class NewRequestDetail {

    /** The list of requester institutions that the user has access to */
    public List<NewRequestRequesterInstitution> requesterInstitutions;

    /** The list of pickup locations the user has access to */
    public List<NewRequestPickupLocation> pickupLocations;

	/** The list of copyright messages they can use from */
	public List<BriefCopyrightMessage> copyrightMessages;

	/** The possible service types */
	public List<BriefRefdataValue> serviceTypes;
	
    public NewRequestDetail() {
        requesterInstitutions = new ArrayList<NewRequestRequesterInstitution>();
        pickupLocations = new ArrayList<NewRequestPickupLocation>();
		copyrightMessages = new ArrayList<BriefCopyrightMessage>();
		serviceTypes = new ArrayList<BriefRefdataValue>();
    }

    /**
     * Adds a list of directory entries to the requester institution list
     * @param directoryEntries The directory entries that are mapped to requester institutions
     */
    public void addRequesterInstitutions(List<DirectoryEntry> directoryEntries) {
        if (directoryEntries) {
            directoryEntries.each{ DirectoryEntry directoryEntry ->
                NewRequestRequesterInstitution requesterInstitution = new NewRequestRequesterInstitution(directoryEntry);
                if (requesterInstitution.valid()) {
                    requesterInstitutions.add(requesterInstitution);
                }
            }
        }
    }

    /**
     * Adds a list of directory entries to the list of pickup location list
     * @param directoryEntries The directory entries that are mapped onto pickup locations
     */
    public void addPickupLocations(List<DirectoryEntry> directoryEntries) {
        if (directoryEntries) {
            directoryEntries.each{ DirectoryEntry directoryEntry ->
                NewRequestPickupLocation pickupLocation = new NewRequestPickupLocation(directoryEntry);
                if (pickupLocation.valid()) {
                    pickupLocations.add(pickupLocation);
                }
            }
        }
    }

	public void addCopyrightMessages(List<CopyrightMessage> copyrightMessages) {
		if (copyrightMessages) {
			copyrightMessages.each{ CopyrightMessage copyrightMessage ->
				this.copyrightMessages.add(new BriefCopyrightMessage(copyrightMessage));
			}
		}
	}

	public void addServiceTypes(List<RefdataValue> serviceTypes) {
		if (serviceTypes) {
			serviceTypes.each{ RefdataValue serviceType ->
				this.serviceTypes.add(new BriefRefdataValue(serviceType));
			}
		}
	}
}

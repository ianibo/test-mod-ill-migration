package com.k_int.directory;

import com.k_int.CanEdit;
import com.k_int.ill.HostLmsService;
import com.k_int.ill.constants.Directory;
import com.k_int.ill.hostlms.z3950.Z3950HostLms;
import com.k_int.ill.itemSearch.SearchGroup
import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionService;
import com.k_int.web.toolkit.custprops.CustomProperty;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.util.Holders;
import groovy.util.logging.Slf4j;

@Slf4j
/* This service will hold the methods for processing a directory entry DirectoryEntry */
public class DirectoryEntryService {

	/** The query to find the Z3950 service account for the business function RTAC */
	static private final String SERVICE_ACCOUNT_QUERY = '''
select sa
from DirectoryEntry de 
	     inner join de.services as sa
	         inner join sa.service as s
where de.id = :id and
      s.type.label = :type and
      s.businessFunction.label = :businessFunction
''';

    static private final String MANAGED_QUERY = '''
select de
from DirectoryEntry as de
where de.status.value = :managed and
      de.type.value = :type
''';

    static private final String TAG_QUERY = '''
select de
from DirectoryEntry as de
     join de.tags as tag
where tag.value = :tag and
      de.parent in (:parents)
''';

	HostLmsService hostLmsService;

    /** We do not have this injected as it will cause a circular dependency issue, so it is initialised the first time it is needed */
    private InstitutionService institutionService = null;

    boolean directoryEntryIsLending(DirectoryEntry dirEnt) {
        String entry_loan_policy = parseCustomPropertyValue(dirEnt, Directory.KEY_ILL_POLICY_LOAN);

        log.debug("directoryEntry(${dirEnt}) loan_policy : ${entry_loan_policy}");
        return(
            entry_loan_policy == null ||
            entry_loan_policy == Directory.LOAN_POLICY_LENDING_ALL ||
            entry_loan_policy == Directory.LOAN_POLICY_LENDING_PHYSICAL_ONLY
        );
    }

    public String parseCustomPropertyValue(DirectoryEntry dirEnt, String key) {
		String returnVal = null;
		if (dirEnt && key) {
			Object value = dirEnt.customProperties?.value?.find { it.definition.name==key }?.value;
			if (value != null) {
				if (value instanceof RefdataValue) {
					// A RefdataValue has been returned
					returnVal = ((RefdataValue)value).value;
				} else {
					// Just turn the object into a string
					returnVal = value.toString();
				}
			}
		}

		return(returnVal);
	}

    // Methods to parse a string Symbol representation and return the Symbol itself
    public Symbol resolveCombinedSymbol(String combinedString) {
        Symbol result = null;
        if ( combinedString != null ) {
            String[] name_components = combinedString.split(':');
            if ( name_components.length == 2 ) {
                result = resolveSymbol(name_components[0], name_components[1]);
            }
        }
        return result;
    }

    public Symbol resolveSymbol(String authorty, String symbol) {
        Symbol result = null;
        List<Symbol> symbol_list = Symbol.executeQuery('select s from Symbol as s where s.authority.symbol = :authority and s.symbol = :symbol',
                                                       [ authority:authorty?.toUpperCase(), symbol:symbol?.toUpperCase() ]);
        if ( symbol_list.size() == 1 ) {
            result = symbol_list.get(0);
        }

        return result;
    }

    /*
     * DirectoryEntries have a property customProperties of class com.k_int.web.toolkit.custprops.types.CustomPropertyContainer
     * In turn, the CustomPropertyContainer hasMany values of class com.k_int.web.toolkit.custprops.CustomProperty
     * CustomProperties have a CustomPropertyDefinition, where the name lives, so we filter the list to find the matching custprop
     */
    public CustomProperty extractCustomPropertyFromDirectoryEntry(DirectoryEntry de, String cpName) {
        if (!de || ! cpName) {
            return null
        }
        def custProps = de.customProperties?.value ?: []
        CustomProperty cp = (custProps.find {custProp -> custProp.definition?.name == cpName})
        return cp
    }

	/**
	 * Obtains the host lms prefix for this directory entry
	 * @param directoryEntry the directory entry that the lms  is required for
	 * @return The lms used by this directory entry
	 */
	public String getHostLms(DirectoryEntry directoryEntry) {
		// Default to not finding the lms
		String lms = null;
		
		// Have we been supplied a directory entry
		if (directoryEntry != null) {
			if (directoryEntry.determinedHostLms == null) {
				// We have so has the lms been specified on this entry
				if (directoryEntry.hostLmsType == null) {
					// No it hasn't so see if we can get it from the institution for this entry
					DirectoryEntry institution = institutionFor(directoryEntry);
					
					// Did we find an institution
					if (institution != null) {
						// Does the institution have the lms set
						if (institution.hostLmsType != null) {
							// That is good, it is set on the institution
							directoryEntry.determinedHostLms = institution.hostLmsType.value;
						}
					}
				} else {
					// It is held on this entry
					directoryEntry.determinedHostLms = directoryEntry.hostLmsType.value;
				}
			}

			// It has now been determined			
			lms = directoryEntry.determinedHostLms;
		}
		
		// return the lms to the caller if we found one
		return(lms);
	}

	/**
	 * Obtains the search directory groups used for this directory entry
	 * @param directoryEntry the directory entry that the search directory groups is required for
	 * @return The search directory groups used by this directory entry
	 */
	public DirectoryGroups getSearchDirectoryGroups(DirectoryEntry directoryEntry) {
		// Default to not finding the directory groups
		DirectoryGroups directoryGroups = null;
		
		// Have we been supplied a directory entry
		if (directoryEntry != null) {
			// We have so has the search directory groups been specified on this entry
			if (directoryEntry.searchDirectoryGroups == null) {
				// No it hasn't so see if we can get it from the institution for this entry
				DirectoryEntry institution = institutionFor(directoryEntry);
				
				// Did we find an institution
				if (institution != null) {
					// Does the institution have the search directory groups set
					if (institution.searchDirectoryGroups == null) {
						// It does not, so look at the consortium
						DirectoryEntry consortium = consortiumFor(institution);
						
						// Did we find a consortium
						if (consortium != null) {
							// Just set the directory groups to that of the consortium
							directoryGroups = consortium.searchDirectoryGroups;
						}
						
					} else {
						// That is good, it is set on the institution
						directoryGroups = institution.searchDirectoryGroups;
					}
				}
			} else {
				// It is held on this entry
				directoryGroups = directoryEntry.searchDirectoryGroups;
			}
		}
		
		// return the search directory groups we have found to the caller
		return(directoryGroups);
	}
	
	/**
	 * Obtains the Z3950 service for use with the directory entry
	 * @param directoryEntry the directory entry that the z3950 service is required for
	 * @return The z3950 service used by this directory entry
	 */
	public Z3950HostLms getZ3950Service(DirectoryEntry directoryEntry) {
		// Return the z3950 service for the determined lms
		return(hostLmsService.getZ3950HostLms(getHostLms(directoryEntry)));
	}

	/**
	 * Obtains the searches used for this directory entry
	 * @param directoryEntry the directory entry that we want the searches
	 * @return The search directory groups used by this directory entry
	 */
	public SearchGroup getSearches(DirectoryEntry directoryEntry) {
		// Default to not finding the directory groups
		SearchGroup searches = null;
		
		// Have we been supplied a directory entry
		if (directoryEntry != null) {
			// We have so has the searches been specified on this entry
			if (directoryEntry.searches == null) {
				// No it hasn't so see if we can get it from the institution for this entry
				DirectoryEntry institution = institutionFor(directoryEntry);
				
				// Did we find an institution
				if (institution != null) {
					// Does the institution have the searches set
					if (institution.searches == null) {
						// It does not, so look at the consortium
						DirectoryEntry consortium = consortiumFor(institution);
						
						// Did we find a consortium
						if (consortium != null) {
							// Just set the searches to that of the consortium
							searches = consortium.searches;
						}
					} else {
						// That is good, it is set on the institution
						searches = institution.searches;
					}
				}
			} else {
				// It is held on this entry
				searches = directoryEntry.searches;
			}
		}
		
		// return the searches we have found to the caller
		return(searches);
	}

    /**
     * Tries to find the consortium record for the supplied directory entry
     * @param child The directory entry we want to find the consortium for
     * @return The directory entry for the consortium if one was found, otherwise null
     */
    public DirectoryEntry consortiumFor(DirectoryEntry child) {
        return(forType(child, Directory.TYPE_LABEL_CONSORTIUM, Directory.TYPE_VALUE_CONSORTIUM));
    }

    /**
     * Tries to find the institution record for the supplied directory entry
     * @param child The directory entry we want to find the Institution for
     * @return The directory entry for the institution if one was found, otherwise null
     */
    public DirectoryEntry institutionFor(DirectoryEntry child) {
        return(forType(child, Directory.TYPE_LABEL_INSTITUTION, Directory.TYPE_VALUE_INSTITUTION));
    }

    /**
     * Tries to find the parent record for the supplied directory entry
     * @param child The directory entry we want to find the parent type for
     * @param typeLabel The label for the directory entry type
     * @param typeValue The value for the directory entry type
     * @return The directory entry for the parent type if one was found, otherwise null
     */
    private DirectoryEntry forType(DirectoryEntry child, String typeLabel, String typeValue) {

        RefdataValue refdataType = RefdataValue.lookupOrCreate(Directory.CATEGORY_DIRECTORY_ENTRY_TYPE, typeLabel, typeValue);
        DirectoryEntry potentialDirectoryEntry = child;

        // Loop until we find the institution or no longer have a parent
        while ((potentialDirectoryEntry != null) &&
               ((potentialDirectoryEntry.type == null) ||
               (potentialDirectoryEntry.type.id != refdataType.id))) {
            // The current potential entry is not of the required type, so try the parent
            potentialDirectoryEntry = potentialDirectoryEntry.parent;
        }

        // Return with what we bail out of the loop with, which may be null
        return(potentialDirectoryEntry);
    }

    /**
     * Sets the can edit property on each of the directory entries we have been supplied
     * @param directoryEntries The directory entries that we need to set the canEdit property on
     * @param institution The institution the caller belongs to
     */
    public void setCanEdit(List<DirectoryEntry> directoryEntries, Institution institution) {
        // Have we been supplied some entries
        if (directoryEntries) {
            // loop through all the directory entries
            directoryEntries.each { DirectoryEntry directoryEntry ->
                // Set whether we can edit it or not
                directoryEntry.canEdit = canEdit(directoryEntry, institution);
            }
        }
    }

    /**
     * Sets the can edit property on the directory entry
     * @param directoryEntry The directory entry that we need to set the canEdit property on
     * @param institution The institution the caller belongs to
     */
    public void setCanEdit(DirectoryEntry directoryEntry, Institution institution) {
        if (directoryEntry != null) {
            directoryEntry.canEdit = canEdit(directoryEntry, institution);
        }
    }

    /**
     * Determines if we can edit the directory entry or not
     * @param directoryEntry The directory entry that we need to determine if it can be edited or not
     * @param institution The institution the caller belongs to
     * @return Whether it can be edited or not
     */
    public CanEdit canEdit(DirectoryEntry directoryEntry, Institution institution) {
        CanEdit canEdit = CanEdit.No;

        // If we do not have a directory entry we cannot do anything
        if (directoryEntry != null) {
            // Get hold of the institution for this directory entry
            DirectoryEntry institutionEntry = institutionFor(directoryEntry);

            // Now what we do varies on whether multiple institutions is enabled
            if (getInstitutionService().multipleInstitutionsEnabled()) {
                // We have multiple institutions on the system, so does this entry belong to the one on the institution
                if (institutionEntry == null) {
                    // Cannot find an institution, so let them edit it with a warning, not really a legit scenario
                    canEdit = CanEdit.YesWarning;
                } else if (institutionEntry.id.equals(institution.directoryEntry?.id)) {
                    // It belongs to their institution so they can edit it
                    canEdit = CanEdit.Yes;
                } else {
                    // If it belongs to another institution then we cannot edit it
                    if (getInstitutionService().getSpecificInstitutionFor(institutionEntry) == null) {
                        // We can edit it with a warning as it dosn't belong to anoyjer institution on the system
                        canEdit = CanEdit.YesWarning;
                    }
                }
            } else {
                // It can be edited, if it is not managed then we need to give a warning
                canEdit = isManaged(institutionEntry) ? CanEdit.Yes : CanEdit.YesWarning;
            }
        }

        // Return whether we can edit this directory entry record to the caller
        return(canEdit);
    }

    /**
     * Says whether the directory entry is managed or not
     * @param directoryEntry The directory entry to be checked
     * @return true if managed, false if not
     */
    public boolean isManaged(DirectoryEntry directoryEntry) {
        return(com.k_int.ill.constants.Directory.STATUS_VALUE_MANAGED.equals(directoryEntry?.status?.value));
    }

    /**
     * Retrieves the possible requester institutions for the user
     * @param institution The institution the user is associated with
     * @return The list of directory entries that the user can make a request for
     */
    public List<DirectoryEntry> requesterInstitutions(Institution institution) {
        List<DirectoryEntry> directoryEntries = null;

		// When multiple institutions were not enables this returned all the managed institutions by calling managedInstitutions()
		// It is now assumed the directory entry is set against the default institution
        // Fairly simple this, we just use the one associated with the institution record
        directoryEntries = new ArrayList<DirectoryEntry>();
        if (institution.directoryEntry != null) {
            directoryEntries.add(institution.directoryEntry);
        }

        return(directoryEntries);
    }

    /**
     * Fetch all the managed institutions
     * @return All the directory entries for the managed institutions
     */
    public List<DirectoryEntry> managedInstitutions() {
        List<DirectoryEntry> directoryEntries = null;

        // Just lookup all the managed institutions
        Map parameters = [
            managed: com.k_int.ill.constants.Directory.STATUS_VALUE_MANAGED,
            type: com.k_int.ill.constants.Directory.TYPE_VALUE_INSTITUTION
        ];

        directoryEntries = DirectoryEntry.executeQuery(MANAGED_QUERY, parameters);

        return(directoryEntries);
    }

    /**
     * Retrieves the pickup locations for the requesting locations,
     * The assumption made is that the pickup location is 1 level below the institution
     * @param institution The institution the user is associated with
     * @return The list of pickup locations valid for the user
     */
    public List<DirectoryEntry> pickupLocations(List<DirectoryEntry> requesterInstitutions) {
        // In this scenario we need to lookup all the managed institutions
        Map parameters = [
            tag: com.k_int.ill.constants.Directory.TAG_PICKUP,
            parents: requesterInstitutions
        ];

        return(DirectoryEntry.executeQuery(TAG_QUERY, parameters));
    }

    /**
     * Checks whether a directory entry belongs to another
     * @param directoryEntry The directory entry that we want to check against
     * @param directoryEntryToCheck The directory entry we want to see if it belongs to the one to check against
     * @return true if it does, otherwise false
     */
    private boolean belongsTo(DirectoryEntry directoryEntry, DirectoryEntry directoryEntryToCheck) {
        boolean result = false;
        DirectoryEntry childToCheck = directoryEntryToCheck;

        // Nothing to do if we have not been pass a directory entry to check against
        if (directoryEntry != null) {
            // Loop until we have a positive result or we no longer have a child to check
            while (!result && (childToCheck != null)) {
                // Do the identifiers match
                if (directoryEntry.id == childToCheck.id) {
                    // They do so we have a positive result
                    result = true;
                } else {
                    // They do not, so look at the parent
                    childToCheck = childToCheck.parent;
                }
            }
        }

        // Let the caller know the result
        return(result);
    }

    private InstitutionService getInstitutionService() {
        // Have we obtained it yet
        if (institutionService == null) {
            // Not yet, so get hold of it
            institutionService = Holders.grailsApplication.mainContext.getBean('institutionService');
        }

        // Return it to the caller
        return(institutionService);
    }

	/**
	 * Retrieves the Z3950 RTAC account if there is one for a directory entry	
	 * @param directoryEntry the directory entry we want the account for
	 * @return The service account if there is only one found
	 */
	public ServiceAccount getServiceZ3950RtacAccount(DirectoryEntry directoryEntry) {
		ServiceAccount serviceAccount = null;

		// Build up the parameter map
		Map parameters =  [
			id : directoryEntry.id,
			type : Directory.SERVICE_TYPE_Z3950,
			businessFunction : Directory.SERVICE_BUSINESS_FUNCTION_RTAC
		];

		// Execute the query on the service account to find the z3950 one
		List<ServiceAccount> results = ServiceAccount.executeQuery(SERVICE_ACCOUNT_QUERY, parameters);

		// Only interested in 1 result
		if (results.size() == 1) {
			serviceAccount = results[0];
		}

		// If we foubd a result return it to the caller
		return(serviceAccount);
	}
}

package com.k_int.institution;

import com.k_int.OperationResult;
import com.k_int.ResultIdName;
import com.k_int.directory.DirectoryEntry;
import com.k_int.directory.DirectoryEntryService;
import com.k_int.directory.Symbol;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.institution.results.InstitutionCreateEditResult;
import com.k_int.institution.results.InstitutionUserResult;
import com.k_int.settings.SystemSettingsService

/**
 * Provides the necessary methods for interfacing with the Institution domain
 * @author Chas
 *
 */
public class InstitutionService extends InstitutionGroupUtils<Institution> implements IInstitutionModifyGroup {

    private static final long thirtyMinutes = 30 * 60 * 1000;
    private static long nextCheckEnabled = System.currentTimeMillis();
    private static boolean multipleInstitutionsEnabled = false;

    private static final String USERS_QUERY = """
select iu as user, ig as group
from Institution as i
        inner join i.institutionGroups ig
        inner join ig.institutionUsers iu
where i.id = :institutionId
order by iu.id, ig.id
""";

    private static final int POSITION_USERS_USER  = 0;
    private static final int POSITION_USERS_GROUP = 1;

    DirectoryEntryService directoryEntryService;
    SystemSettingsService systemSettingsService;

    /**
     * Ensures Ensures the supplies details for an institution exists
     * @param id The id for an institution if null then one will be generated
     * @param name The name of the institution
     * @param description A descriptoin for the institution
     * @return The institution found or newly created one if it was not found
     */
    public Institution ensure(
        String id,
        String name,
        String description
    ) {
        Institution institution = null;
        if (id == null) {
            // Look up the institution by its name
            institution = Institution.findByName(name);
        } else {
            // Try and look it up by id
            institution = Institution.get(id);
        }

        // If we did not find an institution create a new one
        // Note, We do not update it as it will probably have been updated to suit the implementation
        if (institution == null) {
            institution = new Institution();
            institution.id = id;
            institution.name = name;
            institution.description = description;
            institution.save(flush: true);
        }

        // Finally return the institution to the caller
        return(institution);
    }

    /**
     * Get hold of the institution for the patron defined in the user details
     * @param userId The id of the user that we want the institution for
     * @return The institution the user belongs to
     */
    public Institution getInstitution(String userId) {
        Institution institution  = null;

        // Are multiple institutions configured
        if (multipleInstitutionsEnabled()) {
            // Have we been supplied with user details
            if (userId == null) {
                // We do not have a user, how have they got this far ...
                institution = getDefaultInstitution();
                log.error("getPatron returned null, so we are unable to determine the institution, so using the default");
            } else {
                // Now get hold of the institution this patron belongs to
                InstitutionUser institutionUser = InstitutionUser.findByFolioUserId(userId);
                if (institutionUser) {
                    institution = institutionUser.institutionManaging;
                    if (institution == null) {
                        // Find the first institution they have access to
                        if (institutionUser.institutionGroups.size() > 0) {
                            institutionUser.institutionGroups.each { InstitutionGroup institutionGroup ->
                                if (institutionGroup.institutions.size() > 0) {
                                    // Set the institution to the first one
                                    institution = institutionGroup.institutions[0];
                                }
                            }
                        }

                        // If we still do not have an institution use the default
                        if (institution == null) {
                            // Last resort of finding an institution
                            institution = getDefaultInstitution();
                        }
                    }
                    log.debug("Found institution " + institution.id + " for patron " + userId);
                } else {
                    // User not associated with an institution
                    institution = getDefaultInstitution();
                    log.info("User " + userId + " is not associated with an institution, so using the default");
                }
            }
        } else {
            // They are not, so set to the default
            institution = getDefaultInstitution();
        }

        // Return the found institution to the caller
        return(institution);
    }

    /**
     * Get hold of the institution for the supplied directory entry
     * @param directoryEntry The directory entry we want the institution for
     * @return The institution associated with the supplied directory entry
     */
    public Institution getInstitution(DirectoryEntry directoryEntry) {
        Institution institution  = null;

        // Are multiple institutions configured
        if (multipleInstitutionsEnabled()) {
            // Call the directory entry service to find the institution directory entry
            DirectoryEntry institutionDirectoryEntry = directoryEntryService.institutionFor(directoryEntry);

            // Did we find an institution directory entry
            if (institutionDirectoryEntry == null) {
                // We did not so pass back the default institution
                institution = getDefaultInstitution();
            } else {
                // Look up the institution record and find the first that matches
                institution = getSpecificInstitutionFor(institutionDirectoryEntry);
                if (institution == null) {
                    // There isn't one configured so set it to the default
                    institution = getDefaultInstitution();
                }
            }
        } else {
            // They are not, so set to the default
            institution = getDefaultInstitution();
        }

        // Return the found institution to the caller
        return(institution);
    }

    /**
     * Returns the institution for the supplied directory entry
     * @param institutionDirectoryEntry The institution directory entry
     * @return The institution if one is found otherwise null
     */
    public Institution getSpecificInstitutionFor(DirectoryEntry institutionDirectoryEntry) {
        return(Institution.findByDirectoryEntry(institutionDirectoryEntry));
    }

    /**
     * Given a symbol returns the appropriate institution
     * @param symbol The symbol we want the institution for
     * @return The determined institution
     */
    public Institution getInstitutionForSymbol(String symbol) {
        Institution institution = null;
        Symbol directorySymbol = directoryEntryService.resolveCombinedSymbol(symbol);
        if (directorySymbol == null) {
            institution = getDefaultInstitution();
        } else {
            institution = getInstitution(directorySymbol.owner);
        }

        return(institution);
    }

    /**
     * Are multiple institutions enabled on this system, we only check the database every 30 minutes
     * @return true if they are, false if not
     */
    public boolean multipleInstitutionsEnabled() {
        // Is it time to check the database, it is not a setting that will change that often, probably only during setup of the tenant
        if (nextCheckEnabled < System.currentTimeMillis()) {
            multipleInstitutionsEnabled = systemSettingsService.hasRefDataValue(SettingsData.SETTING_INSTITUTION_MULTIPLE_ENABLED, RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_YES);
            nextCheckEnabled += thirtyMinutes;
        }
        return(multipleInstitutionsEnabled);
    }

    /**
     * Checks whether the record the identifier represents is valid for the institution
     * @param domainClass The domain class that we need to lookup the record
     * @param identifier The identifier that is used to lookup the record in the domain class
     * @param institutionId The id of the instition that the record needs to belong to
     * @return true if the identifier belongs to the institution otherwise false
     */
    public boolean isValidForinstitution(Class domainClass, String identifier, String institutionId ) {
        boolean isValid = true;

        // Have we been supplied an identifier
        if (identifier) {
            // Try and get hold of the record, we should cache the get method for this domain class
			def record = domainClass.get(identifier);
			if (record == null) {
				// We have failed to find the get method for the domain class, which is problematic
				log.error("Failed to find get method for domain class");
				isValid = false;
			} else {
	            // Do the institution ids match
	            isValid = institutionId.equals(record?.institution?.id);
			}
        } else {
            // No id so obviously dosn't match
            isValid = false;
        }

        // Return whether this identifier belongs to the institution
        return(isValid);
    }

    /**
     * Returns a list of identifiers from the list supplied that belong to the institution supplied
     * @param domainClass The domain class that the identifiers belong to
     * @param identifiers The identifiers that need to be checked
     * @param institutionId The identifier for the institution that we need to check that they belong to
     * @return The list of valid identifiers
     */
    public List validateForInstitution(Class domainClass, List identifiers, String institutionId ) {
        List<String> validIdentifiers;

        // Are multiple institutions enabled
        if (multipleInstitutionsEnabled()) {
            // They are, so we need to verify all the identifiers are valid for this institution
            validIdentifiers = [ ];

            // Have we been supplied any identifiers
            if ((identifiers != null) && (identifiers.size() > 0)) {
                // Loop through the identifiers checking that the record belongs to the institution
                identifiers.each { String identifier ->
                    // Is it a valid identifier
                    if (isValidForinstitution(domainClass, identifier, institutionId)) {
                        // It is, so add it to array of valid identifiers
                        validIdentifiers.add(identifier);
                    }
                }
            }
        } else {
            // They are not, so all identifiers are valid
            validIdentifiers = identifiers;
        }

        // Return the list of valid identifiers
        return(validIdentifiers);
    }

    /**
     * Retrieves the default institution
     * @return The default institution for the system
     */
    public Institution getDefaultInstitution() {
        // We fully qualify the path so we do not conflict with the Institution domain
        return(Institution.get(com.k_int.ill.constants.Institution.DEFAULT_INSTITUTION));
    }

    /**
     * Adds or removes the supplied groups from the specified institution
     * @param id The institution id that the groups need to be added to or removed from
     * @param groupIds The list of group identifiers that need to be added or removed
     * @param add true if the groups are to be added, false if they are to be removed
     * @return The Result object containing the details of what happened
     */
    public OperationResult modifyGroups(String id, List<String> groupIds, boolean add) {
        // Just call the base class
        return(modifyGroups(Institution, id, groupIds, add));
    }

    /**
     * Fetches the users who have access to the specified institution
     * @param institutionId The institution we want the users for
     * @return The users and the groups through which they have access to this institution
     */
    public List<InstitutionUserResult> users(String institutionId) {
        List<InstitutionUserResult> result = new ArrayList<InstitutionUserResult>();
        InstitutionUserResult currentUser = null;
        Institution.executeQuery(USERS_QUERY, [ institutionId: institutionId ]).each { row ->
            InstitutionUser instititionUser = row[POSITION_USERS_USER];
            InstitutionGroup instititionGroup = row[POSITION_USERS_GROUP];

            // Is this a new user
            if ((currentUser == null) || (currentUser.user.id != instititionUser.id)) {
                // It is, if we had a previous user, sort the groups by name
                if (currentUser != null) {
                    // Sort by name
                    currentUser.groups = currentUser.groups.sort { group -> group.name.toLowerCase() };
                }

                // Create ourselves a new user object
                currentUser = new InstitutionUserResult(instititionUser.id, instititionUser.name);
                if (instititionUser.institutionManaging != null) {
                    currentUser.institutionManaging = new ResultIdName(
                        instititionUser.institutionManaging.id,
                        instititionUser.institutionManaging.name
                    );
                }

                // Add this user to the result
                result.add(currentUser);
            }

            // Add the group to the current user
            currentUser.groups.add(new ResultIdName(instititionGroup.id, instititionGroup.name));
        };

        // Finally sort the groups by name for the current user as that would have not been sorted
        if (currentUser != null) {
            // Sort by name
            currentUser.groups = currentUser.groups.sort { group -> group.name.toLowerCase() };
        }

        // Return the users sorted by name
        return(result.sort { user -> user.user.name.toLowerCase() });
    }

    /**
     * Retrieves the details required for creating or editing an institution
     * @return A InstitutionCreateEditResult object that contains all the details required to create or  edit an institution
     */
    public InstitutionCreateEditResult detailsForCreateEdit() {
        Collection<InstitutionGroup> groups = InstitutionGroup.findAll();
        List<DirectoryEntry> directoryEntries = directoryEntryService.managedInstitutions();
        return(new InstitutionCreateEditResult(groups, directoryEntries));
    }
}

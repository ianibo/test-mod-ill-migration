package com.k_int.institution;

import com.k_int.OperationResult;
import com.k_int.ill.constants.ErrorCodes;

/**
 * Provides genric methods for associating institution domains with an institution group
 * @author Chas
 *
 */
public class InstitutionGroupUtils<TDomain> {

    /**
     * Adds or removes the supplied groups from the domain
     * @param domainClass A Class object that represents the generic TDomain class
     * @param id The id for the domain that the groups need to be added to or removed from
     * @param groupIds The list of group identifiers that need to be added or removed
     * @param add true if the groups are to be added, false if they are to be removed
     * @param domainTextName The domain name text to be appended to institution for message output
     * @return The OperationResult object containing the details of what happened
     */
    protected OperationResult modifyGroups(
        Class<TDomain> domainClass,
        String id,
        List<String> groupIds,
        boolean add,
        String domainTextName = null
    ) {
        OperationResult result = new OperationResult(id);
        String domainText = domainTextName ? (domainTextName + " ") : "";

        // Have we been supplied an id
        if (id) {
            // Is it for the domain
            TDomain domainObject = domainClass.get(id);
            if (domainObject == null) {
                result.error("Institution " + domainText + "with id \"" + id + "\" does not exist", ErrorCodes.RECORD_NOT_FOUND);
            } else {
                // Have we been supplied any group ids
                if (groupIds) {
                    int successful = 0;
                    int failed = 0;
                    int noOperation = 0;

                    // Process each of the groups
                    groupIds.each { String groupId ->
                        // Is it a valid group
                        InstitutionGroup institutionGroup = InstitutionGroup.get(groupId);
                        if (institutionGroup == null) {
                            result.addMessage("Group with id \"" + groupId + "\" does not exist");
                            failed++;
                        } else {
                            if (domainObject.institutionGroups.contains(institutionGroup)) {
                                if (add) {
                                    result.addMessage("Institution " + domainText + "is already a member of group \"" + institutionGroup.name + "\"");
                                    noOperation++;
                                } else {
                                    domainObject.removeFromInstitutionGroups(institutionGroup);
                                    result.addMessage("Institution " + domainText + "removed from group \"" + institutionGroup.name + "\"");
                                    successful++;
                                }
                            } else {
                                if (add) {
                                    domainObject.addToInstitutionGroups(institutionGroup);
                                    result.addMessage("Added institution " + domainText + "to group \"" + institutionGroup.name + "\"");
                                    successful++;
                                } else {
                                    result.addMessage("Institution " + domainText + "is not a member of group \"" + institutionGroup.name + "\"");
                                    noOperation++;
                                }
                            }
                        }
                    }

                    // Save the record
                    domainObject.save(flush: true, failOnError: true);

                    // Give a summary of the the outcome
                    if (add) {
						result.setOperationAdd();
                    } else {
						result.setOperationRemove();
                    }
                    result.setSuccessful(successful);
                    result.setFailed(failed);
                    result.setNoOperation(noOperation);
                    result.addMessage("Summary: Successful: " + successful + ", failed: " + failed + ", no operation: " + noOperation);
                } else {
                    result.error("No group ids supplied", ErrorCodes.INSTITUTION_NO_GROUP_IDS);
                }
            }
        } else {
            result.error("No institution " + domainText + "id supplied", ErrorCodes.ID_NOT_SUPPLIED);
        }

        // return the result to the caller
        return(result);
    }

    /**
     * Removes all the groups ffrom the domain except for the ones with the supplied ids
     * @param domainClass A Class object that represents the generic TDomain class
     * @param id The identifer for the domain we want to remove the groups from
     * @param groupIds The id of the groups we want to keep
     * @param domainTextName The domain name text to be appended to institution for message output
     * @return The OperationResult object containing the details of what happened
     */
    protected OperationResult removeGroupsExcept(
        Class<TDomain> domainClass,
        String id,
        List<String> groupIds,
        String domainTextName = null
    ) {
        OperationResult result = new OperationResult(id);
        String domainText = domainTextName ? (domainTextName + " ") : "";

        // Have we been supplied an id
        if (id) {
            // Is it for the domain
            TDomain domainObject = domainClass.get(id);
            if (domainObject == null) {
                result.error("Institution " + domainText + "with id \"" + id + "\" does not exist", ErrorCodes.RECORD_NOT_FOUND);
            } else {
                List<String> idsToRemove = new ArrayList<String>();

                // Iterate through all the groups
                domainObject.institutionGroups.each { InstitutionGroup institutionGroup ->
                    if (!groupIds.contains(institutionGroup.id)) {
                        // It is a group we need to remove
                        idsToRemove.add(institutionGroup.id);
                    }
                }

                // Did we find any to remove
                if (idsToRemove.isEmpty()) {
                    result.addMessage("No groups to be removed");
                } else {
                    // Lets remove them
                    result = modifyGroups(domainClass, id, idsToRemove, false,  domainTextName);
                }
            }
        } else {
            result.error("No institution " + domainText + "id supplied", ErrorCodes.ID_NOT_SUPPLIED);
        }

        // return the result to the caller
        return(result);
    }
}

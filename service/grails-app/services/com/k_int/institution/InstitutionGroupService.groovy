package com.k_int.institution;

import com.k_int.GenericResult;
import com.k_int.OperationResult;
import com.k_int.ill.constants.ErrorCodes;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.institution.results.InstitutionGroupCreateEditResult;

/**
 * Provides the necessary methods for interfacing with the Institution User domain
 * @author Chas
 *
 */
public class InstitutionGroupService extends InstitutionGroupUtils<InstitutionUser> {

    InstitutionService institutionService;
    InstitutionUserService institutionUserService;

    /**
     * Adds or removes the supplied institutions from the specified group
     * @param id The group id that the institutions need to be added to or removed from
     * @param institutions The list of institution identifiers that need to be added or removed
     * @param add true if the groups are to be added, false if they are to be removed
     * @return The OperationResult object containing the details of what happened
     */
    public OperationResult modifyInstitutions(String id, List<String> institutionIds, boolean add) {
        // We have a generic method for modifying group membership
        return(modifyGroups(id, institutionService, institutionIds, add));
    }

    /**
     * Adds or removes the supplied users from the specified group
     * @param id The group id that the users need to be added to or removed from
     * @param userIds The list of user identifiers that need to be added or removed
     * @param add true if the groups are to be added, false if they are to be removed
     * @return The OperationResult object containing the details of what happened
     */
    public OperationResult modifyUsers(String id, List<String> userIds, boolean add) {
        // We have a generic method for modifying group membership
        return(modifyGroups(id, institutionUserService, userIds, add));
    }

    /**
     * Adds or removes resources to the group
     * @param groupId The id of the group it needs to be added to
     * @param modifyGroupService The service that will do the real work
     * @param ids The identifiers that need to be added to or removed from the group
     * @param add true if the groups are to be added, false if they are to be removed
     * @return The OperationResult object containing the details of what happened
     */
    private OperationResult modifyGroups(
        String groupId,
        IInstitutionModifyGroup modifyGroupService,
        List<String> ids,
        boolean add
    ) {
        OperationResult result = new OperationResult(groupId);

        // Have we been supplied a group id
        if (groupId) {
            // At a minimum we have at least been supplied with an id
            if (ids) {
                // Even better we have some user ids
                List<String>groupIds = [ groupId ];

                // Step through each of the ids to be associated with the group
                ids.each { String id ->
                    // Now let the service modify the group as it will be the parent
                    OperationResult serviceResult = modifyGroupService.modifyGroups(id, groupIds, add);
                    if (serviceResult.result == ActionResult.ERROR) {
                        // We hit an error, so set our result to error
                        result.result = serviceResult.result;
                    }

                    // Copy across any messages
                    result.messages.addAll(serviceResult.messages);

                    // Take into account the counts
                    if (result.responseResult.successful == null) {
                        // Just take everything returned
                        result.responseResult = serviceResult.responseResult;
                    } else if (serviceResult.responseResult != null) {
                        // Increase the results accordingly
                        result.responseResult.successful += serviceResult.responseResult.successful;
                        result.responseResult.failed += serviceResult.responseResult.failed;
                        result.responseResult.noOperation += serviceResult.responseResult.noOperation;
                    }
                }
            } else {
                result.error("No ids supplied to associate with the group", ErrorCodes.IDS_NOT_SUPPLIED);
            }
        } else {
            // No group id supplied
            result.error("No group id supplied", ErrorCodes.ID_NOT_SUPPLIED);
        }

        // Return the result to the caller
        return(result);
    }

    /**
     * Remove the institutions associated with the group, except for the list supplied
     * @param groupId The group to remove the institutions from
     * @param institutionIds The institution ids not to be removed
     * @return The GenericResult object containing the details of what happened
     */
    public GenericResult removeInstitutionsExceptFor(
        String groupId,
        List<String> institutionIds
    ) {
        GenericResult result = new GenericResult(groupId);

        // Must have a group id
        if (groupId) {
            List<String> idsToRemove = new ArrayList<String>();
            InstitutionGroup institutionGroup = InstitutionGroup.get(groupId);
            if (institutionGroup == null) {
                // No group id supplied
                result.error("Group with id \"" + groupId + "\" does not exist", ErrorCodes.RECORD_NOT_FOUND);
            } else {
                List<String> institutionsToRemove = new ArrayList<String>();
                institutionGroup.institutions.each { Institution institution ->
                    // if it is not in our exception list, add it to the list to be removed
                    if (!institutionIds.contains(institution.id)) {
                        institutionsToRemove.add(institution.id);
                    }
                }

                // Did we find any to remove
                if (institutionsToRemove.isEmpty()) {
                    result.addMessage("No institutions to be removed");
                } else {
                    result = modifyGroups(groupId, institutionService, institutionsToRemove, false);
                }
            }
        } else {
            // No group id supplied
            result.error("No group id supplied", ErrorCodes.ID_NOT_SUPPLIED);
        }

        // Return the result to the caller
        return(result);
    }

    /**
     * Remove the users associated with the group, except for the list supplied
     * @param groupId The group to remove the users from
     * @param userIds The institution ids not to be removed
     * @return The GenericResult object containing the details of what happened
     */
    public GenericResult removeUsersExceptFor(
        String groupId,
        List<String> userIds
    ) {
        GenericResult result = new GenericResult(groupId);

        // Must have a group id
        if (groupId) {
            List<String> idsToRemove = new ArrayList<String>();
            InstitutionGroup institutionGroup = InstitutionGroup.get(groupId);
            if (institutionGroup == null) {
                // No group id supplied
                result.error("Group with id \"" + groupId + "\" does not exist", ErrorCodes.RECORD_NOT_FOUND);
            } else {
                List<String> usersToRemove = new ArrayList<String>();
                institutionGroup.institutionUsers.each { InstitutionUser institutionUser ->
                    // if it is not in our exception list, add it to the list to be removed
                    if (!userIds.contains(institutionUser.id)) {
                        usersToRemove.add(institutionUser.id);
                    }
                }

                // Did we find any to remove
                if (usersToRemove.isEmpty()) {
                    result.addMessage("No users to be removed");
                } else {
                    result = modifyGroups(groupId, institutionUserService, usersToRemove, false);
                }
            }
        } else {
            // No group id supplied
            result.error("No group id supplied", ErrorCodes.ID_NOT_SUPPLIED);
        }

        // Return the result to the caller
        return(result);
    }

    /**
     * Retrieves the details required for creating or editing an institution group
     * @return A InstitutionGroupCreateEditResult object that contains all the details required to create or edit an institution group
     */
    public InstitutionGroupCreateEditResult detailsForCreateEdit() {
        Collection<Institution> institutions = Institution.findAll();
        Collection<InstitutionUser> institutionUsers = InstitutionUser.findAll();
        return(new InstitutionGroupCreateEditResult(institutions, institutionUsers));
    }
}

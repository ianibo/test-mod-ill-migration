package com.k_int.institution;

import com.k_int.GenericResult;
import com.k_int.OperationResult;
import com.k_int.ResultIdName;
import com.k_int.ill.constants.ErrorCodes;
import com.k_int.institution.results.InstitutionResult;
import com.k_int.institution.results.InstitutionUserCreateEditResult;
import com.k_int.institution.results.InstitutionUserManagableInstitutions;
import com.k_int.okapi.OkapiPermissionService;
import com.k_int.okapi.OkapiUserService;

/**
 * Provides the necessary methods for interfacing with the Institution User domain
 * @author Chas
 *
 */
public class InstitutionUserService extends InstitutionGroupUtils<InstitutionUser> implements IInstitutionModifyGroup {

    static private final String INSTITUTIONS_FOR_USER_QUERY = '''
select distinct i
from InstitutionUser as iu
     join iu.institutionGroups as ig
        join ig.institutions as i
where iu.folioUserId = :userId
group by i
''';

static private final String USER_QUERY = '''
select iu
from InstitutionUser as iu
where iu.folioUserId = :userId
''';

static private final String USER_INSTITUTION_QUERY = '''
select distinct i
from InstitutionUser as iu
     join iu.institutionGroups as ig
        join ig.institutions as i
where iu.id = :userId and
      i.id = :institutionId
group by i
''';

private static final String INSTITUTION_AND_GROUP_QUERY = """
select i as institution, ig as group
from InstitutionUser as iu
        inner join iu.institutionGroups ig
        inner join ig.institutions i
where iu.id = :userId
order by i.id, ig.id
""";

    private static final int POSITION_INSTITUTIONS_INSTITUTION = 0;
    private static final int POSITION_INSTITUTIONS_GROUP       = 1;

    OkapiPermissionService okapiPermissionService;
    OkapiUserService okapiUserService;

    /**
     * Returns the list of institutions that can be managed by the supplied user
     * @param institutionId The id of the institution currently being managed
     * @param userId The id of the user that we want the list of institutions they are managing
     * @return The list of institutions this user can manage
     */
    public InstitutionUserManagableInstitutions canManage(String institutionId, String userId) {
        List<Institution> institutions = InstitutionUser.executeQuery(INSTITUTIONS_FOR_USER_QUERY, [ userId: userId ]);

        // Return the institutions
        return(new InstitutionUserManagableInstitutions(institutionId, institutions));
    }

    /**
     * Adds or removes the supplied groups from the specified institution
     * @param id The institution id that the groups need to be added to or removed from
     * @param groupIds The list of group identifiers that need to be added or removed
     * @param add true if the groups are to be added, false if they are to be removed
     * @return The OperationResult object containing the details of what happened
     */
    public OperationResult modifyGroups(String id, List<String> groupIds, boolean add) {
        // Just call the base class
        return(modifyGroups(InstitutionUser, id, groupIds, add, "user"));
    }

    public GenericResult manageInstitution(String userId, String institutionId) {
        GenericResult result = new GenericResult(userId);

        // Must have a user id
        if (userId) {
            // Is it valid
            InstitutionUser institutionUser = getInstitutionUser(userId);
            if (institutionUser == null) {
                result.error("\"" + userId + "\" is not a valid user to set a mangeing institution on", ErrorCodes.INSTITUTION_INVALID_USER);
            } else {
                // Must have an institution id
                if (institutionId) {
                    // Is it a valid institution
                    Map parameters = [
                        userId: institutionUser.id,
                        institutionId: institutionId
                    ];
                    List<Institution> institutions = InstitutionUser.executeQuery(USER_INSTITUTION_QUERY, parameters);
                    if (institutions.size() != 1) {
                        result.error("\"" + institutionId + "\" is not a valid institution for you to manage", ErrorCodes.INSTITUTION_INVALID);
                    } else {
                        // All looks good, so change the institution we are currently managing
                        institutionUser.institutionManaging = institutions[0];
                        institutionUser.save(flush: true, failOnError: true);
                    }
                } else {
                    result.error("No institution id supplied", ErrorCodes.ID_NOT_SUPPLIED);
                }
            }
        } else {
            result.error("No folio user id supplied", ErrorCodes.ID_NOT_SUPPLIED);
        }

        // return the result to the caller
        return(result);
    }

    /**
     * Fetches the institutions which the specified user has access to
     * @param userId The user we want the institutions for
     * @return The institutions and the groups through which they have access to the institution
     */
    public List<InstitutionResult> institutions(String userId) {
        List<InstitutionResult> result = new ArrayList<InstitutionResult>();
        InstitutionResult currentInstitution = null;
        Institution.executeQuery(INSTITUTION_AND_GROUP_QUERY, [ userId: userId ]).each { row ->
            Institution institition = row[POSITION_INSTITUTIONS_INSTITUTION];
            InstitutionGroup instititionGroup = row[POSITION_INSTITUTIONS_GROUP];

            // Is this a new institution
            if ((currentInstitution == null) || (currentInstitution.institution.id != institition.id)) {
                // It is, if we had a previous user, sort the groups by name
                if (currentInstitution != null) {
                    // Sort by name
                    currentInstitution.groups = currentInstitution.groups.sort { group -> group.name.toLowerCase() };
                }

                // Create ourselves a new user object
                currentInstitution = new InstitutionResult(institition.id, institition.name);

                // Add this user to the result
                result.add(currentInstitution);
            }

            // Add the group to the current user
            currentInstitution.groups.add(new ResultIdName(instititionGroup.id, instititionGroup.name));
        };

        // Finally sort the groups by name for the current user as that would have not been sorted
        if (currentInstitution != null) {
            // Sort by name
            currentInstitution.groups = currentInstitution.groups.sort { group -> group.name.toLowerCase() };
        }

        // Return the users sorted by name
        return(result.sort { user -> user.institution.name.toLowerCase() });
    }

    /**
     * Looks up by the institution user from the supplied folio user id
     * @param folioUserId The folio user id
     * @return The institution user record or null if the folio user id does not exist as an institution user id
     */
    private InstitutionUser getInstitutionUser(String folioUserId) {
        return(InstitutionUser.findByFolioUserId(folioUserId));
    }

    /**
     * Retrieves the details required for creating or editing an institution user
     * @return A InstitutionUserCreateEditResult object that contains all the details required to create or edit an institution user
     */
    public InstitutionUserCreateEditResult detailsForCreateEdit() {
        Collection<Institution> institutions = Institution.findAll();
        Collection<InstitutionGroup> institutionGroups = InstitutionGroup.findAll();

        // We need to fetch the users that have permission to the UI
        List<String> userIds = okapiPermissionService.getUsersWithAccessToUI();

        // Finally create our result record and return it
        return(new InstitutionUserCreateEditResult(
            institutions,
            institutionGroups,
            okapiUserService.getLabelsForUserids(userIds)
        ));
    }
}

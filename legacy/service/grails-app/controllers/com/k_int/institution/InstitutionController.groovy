package com.k_int.institution;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.OperationResult;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.utils.Json;
import com.k_int.institution.results.InstitutionCreateEditResult;
import com.k_int.institution.results.InstitutionUserResult;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Slf4j
@CurrentTenant
@Api(value = "/ill/institution", tags = ["Institution"])
@OkapiApi(name = "institution", isSystem = true)
@ExcludeFromGeneratedCoverageReport
public class InstitutionController extends OkapiTenantAwareSwaggerController<Institution>  {

	public InstitutionController() {
		super(Institution)
	}

    @ApiOperation(
        value = "Modifies the groups associated with the institution",
        nickname = "{id}/modifyGroups",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "path",
            required = true,
            value = "The id of the institution to modify the groups it belongs to",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "addRemove",
            paramType = "query",
            required = true,
            value = "Do we add or removed these groups",
            dataType = "string",
            allowableValues = com.k_int.ill.constants.Institution.ACTIONS,
            defaultValue = com.k_int.ill.constants.Institution.ACTION_ADD
        ),
        @ApiImplicitParam(
            name = "group",
            paramType = "query",
            required = true,
            dataType = "string",
            allowMultiple = true,
            value = "The group that is to be added or removed"
        )
    ])
    @OkapiPermission(name = "modifyGroups",  permissionGroup = PermissionGroup.WRITE)
    def modifyGroups() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_MODIFY_GROUPS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
        List<String> groupIds = params.list("group");
        OperationResult result = institutionService.modifyGroups(params.id, groupIds, !(com.k_int.ill.constants.Institution.ACTION_REMOVE.equals(params.addRemove)));
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Users associated with the institution",
        nickname = "{id}/users",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "path",
            required = true,
            value = "The id of the institution that the users are associated with",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "users",  permissionGroup = PermissionGroup.READ)
    def users() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_USERS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
        List<InstitutionUserResult> result = institutionService.users(params.id);
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Details required for creating or editing an institution",
        nickname = "createEditDetails",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission(name = "createEditDetails",  permissionGroup = PermissionGroup.READ)
    def createEditDetails() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_CREATE_EDIT_DETAILS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
        InstitutionCreateEditResult result = institutionService.detailsForCreateEdit();
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @Override
    protected void postCreate(Map createContext) {
        // Was it saved successfully
        if (savedResource) {
            // It was so add the settings for the institution
			SettingsData.load(savedResource);
        }
    }

    @Override
    protected void preUpdate(Map updateContext) {
        // Save the the groups that should be associated with the institution
        // So we can remove the ones thats should not be
        updateContext.groups = request.JSON.institutionGroups;
    }

    @Override
    protected void postUpdate(Map updateContext) {
        ArrayList<String> idsToKeep = new ArrayList<String>();

        // Have we been supplied with a list of ids that we want to keep
        if (updateContext.groups) {
            updateContext.groups.each { group ->
                idsToKeep.add(group.id);
            }
        }

        // Now let the service do the work
        institutionService.removeGroupsExcept(Institution, params.id, idsToKeep);
    }
}

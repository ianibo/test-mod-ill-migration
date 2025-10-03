package com.k_int.institution;

import com.k_int.GenericResult;
import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.OperationResult;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.utils.Json;
import com.k_int.institution.results.InstitutionUserCreateEditResult;
import com.k_int.institution.results.InstitutionUserManagableInstitutions;
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
@Api(value = "/ill/institutionUser", tags = ["Institution User"])
@OkapiApi(name = "institutionuser", isSystem = true)
@ExcludeFromGeneratedCoverageReport
public class InstitutionUserController extends OkapiTenantAwareSwaggerController<InstitutionUser>  {

    InstitutionUserService institutionUserService;

	public InstitutionUserController() {
		super(InstitutionUser)
	}

    @ApiOperation(
        value = "canManage",
        nickname = "canManage",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission(name = "canManage", permissionGroup = PermissionGroup.READ)
    public def canManage() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_CAN_MANAGE);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work
        InstitutionUserManagableInstitutions result =  institutionUserService.canManage(getInstitutionId(), getUserId());
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Set the institution to be managed",
        nickname = "manageInstitution",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "institution",
            paramType = "query",
            required = true,
            allowMultiple = false,
            value = "The institution to be managed",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "manageInstitution", permissionGroup = PermissionGroup.WRITE)
    def manageInstitution() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_MANAGE_INSTITUTION);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
        GenericResult result = institutionUserService.manageInstitution(getUserId(), params.institution);
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
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
        OperationResult result = institutionUserService.modifyGroups(params.id, groupIds, !(com.k_int.ill.constants.Institution.ACTION_REMOVE.equals(params.addRemove)));
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Institutions associated with the user",
        nickname = "{id}/institutions",
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
            value = "The id of the user that the institutions are associated with",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "institutions",  permissionGroup = PermissionGroup.READ)
    def institutions() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_INSTITUTIONS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
        List result = institutionUserService.institutions(params.id);
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Details required for creating or editing an institution user",
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
        InstitutionUserCreateEditResult result = institutionUserService.detailsForCreateEdit();
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @Override
    protected void preUpdate(Map updateContext) {
        // Save the the groups that should be associated with the user
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
        institutionUserService.removeGroupsExcept(InstitutionUser, params.id, idsToKeep);
    }
}

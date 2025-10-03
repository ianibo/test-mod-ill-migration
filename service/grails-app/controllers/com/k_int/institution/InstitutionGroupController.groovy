package com.k_int.institution;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.OperationResult;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.utils.Json;
import com.k_int.institution.results.InstitutionGroupCreateEditResult;
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
@Api(value = "/ill/institutionGroup", tags = ["Institution Group"])
@OkapiApi(name = "institutiongroup", isSystem = true)
@ExcludeFromGeneratedCoverageReport
public class InstitutionGroupController extends OkapiTenantAwareSwaggerController<InstitutionGroup>  {

    InstitutionGroupService institutionGroupService;

	public InstitutionGroupController() {
		super(InstitutionGroup)
	}

    @ApiOperation(
        value = "Modifies the institutions associated with the group",
        nickname = "{id}/modifyInstitutions",
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
            value = "The id of the group to modify the institutions it belongs to",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "addRemove",
            paramType = "query",
            required = true,
            value = "Do we add or removed these institutions",
            dataType = "string",
            allowableValues = com.k_int.ill.constants.Institution.ACTIONS,
            defaultValue = com.k_int.ill.constants.Institution.ACTION_ADD
        ),
        @ApiImplicitParam(
            name = "institution",
            paramType = "query",
            required = true,
            dataType = "string",
            allowMultiple = true,
            value = "The institutions that are to be added or removed"
        )
    ])
    @OkapiPermission(name = "modifyInstitutions",  permissionGroup = PermissionGroup.WRITE)
    def modifyInstitutions() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_MODIFY_INSTITUTIONS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
        List<String> institutionIds = params.list("institution");
        OperationResult result = institutionGroupService.modifyInstitutions(params.id, institutionIds, !(com.k_int.ill.constants.Institution.ACTION_REMOVE.equals(params.addRemove)));
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Modifies the users associated with the group",
        nickname = "{id}/modifyUsers",
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
            value = "The id of the group to modify the users that belongs to it",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "addRemove",
            paramType = "query",
            required = true,
            value = "Do we add or removed these users",
            dataType = "string",
            allowableValues = com.k_int.ill.constants.Institution.ACTIONS,
            defaultValue = com.k_int.ill.constants.Institution.ACTION_ADD
        ),
        @ApiImplicitParam(
            name = "user",
            paramType = "query",
            required = true,
            dataType = "string",
            allowMultiple = true,
            value = "The users that are to be added or removed"
        )
    ])
    @OkapiPermission(name = "modifyUsers",  permissionGroup = PermissionGroup.WRITE)
    def modifyUsers() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_MODIFY_USERS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
        List<String> userIds = params.list("user");
        OperationResult result = institutionGroupService.modifyUsers(params.id, userIds, !(com.k_int.ill.constants.Institution.ACTION_REMOVE.equals(params.addRemove)));
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Details required for creating or editing an institution group",
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
        InstitutionGroupCreateEditResult result = institutionGroupService.detailsForCreateEdit();
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @Override
    protected void preCreate(Map createContext) {
        // Set the no delete on the context
        createContext.noDelete = true;

        // We just call preUpdate to do the work for us
        preUpdate(createContext);
    }

    @Override
    protected void postCreate(Map createContext) {
        // We can only do something if we have a response object
        if (savedResource) {
            // Set the id from the response object
            createContext.id = savedResource.id;

            // We just call postUpdate to do the work for us
            postUpdate(createContext);
        }
    }

    @Override
    protected void preUpdate(Map updateContext) {
        // We clear out the institutions and institutionUsers and set them against the context
        // This is because only the parent can add them
        updateContext.institutions = request.JSON.institutions;
        updateContext.users = request.JSON.institutionUsers;
        request.JSON.institutions = null;
        request.JSON.institutionUsers = null;

        // Store the id in the context
        updateContext.id = params.id;
    }

    @Override
    protected void postUpdate(Map updateContext) {
        // If we have institutions then ensure they exist
        List institutionIds = [ ];
        if (updateContext.institutions) {
            updateContext.institutions.each { institution ->
                institutionIds.add(institution.id);
            }

            // Now call the service to ensure these exist
            institutionGroupService.modifyInstitutions(updateContext.id, institutionIds, true);
        }

        // Do we remove the institutions
        if (!updateContext.noDelete) {
            // Remove the institutions except the ones that have been supplied
            institutionGroupService.removeInstitutionsExceptFor(updateContext.id, institutionIds);
        }

        // If we have users then ensure they exist
        List userIds = [ ];
        if (updateContext.users) {
            updateContext.users.each { user ->
                userIds.add(user.id);
            }

            // Now call the service to ensure these exist
            institutionGroupService.modifyUsers(updateContext.id, userIds, true);
        }

        // Do we remove the users
        if (!updateContext.noDelete) {
            // Remove the users except the ones that have been supplied
            institutionGroupService.removeUsersExceptFor(updateContext.id, userIds);
        }
    }
}

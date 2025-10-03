package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.protocols.illEmail.IllEmailMessageTokensService;
import com.k_int.ill.results.IllSmtpMessageCreateEditResult;
import com.k_int.ill.statemodel.ActionService;
import com.k_int.ill.utils.Json;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.converters.JSON;
import grails.gorm.multitenancy.CurrentTenant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@CurrentTenant
@Api(value = "/ill/illSmtpMessage", tags = ["IllSmtpMessage"])
@OkapiApi(name = "illSmtpMessage")
@ExcludeFromGeneratedCoverageReport
public class IllSmtpMessageController extends OkapiTenantAwareSwaggerController<IllSmtpMessage> {

	static responseFormats = ['json', 'xml']

	ActionService actionService;
	IllEmailMessageTokensService illEmailMessageTokensService;
	IllSmtpMessageService illSmtpMessageService;

	public IllSmtpMessageController() {
		super(IllSmtpMessage)
	}

    @ApiOperation(
        value = "Details required for creating or editing an Ill SMTP Message",
        nickname = "createEditDetails",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission(name = "createEditDetails",  permissionGroup = PermissionGroup.WRITE)
    def createEditDetails() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_CREATE_EDIT_DETAILS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
        IllSmtpMessageCreateEditResult result = illSmtpMessageService.detailsForCreateEdit(getInstitution());
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Retrieves the token values that will be used for a request, when sending a SMTP request",
        nickname = "tokenValues/{patronRequestId}",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "patronRequestId",
            paramType = "path",
            required = true,
            value = "The identifier of the request that we want the token values for",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "tokenValues",  permissionGroup = PermissionGroup.WRITE)
    def tokenValues() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_TOKEN_VALUES);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
		def result = [ : ];
        if (params.patronRequestId) {
            // Is it a valid request for this institution
			PatronRequest patronRequest = actionService.getRequest(params.patronRequestId, getInstitution());
            if (patronRequest == null) {
                // No it wasn't
                result.error = "Request id not valid for institution";
                response.status = 400;
            } else {
                // Get hold of the token values
				result.values = illEmailMessageTokensService.tokenValues(patronRequest);
            }
        } else {
            result.error = "No patron request id supplied";
            response.status = 400;
        }
		
		render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

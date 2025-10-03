package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;

import grails.converters.JSON;
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
@Api(value = "/ill/remoteAction", tags = ["Remote Action"])
@OkapiApi(name = "remoteAction")
@ExcludeFromGeneratedCoverageReport
public class RemoteActionController extends OkapiTenantAwareSwaggerController<RemoteAction>  {

    private static final String RESOURCE_REMOTE_ACTION = RemoteAction.getSimpleName();

	RemoteActionService remoteActionService;
	
	public RemoteActionController() {
		super(RemoteAction)
	}

    @ApiOperation(
        value = "Performs the action action associated with the id",
        nickname = "{id}/perform",
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
            value = "The identifier that contains the details about what needs to happen",
            dataType = "string"
        )
    ])
    @OkapiPermission()
	public def perform() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_REMOTE_ACTION);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_PERFORM);
        log.debug(ContextLogging.MESSAGE_ENTERING);

		def result = [ : ]
		RemoteAction remoteAction = remoteActionService.get(params.id);
        if (remoteAction == null) {
            log.error("Unable to find remote action with id: " + params.remoteActionId);
            response.status = 400;
        } else {
            // Execute the action
            result = remoteActionService.perform(remoteAction);
            response.status = (result.actionResult == ActionResult.SUCCESS ? 200 : (result.actionResult == ActionResult.INVALID_PARAMETERS ? 400 : 500));

            // We do not want to pass the internal action result back to the caller, so we need to remove it
            result.remove('actionResult');
        }

		render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

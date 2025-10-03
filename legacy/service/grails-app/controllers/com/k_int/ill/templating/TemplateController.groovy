package com.k_int.ill.templating;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.results.TemplateContainerCreateEditResult;
import com.k_int.ill.utils.Json;
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
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Slf4j
@CurrentTenant
@Api(value = "/ill/template", tags = ["Template Container"])
@OkapiApi(name = "template")
@ExcludeFromGeneratedCoverageReport
public class TemplateController extends OkapiTenantAwareSwaggerController<TemplateContainer>  {

	TemplateContainerService templateContainerService;
    TemplatingService templatingService;

  public TemplateController() {
    super(TemplateContainer)
  }

  /*
   * Deletes the template if it is not currently in use
   */
    @ApiOperation(
        value = "Deletes the specified template container",
        nickname = "/{id}",
        httpMethod = "DELETE"
    )
    @ApiResponses([
        @ApiResponse(code = 204, message = "No Content Success"),
        @ApiResponse(code = 400, message = "Bsd Request the template is in use")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "path",
            required = true,
            value = "The id of the template container to delete",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "delete", permissionGroup = PermissionGroup.ALL)
    public def delete() {
      // Before deletion, check this template container isn't in use as a value from any template setting
      if (!templatingService.usedInAppSettings(getInstitution(), params.id)) {
        super.delete()
      } else {
        response.sendError(400, "That template is in use on one or more AppSettings")
      }
    }
	
    @ApiOperation(
        value = "Details required for creating or editing a template container",
        nickname = "createEditDetails",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "context",
            paramType = "query",
            required = true,
            allowMultiple = false,
            value = "The container context the tokens are required for",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "createEditDetails",  permissionGroup = PermissionGroup.WRITE)
    def createEditDetails() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_CREATE_EDIT_DETAILS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Let the service do the work for us
        TemplateContainerCreateEditResult result = templateContainerService.detailsForCreateEdit(
			getInstitution(),
			params.context
		);
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

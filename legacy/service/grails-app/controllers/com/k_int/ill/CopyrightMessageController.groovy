package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.results.CopyrightMessageCreateEditResult;
import com.k_int.ill.utils.Json;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Slf4j
@CurrentTenant
@Api(value = "/ill/copyrightMessage", tags = ["CopyrightMessage"])
@OkapiApi(name = "Copyright Message")
@ExcludeFromGeneratedCoverageReport
public class CopyrightMessageController extends OkapiTenantAwareSwaggerController<CopyrightMessage>  {

	CopyrightMessageService copyrightMessageService;

	public CopyrightMessageController() {
		super(CopyrightMessage)
	}
	
    @ApiOperation(
        value = "Details required for creating or editing a copyright message",
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
        CopyrightMessageCreateEditResult result = copyrightMessageService.detailsForCreateEdit();
        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

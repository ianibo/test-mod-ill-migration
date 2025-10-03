package com.k_int.ill

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.ill.constants.Okapi;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.gorm.multitenancy.CurrentTenant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@CurrentTenant
@Api(value = "/ill/timers", tags = ["Timer"])
@OkapiApi(name = "timer")
@ExcludeFromGeneratedCoverageReport
public class TimerController extends OkapiTenantAwareSwaggerController<Timer> {

    static responseFormats = ['json', 'xml']

    BackgroundTaskService backgroundTaskService;

    public TimerController() {
        super(Timer)
    }

    @ApiOperation(
        value = "Executes the timer with the supplied code",
        nickname = "execute",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "query",
            required = false,
            value = "The id of the timer object to be executed",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "code",
            paramType = "query",
            required = false,
            value = "The code of the timer object to be executed",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "execute", permissionGroup = PermissionGroup.WRITE)
    public def execute() {
        List result = backgroundTaskService.executeTimer(params.id, params.code, request.getHeader(Okapi.HEADER_TENANT));

        // "as JSON" dosn't expand parent object, so using JsonMapper instead
        JsonMapper jsonMapper = new JsonMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String json = jsonMapper.writeValueAsString(result);
        render json, status: 200, contentType: "application/json";
    }
}

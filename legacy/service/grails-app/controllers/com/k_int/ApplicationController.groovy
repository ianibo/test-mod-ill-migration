package com.k_int;

import com.k_int.ill.logging.ContextLogging;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;

import grails.core.GrailsApplication;
import grails.plugins.GrailsPluginManager;
import grails.plugins.PluginManagerAware;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Api(value = "/", tags = ["Application"])
@OkapiApi(name = "application")
@ExcludeFromGeneratedCoverageReport
public class ApplicationController implements PluginManagerAware {

    GrailsApplication grailsApplication;
    GrailsPluginManager pluginManager;

    @ApiOperation(
        value = "The settings for the applications",
        nickname = "ill",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission()
    public def index() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_INDEX);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        render view: "index", model: [grailsApplication: grailsApplication, pluginManager: pluginManager]

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

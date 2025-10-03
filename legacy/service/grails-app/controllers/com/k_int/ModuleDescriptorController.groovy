package com.k_int;

import com.k_int.ModuleDescriptor.ModuleDescriptorService;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.converters.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Api(value = "/ill/moduleDescriptor", tags = ["Module Descriptor"])
@OkapiApi(name = "moduleDescriptor")
@ExcludeFromGeneratedCoverageReport
public class ModuleDescriptorController {

    ModuleDescriptorService moduleDescriptorService;

    public ModuleDescriptorController() {
    }

    @ApiOperation(
        value = "Generate the module descriptor template",
        nickname = "generate",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "validate",
            paramType = "query",
            required = false,
            value = "Do we validate the generated url mappings against the current url mappings. Note: this generates extra settings ing the module descriptor",
            defaultValue = "false",
            dataType = "boolean"
        )
    ])
    @OkapiPermission(name = "generate", permissionGroup = PermissionGroup.READ)
    public def generate() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_GENERATE);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Generate the module descriptor template
        boolean validate = params.validate ? params.validate.toBoolean() : false;
        Map moduleDescriptorTemplate = moduleDescriptorService.generate("ill", validate);

        // Now render it as json
        render moduleDescriptorTemplate as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}
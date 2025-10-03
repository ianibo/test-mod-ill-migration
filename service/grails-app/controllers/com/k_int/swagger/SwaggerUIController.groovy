package com.k_int.swagger;

import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;

import groovy.json.JsonOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

/**
 * Swagger UI Controller
 */
@Api(value = "/ill", tags = ["Swagger UI"])
@OkapiApi(name = "swagger")
@ExcludeFromGeneratedCoverageReport
public class SwaggerUIController {

    SwaggerApiService swaggerApiService;

    /**
     * Generates the api documentation, as we do not want all package being documented and there is no way to configure
     * which packages we want or to exclude, we do not use the default SwaggerController api call, but simulate it here
     * and then manipulate what is returned, it is hard coded.
     * @return The api document in json form
     */
    @ApiOperation(
        value = "API documentation for the application",
        nickname = "swagger*",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission
    public def swaggerUI() {
        // Should never get called as it should be redirected elsewhere in url mappings
        // Just here so that the module descriptor template generation creates a path to it
    }

    /**
     * Generates the api documentation, this is from a service we have written as the grails plugin does not work with JDK 17,
     * so we are only looking at the annotations hence its name
     * @return The api document in json form
     */
    @ApiOperation(
        value = "API documentation for the application",
        nickname = "swagger/api",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission
    public def justFromAnnotations() {
        // Generate the API documentation
        Map swaggerApiDoc = swaggerApiService.generateSwaggerApiDoc();

        // This header is required if we are coming through okapi
        header("Access-Control-Allow-Origin", request.getHeader('Origin'));

        // We should now just have the calls we are interested in
        render(status: 200, contentType: "application/json", text: JsonOutput.toJson(swaggerApiDoc));
    }
}

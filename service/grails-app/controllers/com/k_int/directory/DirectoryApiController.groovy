package com.k_int.directory;

import com.k_int.events.AppListenerService;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.converters.JSON;
import grails.gorm.multitenancy.CurrentTenant;
import grails.web.Controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@CurrentTenant
@Controller
@Api(value = "/ill/directory/api", tags = ["Directory API"])
@OkapiApi(name = "directoryApi")
@ExcludeFromGeneratedCoverageReport
public class DirectoryApiController {

	AppListenerService appListenerService;
	FoafService foafService;

    @ApiOperation(
        value = "Fetch the symbols for a directory entry",
        nickname = "findSymbol",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid parameters have been supplied")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "for",
            paramType = "query",
            required = true,
            allowMultiple = false,
            value = "The id of the directory entry that symbols are required for",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "ns",
            paramType = "query",
            required = true,
            allowMultiple = false,
            value = "The namespace the symbols are required for",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "findsymbol", permissionGroup = PermissionGroup.READ)
	public def findSymbol() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_FIND_SYMBOL);
        log.debug(ContextLogging.MESSAGE_ENTERING);

		def result = [ : ];
		DirectoryEntry directory_entry = DirectoryEntry.get(params.'for');
		if (directory_entry) {
			// got entry
			log.debug("Located directory entry ${directory_entry} - symbols ${directory_entry.symbols.collect { it.symbol } }");
			result.symbol = directory_entry.locateSymbolInNamespace(params.ns);
		} else {
			result.message = "Unable to locate directory entry ${params.'for'}";
		}

		render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
	}

    @ApiOperation(
        value = "Fetches the specified url and adds the found institutions as directory entries",
        nickname = "addFriend",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid parameters have been supplied")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "friendUrl",
            paramType = "query",
            required = true,
            allowMultiple = false,
            value = "The url that contains the definitions of the institution to be added",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "addFriend", permissionGroup = PermissionGroup.WRITE)
	public def addFriend() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_ADD_FRIEND);
        log.debug(ContextLogging.MESSAGE_ENTERING);

		def result=[status:'OK'];
		log.debug("ApplicationController::addFriend(${params})");
		if (params.friendUrl) {
			foafService.checkFriend(params.friendUrl);
		}
		render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
	}

    @ApiOperation(
        value = "Freshen the directory entries",
        nickname = "freshen",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid parameters have been supplied")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "republish",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Should the directory entries be republished",
            dataType = "string",
            defaultValue = "N",
            allowableValues = "N,Y"
        )
    ])
    @OkapiPermission(name = "freshen", permissionGroup = PermissionGroup.READ)
	public def freshen() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_FRESHEN);
        log.debug(ContextLogging.MESSAGE_ENTERING);

		def result=[status:'OK'];
		String tenant_header = request.getHeader('X-OKAPI-TENANT');
		foafService.freshen(tenant_header);

		if (params.republish != null) {
			if ((params.republish == 'Y' ) && (tenant_header?.length() > 0)) {
				appListenerService.republish(tenant_header);
			}
		}

		render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
	}
}

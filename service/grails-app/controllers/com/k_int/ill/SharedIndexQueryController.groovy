package com.k_int.ill;

import com.k_int.GenericResult;
import com.k_int.OkapiTenantAwareInstitutionController;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.routing.SharedindexRouterService;
import com.k_int.ill.settings.SharedIndexSettings;
import com.k_int.ill.sharedindex.SharedIndexAvailabilityResult;
import com.k_int.ill.sharedindex.SharedIndexResult;
import com.k_int.ill.sharedindex.openRS.connections.OpenRsTokenApiConnection;
import com.k_int.ill.utils.Json;
import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionSetting;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;
import com.k_int.settings.SharedIndexSettingsService;
import com.k_int.web.toolkit.settings.AppSetting;

import grails.converters.JSON;
import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import groovyx.net.http.FromServer;
import groovyx.net.http.HttpBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Slf4j
@CurrentTenant
@Api(value = "/ill/sharedIndexQuery", tags = ["Shared Index Query"])
@OkapiApi(name = "sharedIndexQuery")
@ExcludeFromGeneratedCoverageReport
// Note: We needed to extend from OkapiTenantAwareInstitutionController. so needed a domain,
// so just used InstitutionSetting,feel free to swap it out as it should never get used
public class SharedIndexQueryController extends OkapiTenantAwareInstitutionController<InstitutionSetting>{

    @Autowired
    OpenRsTokenApiConnection openRsTokenApiConnection;

    SharedindexRouterService sharedindexRouterService;
    SharedIndexSettingsService sharedIndexSettingsService;
    SharedIndexService sharedIndexService;

    public SharedIndexQueryController() {
        // Have purposely made this domain different, so it will blow up if it ever uses it
        super(AppSetting);
    }

    @ApiOperation(
        value = "Performs a pass through query to the configured shared index, this will be removed so clients do not need to know about the shared index",
        nickname = "/",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission(name = "passThrough", permissionGroup = PermissionGroup.READ)
    public def passThrough() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_PASS_THROUGH);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        def stream;
        def status;
        def headers;
        def si = sharedIndexService.getSharedIndexActions();

        // Is the shared index configured
        if (si == null) {
            renderError("No shared index configured to perform a query", "SharedIndexQuerry_001");
        } else {
            // right now only FOLIO SI has such a method, eventually (TODO) we should
            // have an interface for the passthrough but it probably won't be based on
            // HttpBuilder for reasons below
            if (!(si instanceof FolioSharedIndexService)) {
                renderError("Query passthrough accessed on a shared index that does not implement it", "SharedIndexQuerry_002");
            } else {
                HttpBuilder httpBuilder = si.queryPassthrough(request);
                if (httpBuilder == null) {
                    renderError("Shared index not configured correctly to perform a query", "SharedIndexQuerry_003");
                } else {
                    httpBuilder.get() {
                        def parser = { Object cfg, FromServer fs ->
                            stream = fs.inputStream;
                            status = fs.getStatusCode();
                            headers = fs.getHeaders();
                        }
                        // Doesn't seem to be a clear way to just disable the parser irrespective of
                        // content-type, will probably ultimately want to eschew HttpBuilder entirely
                        // for this interface
                        response.parser('application/json', parser);
                        response.parser('text/plain', parser);
                        response.success { FromServer fs ->
                            log.debug("Success response from shared index query passthrough: ${fs.getStatusCode()}");
                        }
                        response.failure { FromServer fs ->
                            log.debug("Failure response from shared index query passthrough: ${status}");
                        }
                    };

                    def passHeaders = ['Content-Type'];
                    passHeaders.each {
                        response.setHeader(it, headers.find { h-> h.key == it }.getValue());
                    }

                    response.setStatus(status);
                    response << stream;
                }
            }
        }

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Find suppliers for the shared index cluster identifier",
        nickname = "findMoreSuppliers",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "clusterSystemIdentifierId",
            paramType = "query",
            required = true,
            value = "The cluster identifier that we want to get the holdings from",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "findMoreSuppliers", permissionGroup = PermissionGroup.READ)
    public def findMoreSuppliers() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_FIND_MORE_SUPPLIERS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        Map result = [ : ];
        String clusterSystemIdentifierId = params.clusterSystemIdentifierId;

        if (clusterSystemIdentifierId) {
            Institution institution = getInstitution();
			PatronRequest patronRequest = new PatronRequest();
			patronRequest.systemInstanceIdentifier = clusterSystemIdentifierId;
			patronRequest.institution = institution;
            Map search = [ systemInstanceIdentifier : clusterSystemIdentifierId ];
            result.copiesFound = sharedIndexService.getSharedIndexActions().findAppropriateCopies(institution, search);
            result.suppliersFound = sharedindexRouterService.findMoreSuppliers(patronRequest);
        }
        render result as JSON

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Fetch the bib record for a cluster identifier",
        nickname = "byId",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "clusterSystemIdentifierId",
            paramType = "query",
            required = true,
            value = "The cluster identifier that we want to get the record from",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "byId", permissionGroup = PermissionGroup.READ)
    public def byId() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_BY_ID);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        Map search = [
            systemInstanceIdentifier : params.clusterSystemIdentifierId
        ];
        SharedIndexResult result = sharedIndexService.getSharedIndexActions().fetchSharedIndexRecords(search);

        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Fetch the bib record for a cluster identifier using a query search",
        nickname = "byQuery",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "term",
            paramType = "query",
            required = true,
            value = "The search term to use to perform the query against the shared index",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "page",
            paramType = "query",
            required = false,
            value = "The page want the results being returned from, if not supplied will be determined from offset",
            dataType = "number"
        ),
        @ApiImplicitParam(
            name = "offset",
            paramType = "query",
            required = false,
            value = "From which record in the result set do you want the records being returned, ignored if page supplied",
            dataType = "number"
        ),
        @ApiImplicitParam(
            name = "perPage",
            paramType = "query",
            required = false,
            value = "Number of records per page, if not supplied max will be used instead",
            dataType = "number"
        ),
        @ApiImplicitParam(
            name = "max",
            paramType = "query",
            required = false,
            value = "How many records should be returned, ignored if perPage supplied",
            dataType = "number",
            defaultValue = "10"
        ),
        @ApiImplicitParam(
            name = "filters",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The filters to be applied",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "byQuery", permissionGroup = PermissionGroup.READ)
    public def byQuery() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_BY_QUERY);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        long pageSize = sharedIndexService.determinePageSize(params.perPage, params.max);
        long offset = sharedIndexService.determineStartOffset(pageSize, params.page, params.offset);
        String filters = params.filters;
        Map search = [
            query : params.term,
            from : offset,
            size : pageSize,
            filters : sharedIndexService.parseFilters(filters)
        ];
        SharedIndexResult result = sharedIndexService.getSharedIndexActions().fetchSharedIndexRecords(search);

        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Fetch the availability for a cluster identifier",
        nickname = "availability",
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
            required = true,
            value = "The shared index identifier the availability is required for",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "availability", permissionGroup = PermissionGroup.READ)
    public def availability() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_AVAILABILITY);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        SharedIndexAvailabilityResult result = sharedIndexService.getSharedIndexActions().availability(params.id);

        render Json.toJson(result), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Retrieve a token from the Shared Index authentication server",
        nickname = "token",
        produces = "application/json",
        httpMethod = "POST",
        consumes = "multipart/form-data"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "username",
            paramType = "formData",
            required = true,
            value = "The username a token is required for",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "password",
            paramType = "formData",
            required = true,
            value = "The password for the user",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "token", permissionGroup = PermissionGroup.READ)
    public def token() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_TOKEN);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        Map result = [ : ];
        result.token = openRsTokenApiConnection.get(new SharedIndexSettings(sharedIndexSettingsService), params.username, params.password);

        render result as JSON

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    private void renderError(String message, String errorCode, int httpStatus = 422) {
        GenericResult result = new GenericResult();
        result.error(message, errorCode);
        log.warn(message);
        render Json.toJson(result), status: httpStatus, contentType: "application/json";
    }
}

package com.k_int.ill;

import com.k_int.OkapiTenantAwareInstitutionController;
import com.k_int.directory.DirectoryEntryService;
import com.k_int.directory.Symbol;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.utils.Json;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Slf4j
@CurrentTenant
@Api(value = "/ill/statistics", tags = ["ILL Statistics"])
@OkapiApi(name = "statistics")
@ExcludeFromGeneratedCoverageReport
public class StatisticsController extends OkapiTenantAwareInstitutionController<Counter> {

    DirectoryEntryService directoryEntryService;
    StatisticsService statisticsService;

    public StatisticsController() {
        super(Counter)
    }

    @ApiOperation(
        value = "Generates the statistics for this tenant",
        nickname = "",
        httpMethod = "GET",
        produces = "application/json"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission(name = "index", permissionGroup = PermissionGroup.READ)
    public def index() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_INDEX);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Generate and render the statistics
        render Json.toJson(statisticsService.generateStatistics(getInstitution(), false)), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Generates the statistics for a symbol",
        nickname = "forSymbol",
        httpMethod = "GET",
        produces = "application/json"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "symbol",
            paramType = "query",
            required = true,
            value = "The symbol of the form authority:symbol that the statistics are required for",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "fileUpload", permissionGroup = PermissionGroup.READ)
    public def forSymbol() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_INDEX);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Determine the symbol they want the statistics for
        String stringSymbol = null;
        Symbol symbol = null;
        if (params.symbol && (params.symbol instanceof String)) {
            symbol = directoryEntryService.resolveCombinedSymbol(params.symbol);
        }

        log.debug("Chas: Receieved symbol: " + params.symbol + ", resolved: " + (symbol == null ? "No" : "Yes"));
        // Now we have the symbol gather the statistics for it and return them as json
        IllStatisticSymbol illStatisticSymbol = statisticsService.getStatsFor(symbol);
        if (illStatisticSymbol == null) {
            illStatisticSymbol = IllStatisticSymbol.defaultStatistics;
        }
        render Json.toJson(illStatisticSymbol), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

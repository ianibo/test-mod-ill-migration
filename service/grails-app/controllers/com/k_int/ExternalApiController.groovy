package com.k_int;

import com.k_int.directory.DirectoryEntry;
import com.k_int.events.AppListenerService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.StatisticsService;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.utils.Json;
import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionService;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;

import grails.converters.JSON;
import grails.core.GrailsApplication;
import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * External Read-Only APIs for resource sharing network connectivity
 */
@Slf4j
@CurrentTenant
@Api(value = "/ill/externalApi", tags = ["External API"])
@OkapiApi(name = "externalApi")
@ExcludeFromGeneratedCoverageReport
public class ExternalApiController {

    static private final String STATUS_QUERY = '''
select pr.institution.name, pr.stateModel.shortcode, pr.state.code, count(*)
from PatronRequest as pr
group by pr.institution.name, pr.stateModel.shortcode, pr.state.code
order by pr.institution.name, pr.stateModel.shortcode, pr.state.code
''';

    static private final String MANGED_QUERY = '''
select de
from DirectoryEntry as de
where de.status.value = :managed and
      de.parent is null
''';

    AppListenerService appListenerService;
    GrailsApplication grailsApplication;
    InstitutionService institutionService;
    StatisticsService statisticsService;

    // ToDo this method needs a TTL cache and some debounce mechanism as it increases in complexity
    @ApiOperation(
        value = "Return the statistics",
        nickname = "statistics",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "symbol",
            paramType = "query",
            required = false,
            value = "The symbol in the form authority:symbol that the statistics are required for",
            dataType = "string"
        )
    ])
    @OkapiPermission()
    public def statistics() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_STATISTICS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Obtain the institution
        Institution institution = institutionService.getInstitutionForSymbol(params.symbol);

        // Generate and render the statistics
		render Json.toJson(statisticsService.generateStatistics(institution, true)), contentType: "application/json";
		
        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Generates a brief status report about the service",
        nickname = "statusReport",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission()
    public def statusReport() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_STATUS_REPORT);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        Map result = [
            institutions : [ ]
        ];

        Map institution = null;
        Map stateModel = null;
        PatronRequest.executeQuery(STATUS_QUERY).each { sg ->
            if ((stateModel == null) || !stateModel.name.equals(sg[1])) {
                if ((institution == null) || !institution.name.equals(sg[0])) {
                    institution = [
                        name : sg[0],
                        stateModels : [ ]
                    ];
                    result.institutions.add(institution);
                }
                stateModel = [
                    name : sg[1],
                    states : [ ]
                ];
                institution.stateModels.add(stateModel);
            }
            stateModel.states.add(
                [
                    state : sg[2],
                    count :sg[3]
                ]
            );
        };
        render result as JSON

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    public def managedDirectories() {
        def result =  [
            status:'OK',
            hostedEntries:[],
            directoryAppMetadata:[
                version: grailsApplication.config?.info?.app?.version,
                buildNumber: grailsApplication.metadata['build.number']
            ]
        ]

        DirectoryEntry.executeQuery(MANGED_QUERY, [managed: com.k_int.ill.constants.Directory.STATUS_VALUE_MANAGED]).each { de ->
            result.hostedEntries.add([slug:de.slug, name:de.name, description: de.description]);
        }

        render result as JSON;
    }

    @ApiOperation(
        value = "Retrieve details about the request directory entry",
        nickname = "directoryEntry",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "slug",
            paramType = "query",
            required = false,
            value = "The directory entry the information is requested for",
            dataType = "string"
        )
    ])
    @OkapiPermission()
    public def directoryEntry(final String slug) {

        Map result = null;
        DirectoryEntry de = DirectoryEntry.findBySlug(slug)

        log.debug("Looking up ${slug} - result:${de}");

        // We want a JSON version of the directory entry without any identifiers in it - this is a context free object
        // that other reshare instances will consume, not a JSON object for a local edit screen. appListenerService already
        // exposes a method for this - so reuse it.
        if ( de ) {
            result = appListenerService.makeDirentJSON(de, true, false, true);
        }
        else {
            response.sendError(404)
        }

        if ( result ) {
            result.directoryAppMetadata = [
                version: grailsApplication.config?.info?.app?.version,
                buildNumber: grailsApplication.metadata['build.number']
            ]
        }
        render result as JSON
    }
}

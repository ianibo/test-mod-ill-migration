package com.k_int.ill;

import com.k_int.OkapiTenantAwareInstitutionController;
import com.k_int.ill.files.FileFetchResult;
import com.k_int.ill.files.ReportCreateUpdateResult;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.reporting.Report;
import com.k_int.ill.reporting.ReportService;
import com.k_int.institution.Institution;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.converters.JSON;
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
@Api(value = "/ill/report", tags = ["Report"])
@OkapiApi(name = "report")
@ExcludeFromGeneratedCoverageReport
public class ReportController extends OkapiTenantAwareInstitutionController<Report>  {

    private static final String RESOURCE_REPORT = Report.getSimpleName();

    BatchService batchService;

	public ReportController() {
		super(Report)
	}

    ReportService reportService;

    @ApiOperation(
        value = "Create / Update a report",
        nickname = "createUpdate",
        httpMethod = "POST",
        consumes = "multipart/form-data",
        produces = "application/json"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "formData",
            allowMultiple = false,
            required = false,
            value = "The id of the report if updating, leave blank for a new report",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "name",
            paramType = "formData",
            allowMultiple = false,
            required = true,
            value = "A user friendly name for this report",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "description",
            paramType = "formData",
            allowMultiple = false,
            required = true,
            value = "The description for this report",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "domain",
            paramType = "formData",
            allowMultiple = false,
            required = true,
            value = "The domain this report is for (eg. patronRequest)",
            dataType = "string",
            allowableValues = "patronRequest"
        ),
        @ApiImplicitParam(
            name = "contentType",
            paramType = "formData",
            allowMultiple = false,
            required = true,
            value = "The content type of the generated report)",
            dataType = "string",
            allowableValues = "application/pdf"
        ),
        @ApiImplicitParam(
            name = "filename",
            paramType = "formData",
            allowMultiple = false,
            required = true,
            value = "The filename to give the generated report",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "isSingleRecord",
            paramType = "formData",
            allowMultiple = false,
            required = true,
            value = "Does this report just take a single identifier",
            dataType = "boolean",
            defaultValue = "false"
        ),
        @ApiImplicitParam(
            name = "file",
            paramType = "formData",
            allowMultiple = false,
            required = true,
            value = "The file to be uploaded",
            dataType = "file"
        )
    ])
    @OkapiPermission(name = "createUpdate", permissionGroup = PermissionGroup.WRITE)
    public def createUpdate() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_REPORT);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_CREATE_UPDATE);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Need to convert the parameter isSingleRecord to a boolean first
        boolean isSingleRecord = params.isSingleRecord ? params.isSingleRecord.toBoolean() : false;

        // Just pass it onto the service to do the work
        ReportCreateUpdateResult result = reportService.createUpdate(
            getInstitution(),
            params.name,
            params.description,
            params.domain,
            isSingleRecord,
            params.contentType,
            params.filename,
            params.file,
            params.id
        );

        // Render the result as json
        render result as JSON

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Executes the picklist report",
        nickname = "generatePicklist",
        httpMethod = "GET",
        produces = "application/pdf,application/json"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success", response = byte.class)
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "batchId",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "The batch to generate the picklist from",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "requestId",
            paramType = "query",
            allowMultiple = true,
            required = false,
            value = "The request identifier(s) to generate the report for",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "generatePicklist", permissionGroup = PermissionGroup.READ)
    public def generatePicklist() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_REPORT);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_GENERATE_PICK_LIST);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        List requestIdentifiers = null;

        // Do we have a batch id
        if (params.batchId != null) {
            // Generate the request identifiers from the batch
            requestIdentifiers = batchService.fetchRequestIdentifiersForBatch(params.batchId);
        } else if (params.requestId != null) {
            // We have 1 or more request identifiers
            if (params.requestId instanceof String) {
                requestIdentifiers = new ArrayList();
                requestIdentifiers.add(params.requestId);
            } else if (params.requestId != null) {
                // it must be an array
                requestIdentifiers = params.requestId;
            }
        }

        // From the supplied list of identifiers, obtain those that are only valid valid for the users institution
        requestIdentifiers = validateForInstitution(PatronRequest, requestIdentifiers);

        // Have we been supplied any request identifiers
        if ((requestIdentifiers == null) || (requestIdentifiers.size() == 0)) {
            Map renderResult = [ error: "No valid batch identifier or request identifier has been specified to generate a report" ];
            render renderResult as JSON, status: 400, contentType: "application/json";
        } else {
            // Now generate the report, this does the render
            Institution institution = getInstitution();
            generateReport(
                reportService.getPullSlipReportId(institution),
                requestIdentifiers,
                reportService.getPullSlipLogoId(institution),
                ReportService.pullSlipDefaultReport
            );
        }

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Executes the specified report",
        nickname = "execute",
        httpMethod = "GET",
        produces = "application/pdf,application/json"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success", response = byte.class)
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "reportId",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "The id of the report to be run",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "id",
            paramType = "query",
            allowMultiple = true,
            required = true,
            value = "The id(s) that that will be passed to the report",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "execute", permissionGroup = PermissionGroup.READ)
    public def execute() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_REPORT);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_EXECUTE);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        List ids;
        if (params.id == null) {
            ids = new ArrayList();
        } else if (params.id instanceof String) {
            ids = new ArrayList();
            ids.add(params.id);
        } else {
            // it must be an array
            ids = params.id;
        }

        // Now generate the report, this performs the render
        generateReport(params.reportId, ids);

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    /**
     * Generates the report
     * @param reportId The report id to generate
     * @param identifiers The identifiers to pass to the report
     * @param defaultReport The path to the default report in the resources
     */
    private void generateReport(String reportId, List identifiers, String imageId = null, String defaultReport = null) {

        // Set the no cache header, should catch all permentations of the browser not cacheing it
        // As the conents may have changed, the next time they view the report
        header('Cache-Control', 'max-age=0, no-cache, no-store');

        // It is assumed that everything has been validated by this point
        try {
            // Attempt to execute the report
            FileFetchResult fetchResult = reportService.generateReport(
                getInstitution(),
                request.getHeader("X-Okapi-Tenant"),
                reportId,
                identifiers,
                imageId,
                defaultReport
            );

            // Did we manage to generate the report
            if (fetchResult.inputStream == null) {
                // we had an error
                Map renderResult = [ error: fetchResult.error ];
                render renderResult as JSON, status: 404, contentType: "application/json";
            } else {
                // Present the pdf
                render file: fetchResult.inputStream, contentType: fetchResult.contentType, status: 200, filename: fetchResult.filename
            }
        } catch (Exception e) {
            String message = "Exception thrown generating report";
            log.error(message, e);
            Map renderResult = [ error: (message + ", exception: " + e.getMessage()) ];
            render renderResult as JSON, status: 404, contentType: "application/json";
        }
    }
}

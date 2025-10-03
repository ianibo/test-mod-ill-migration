package com.k_int.ill;

import com.k_int.OkapiTenantAwareInstitutionController;
import com.k_int.directory.Symbol;
import com.k_int.ill.itemSearch.Z3950SearchService;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.logging.HoldingLogDetails;
import com.k_int.ill.logging.IHoldingLogDetails;
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
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Slf4j
@Api(value = "/ill/testExternalSearch", tags = ["Test External Search"])
@OkapiApi(name = "testExternalSearch")
@ExcludeFromGeneratedCoverageReport
@CurrentTenant
public class TestExternalSearchController extends OkapiTenantAwareInstitutionController<PatronRequest> {

    IllApplicationEventHandlerService illApplicationEventHandlerService;
	Z3950SearchService z3950SearchService;

	public TestExternalSearchController() {
        super(PatronRequest);
	}

    /**
     * Determines the best location
     * @return The result from the determineBestLocation call
     */
    @ApiOperation(
        value = "Locate an item externally",
        nickname = "locate",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "requestingInstitutionSymbol",
            paramType = "query",
            allowMultiple = false,
            required = true,
            value = "The symbol of the requesting institution",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "supplierItemId",
            paramType = "query",
            required = false,
            value = "Supplier item Id",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "title",
            paramType = "query",
            required = false,
            value = "Title of the item",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "isbn",
            paramType = "query",
            required = false,
            value = "ISBN of the item",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "locate", permissionGroup = PermissionGroup.READ)
    public def locate() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_TEST_EXTERNAL_LOCATE);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        Map result = [ : ];

        // Must have been supplied the requesting institution symbol
        if (params.requestingInstitutionSymbol) {
			Symbol symbol = illApplicationEventHandlerService.resolveCombinedSymbol(params.requestingInstitutionSymbol);
			
            if (symbol == null) {
                result.error = "Unable to determine requesting institution for symbol: " + params.requestingInstitutionSymbol;
            } else  {
                PatronRequest.withTransaction { tstatus ->
                    try {
						// create a new PatronRequest, Note this should never get saved
						PatronRequest patronRequest = new PatronRequest();
						patronRequest.title = params.title;
						patronRequest.isbn = params.isbn;
						patronRequest.supplierUniqueRecordId = params.supplierItemId;
						patronRequest.resolvedRequester = symbol;
						patronRequest.institution = getInstitution();
						
                        // Now we can make the call
                        IHoldingLogDetails holdingLogDetails = new HoldingLogDetails(ProtocolType.Z3950_RESPONDER, ProtocolMethod.GET);
                        result.itemLocation = z3950SearchService.locate(patronRequest, null, holdingLogDetails);
                        result.logging = holdingLogDetails.toMap();
						
						// Not forgetting to discard the patron request
						patronRequest.discard();
                    } catch (Exception e) {
                        log.error("Exception thrown, while locating external institutions", e);
                        result.error = "Exception: " + e.getMessage();
                    }
                }
            }
        } else {
            result.error = "Symbol not supplied";
        }

        render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

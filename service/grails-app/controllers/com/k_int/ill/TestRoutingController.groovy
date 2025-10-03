package com.k_int.ill;

import com.k_int.OkapiTenantAwareInstitutionController;
import com.k_int.directory.Symbol;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.logging.HoldingLogDetails;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.routing.RequestRouter;
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
@Api(value = "/ill/testRouting", tags = ["Test Routing"])
@OkapiApi(name = "testRouting")
@ExcludeFromGeneratedCoverageReport
@CurrentTenant
public class TestRoutingController extends OkapiTenantAwareInstitutionController<PatronRequest> {

	IllApplicationEventHandlerService illApplicationEventHandlerService;
    RequestRouterService requestRouterService;

	public TestRoutingController() {
        super(PatronRequest);
	}

    /**
     * Finds more suppliers for the institution and routing
     * @return The result from the determineBestLocation call
     */
    @ApiOperation(
        value = "View the results from request routing",
        nickname = "findMoreSuppliers",
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
            name = "requestRouter",
            paramType = "query",
            allowMultiple = false,
            required = true,
            value = "The request router to be used to locate the institutions",
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
            name = "author",
            paramType = "query",
            required = false,
            value = "Author of the item",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "isbn",
            paramType = "query",
            required = false,
            value = "ISBN of the item",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "issn",
            paramType = "query",
            required = false,
            value = "ISSN of the item",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "findMoreSuppliers", permissionGroup = PermissionGroup.READ)
    public def findMoreSuppliers() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_FIND_MORE_SUPPLIERS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        Map result = [ : ];

        // Must have been supplied the requesting institution symbol
        if (params.requestingInstitutionSymbol) {
			Symbol symbol = illApplicationEventHandlerService.resolveCombinedSymbol(params.requestingInstitutionSymbol);
			
            if (symbol == null) {
                result.error = "Unable to determine requesting institution for symbol: " + params.requestingInstitutionSymbol;
            } else  {
				if (params.requestRouter) {
					// Attempt to find the routing service
					RequestRouter requestRouter = requestRouterService.getRequestRouterFor(params.requestRouter);
					if (requestRouter == null) {
							result.error = "Unable to locate the request router for " + params.requestRouter;
					} else {
		                PatronRequest.withTransaction { tstatus ->
		                    try {
								// create a new PatronRequest, Note this should never get saved
								PatronRequest patronRequest = new PatronRequest();
								patronRequest.title = params.title;
								patronRequest.author = params.author;
								patronRequest.isbn = params.isbn;
								patronRequest.issn = params.issn;
								patronRequest.supplierUniqueRecordId = params.supplierItemId;
								patronRequest.resolvedRequester = symbol;
								patronRequest.institution = getInstitution();
								
		                        // Now we can make the call
								IHoldingLogDetails holdingLogDetails = new HoldingLogDetails(ProtocolType.Z3950_REQUESTER, ProtocolMethod.GET);
								result.suppliers = requestRouter.findMoreSuppliers(patronRequest, holdingLogDetails);
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
					result.error = "Request Router not supplied";
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

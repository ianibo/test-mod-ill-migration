package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.ill.logging.ContextLogging;
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
@Api(value = "/ill/patron", tags = ["Patron"])
@OkapiApi(name = "patron")
@ExcludeFromGeneratedCoverageReport
public class PatronController extends OkapiTenantAwareSwaggerController<Patron>  {

    private static final String RESOURCE_PATRON = Patron.getSimpleName();

    public PatronController() {
        super(Patron)
    }

    IllActionService illActionService;

    /**
     * Looks up the patron to see if the profile they belong to is allowed to make requests
     * @return a map containing the result of the call that can contain the following fields:
     *      patronValid ... can the patron create requests
     *      problems ... An array of reasons that explains either a FAIL or the patron is not valid
     *      status ... the status of the patron (FAIL or OK)
     */
    @ApiOperation(
        value = "Is the user allowed to make requests",
        nickname = "{patronIdentifier}/canCreateRequest",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "patronIdentifier",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The identifier of the patron",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "canCreateRequest", permissionGroup = PermissionGroup.WRITE)
    public def canCreateRequest() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_PATRON);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_CAN_CREATE_REQUEST);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        Map result = [ : ];
        Patron.withTransaction { status ->
          // We do need to be supplied a patronId
          if (params?.patronIdentifier == null) {
            response.status = 400;
            result.message = 'No patron identifier supplied to perform the check';
          } else {
            // Lookup the patron
            result = illActionService.lookupPatron(getInstitution(), [ patronIdentifier : params.patronIdentifier ]);

            // Remove the patron details and callSuccess as they should not be passed back
            result.remove('callSuccess');
            result.remove('patronDetails');
          }
        }
        render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

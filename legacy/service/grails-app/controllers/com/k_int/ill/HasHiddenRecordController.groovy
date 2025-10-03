package com.k_int.ill;

import org.springframework.http.HttpStatus;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.artefact.Artefact;
import grails.gorm.multitenancy.CurrentTenant;
import grails.gorm.transactions.Transactional;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@CurrentTenant
@Artefact('Controller')
@ExcludeFromGeneratedCoverageReport
public class HasHiddenRecordController<T> extends OkapiTenantAwareSwaggerController<T> {

    private final static String PROPERTY_HIDDEN = 'hidden';

    public HasHiddenRecordController(Class<T> resource) {
        super(resource)
    }

    @ApiOperation(
        value = "Search with the supplied criteria",
        nickname = "/",
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
            required = false,
            allowMultiple = false,
            value = "The term to be searched for",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "match",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The properties the match is to be applied to",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "filters",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The filters to be applied",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "sort",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The properties to sort the items by",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "max",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Maximum number of items to return",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "perPage",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Number of items per page",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "offset",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Offset from the becoming of the result set to start returning results",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "page",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "The page you wnat the results being returned from",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "stats",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Do we return statistics about the search",
            dataType = "boolean"
        )
    ])
    @Override
    @OkapiPermission(name = "collection", permissionGroup = PermissionGroup.READ)
    public def index(Integer max) {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, resource.getSimpleName());
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_SEARCH);
        ContextLogging.setValue(ContextLogging.FIELD_TERM, params.term);
        ContextLogging.setValue(ContextLogging.FIELD_FIELDS_TO_MATCH, params.match);
        ContextLogging.setValue(ContextLogging.FIELD_FILTERS, params.filters);
        ContextLogging.setValue(ContextLogging.FIELD_SORT, params.sort);
        ContextLogging.setValue(ContextLogging.FIELD_MAXIMUM_RESULTS, max);
        ContextLogging.setValue(ContextLogging.FIELD_NUMBER_PER_PAGE, params.perPage);
        ContextLogging.setValue(ContextLogging.FIELD_OFFSET, params.offset);
        ContextLogging.setValue(ContextLogging.FIELD_PAGE, params.page);
        ContextLogging.setValue(ContextLogging.FIELD_STATISTICS_REQUIRED, params.stats);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Closure definition can be found at https://gorm.grails.org/latest/hibernate/manual/#criteria
        respond doTheLookup(resource, {
            or {
                // How can I user PROPERTY_HIDDEN here, as it just tried to used PROPERTY_HIDDEN as the property name rether than its value
                eq('hidden', false)
                isNull('hidden')
            }
        });

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @Transactional
    @Override
    // Not quite sure why it dosn't inherit the @ApiImplicitParams from the paren
    @ApiOperation(
        value = "Deletes the record with the supplied identifier",
        nickname = "{id}",
        httpMethod = "DELETE"
    )
    @ApiResponses([
        @ApiResponse(code = 204, message = "Deleted")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The id of the record to be deleted",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "delete", permissionGroup = PermissionGroup.ALL)
    public def delete() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, resource.getSimpleName());
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_DELETE);
        ContextLogging.setValue(ContextLogging.FIELD_ID, params.id);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        T instance = queryForResource(params.id)

        // Not found.
        if (instance == null) {
            transactionStatus.setRollbackOnly();
            notFound();
        } else {
            // Return the relevant status if not allowed to delete.
            Map deletable = instance.metaClass.respondsTo(instance, 'canDelete') ? instance.canDelete() : [ deleteValid: true ];

            if (deletable.deleteValid) {
                // Delete the instance
                deleteResource instance
            } else {
                log.info('Marking ' + instance.class.name + ':' +params.getIdentifier() + ' as hidden because ' + deletable.error)
                instance.hidden = true;
                instance.save(flush:true, failOnError:true);
            }

            // It has either been deleted or marked as hidden, either way the user thinks it has been deleted
            render status : HttpStatus.NO_CONTENT;
        }

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

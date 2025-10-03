package com.k_int;

import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;
import com.k_int.web.toolkit.refdata.GrailsDomainRefdataHelpers;
import com.k_int.web.toolkit.refdata.RefdataCategory;
import com.k_int.web.toolkit.refdata.RefdataValue;
import com.k_int.web.toolkit.utils.DomainUtils;

import grails.gorm.multitenancy.CurrentTenant;
import grails.gorm.transactions.Transactional;
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
@Api(value = "/ill/refdata", tags = ["Reference Data"])
@OkapiApi(name = "refdata")
@ExcludeFromGeneratedCoverageReport
public class RefdataController extends OkapiTenantAwareSwaggerController<RefdataCategory> {

    public RefdataController() {
        super(RefdataCategory)
    }

    @ApiOperation(
        value = "Returns all the category values for the supplied domain and property",
        nickname = "{domain}/{property}",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "domain",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The domain to look for the property",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "property",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The property that defines the category",
            dataType = "string"
        ),
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
    @OkapiPermission(name = "collectionPropertyLookup", permissionGroup = PermissionGroup.READ)
    @Transactional(readOnly=true)
    public def lookup (String domain, String property) {
        def c = DomainUtils.resolveDomainClass(domain)?.javaClass;
        def cat = c ? GrailsDomainRefdataHelpers.getCategoryString(c, property) : null;

        // Bail if no cat.
        if (!cat) {
          render status: 404
        } else {

            // SO: THis needs addressing in the superclasses. Shouldn't have to repeat this here.
            final int offset = params.int("offset") ?: 0
            final int perPage = Math.min(params.int('perPage') ?: params.int('max') ?: 10, 100)
            final int page = params.int("page") ?: (offset ? (offset / perPage) + 1 : 1)
            final List<String> filters = ["owner.desc==${cat}"]
            final List<String> match_in = params.list("match[]") ?: params.list("match")
            final List<String> sorts = params.list("sort[]") ?: params.list("sort")
            if (params.boolean('stats')) {
                respond simpleLookupService.lookupWithStats(RefdataValue, params.term, perPage, page, filters, match_in, sorts, null)
            } else {
                respond simpleLookupService.lookup(RefdataValue, params.term, perPage, page, filters, match_in, sorts)
            }
        }
    }
}

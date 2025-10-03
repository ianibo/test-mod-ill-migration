package com.k_int.ill;

import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.shared.TenantSymbolMapping;
import com.k_int.okapi.OkapiTenantAwareController;

import grails.converters.JSON;
import grails.gorm.multitenancy.CurrentTenant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

/**
 * This controller is hit by a timer from OKAPI every 2 minutes/
 * To make use of the generic database functionality we need to pass it a domain,
 * Which gives this controller more functionality than it needs or requires but gives us a database connection to work with.
 *
 * @author Chas
 *
 */
@CurrentTenant
@Api(value = "/ill/settings", tags = ["Ill Settings"])
// We do not want this controller appearing in the generated module descriptor template
@ExcludeFromGeneratedCoverageReport
public class IllSettingsController extends OkapiTenantAwareController<TenantSymbolMapping> {

    BackgroundTaskService backgroundTaskService;

    public IllSettingsController() {
        super(TenantSymbolMapping);
    }

    @ApiOperation(
        value = "Triggers the background tasks for the instance",
        nickname = "worker",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    public def worker() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, resource.getSimpleName());
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_WORKER);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        def result = [result:'OK'];
        String tenant = request.getHeader('X-OKAPI-TENANT')
        log.info("worker call start tenant: ${tenant}");
        try {
            // Just perform the background tasks, we do not need to start a new transaction as it grabs a transaction when needed
            backgroundTaskService.performIllTasks(tenant);
        } catch ( Exception e ) {
            log.error("Problem in background task service",e);
        }
        render result as JSON

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}

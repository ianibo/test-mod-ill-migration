package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;
import com.k_int.ill.statemodel.ActionResult;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class TimerSpec extends TestBase {

	private static final String FIELD_DESCRIPTION = "description";

	private static final String CONTEXT_TIMER_ID = "timerId";
	private static final String CONTEXT_TIMER_DESCRIPTION = "timerDescription";

	private static final String TIMER_DESCRIPTION = "A timer";
	private static final String TIMER_DESCRIPTION_CHANGED = "Timer description has been changed";

    // This method is declared in the HttpSpec
    def setupSpecWithSpring() {
        super.setupSpecWithSpring();
    }

    def setupSpec() {
    }

    def setup() {
    }

    def cleanup() {
    }

    void "Attempt to delete any old tenants"(tenantid, name) {
        when:"We post a delete request"
            boolean result = deleteTenant(tenantid, name);

        then:"Any old tenant removed"
            assert(result);

        where:
            tenantid     | name
            TENANT_ONE   | TENANT_ONE
    }

    void "Set up test tenants"(String tenantId, String name) {
        when:"We post a new tenant request to the OKAPI controller"
            boolean response = setupTenant(tenantId, name);

        then:"The response is correct"
            assert(response);

        where:
            tenantId   | name
            TENANT_ONE | TENANT_ONE
    }

    void "Create a new Timer"(
        String tenantId,
        String code,
        String description,
        String rrule,
        long lastExecution,
        long nextExecution,
        String taskCode,
        String taskConfig,
        boolean enabled,
        boolean executeAtDayStart
    ) {
        when:"Create a new Timer"

            // Create the Timer
            Map timer = [
                code : code,
                description : description,
                rrule : rrule,
                lastExecution : lastExecution,
                nextExecution : nextExecution,
                taskCode : taskCode,
                taskConfig : taskConfig,
                enabled : enabled,
                executeAtDayStart : executeAtDayStart
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_TIMERS,
				timer,
				CONTEXT_TIMER_ID,
				FIELD_ID,
				CONTEXT_TIMER_DESCRIPTION,
				FIELD_DESCRIPTION
			);


        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_DESCRIPTION] == description);

        where:
            tenantId   | code   | description         | rrule   | lastExecution | nextExecution | taskCode | taskConfig | enabled | executeAtDayStart
            TENANT_ONE | 'test' | TIMER_DESCRIPTION   | "rrule" | 0             | 1             | "Timer"  | "{}"       | false   | true
    }

    void "Fetch a specific Timer"(String tenantId, String ignore) {
        when:"Fetch the Timer"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_TIMERS,
				testctx[CONTEXT_TIMER_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_TIMER_ID]);
            assert(restResult.responseBody[FIELD_DESCRIPTION] == testctx[CONTEXT_TIMER_DESCRIPTION]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for Timers"(String tenantId, String ignore) {
        when:"Search for Timers"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_TIMERS,
				FIELD_DESCRIPTION,
				testctx[CONTEXT_TIMER_DESCRIPTION]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_TIMER_ID]);
			assert(restResult.responseBody[0][FIELD_DESCRIPTION] == testctx[CONTEXT_TIMER_DESCRIPTION]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update Timer description"(String tenantId, description) {
        when:"Update description for Timer"

            Map timer = [
                description : description
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_TIMERS,
				testctx[CONTEXT_TIMER_ID].toString(),
				timer
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_DESCRIPTION] == description);

        where:
            tenantId   | description
            TENANT_ONE | TIMER_DESCRIPTION_CHANGED
    }

    void "Delete a Timer"(String tenantId, String ignore) {
        when:"Delete a Timer"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_TIMERS,
				testctx[CONTEXT_TIMER_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Execute a Timer"(String tenantId, String timerCode) {
        when:"Execute a Timer"
            // Just call the base class
            RestResult restResult = fetchObject(
                tenantId,
                PATH_TIMERS_EXECUTE,
                null,
                [ (FIELD_CODE) : timerCode ]
            );

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody != null);
            assert(restResult.responseBody[0].result == ActionResult.SUCCESS.toString());

        where:
            tenantId   | timerCode
            TENANT_ONE | "CheckForOverdueSupplierRequests"
            TENANT_ONE | "CheckForStaleSupplierRequests"
            TENANT_ONE | "ProcessPatronNotices"
            TENANT_ONE | "RequestNetworkRetry"
            TENANT_ONE | "RequestNetworkTimeout"
            TENANT_ONE | "RemoveFromProtocolAudit"
    }
}

package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class BackgroundTasksSpec extends TestBase {

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

    void "Set up test tenants"(String tenantId, String name) {
        when:"We post a new tenant request to the OKAPI controller"
            boolean response = setupTenant(tenantId, name);

        then:"The response is correct"
            assert(response);

        where:
            tenantId   | name
            TENANT_ONE | TENANT_ONE
    }

    void "Run the background tasks"(String tenantId, String ignore) {
        when:"Execute the background tasks"

            // Call the base method to fetch it
            RestResult restResult = fetchObject(
                tenantId,
                PATH_SETTINGS_WORKER,
                null
            );

        then:"Check we have a valid response"
            // Check the various fields
            assert(restResult.responseBody != null);
            assert(restResult.responseBody.result == "OK");

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

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
class SharedIndexQuerySpec extends TestBase {

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

    void "Error when not configured"() {
        when:"Fetch the raml"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(TENANT_ONE, "sharedIndexQuery", null);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult);
            assert(restResult.responseBody != null);
            assert(restResult.responseBody.result != null);
            assert(restResult.responseBody.result == ActionResult.ERROR.toString());
            assert(restResult.responseBody.messages != null);
            assert(restResult.responseBody.messages.size() == 1);
    }
}

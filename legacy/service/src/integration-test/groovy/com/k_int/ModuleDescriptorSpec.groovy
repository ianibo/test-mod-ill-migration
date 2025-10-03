package com.k_int;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class ModuleDescriptorSpec extends TestBase {

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

    void "Generate the moduole descriptor template"() {
        when:"Fetch the generated template"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(TENANT_ONE, "moduleDescriptor/generate", null, [validate : true]);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.responseBody);
            assert(restResult.responseBody.id != null);
            assert(restResult.responseBody.name != null);
    }
}

package com.k_int.directory;

import com.k_int.RestResult;
import com.k_int.TestBase;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class ServiceSpec extends TestBase {

	private static String FIELD_NAME = "name";

	private static String CONTEXT_SERVICE_ID = "serviceId";
	private static String CONTEXT_SERVICE_NAME = "serviceName";

	private static final String SERVICE_NAME = "testService";
	private static final String SERVICE_NAME_CHANGED = "changed";

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

    @Override
	protected void tidyUpOnTenantSetup(String tenantId) {
		// Deletes any data we may have created previously but wasn't deleted
		searchAndDelete(
			tenantId,
			PATH_SERVICE,
			FIELD_NAME,
			[ SERVICE_NAME, SERVICE_NAME_CHANGED ]
		);
	}

    void "Set up test tenants"(String tenantId, String name) {
        when:"We post a new tenant request to the OKAPI controller"
            boolean result = setupTenant(tenantId, name);

        then:"The response is correct"
            assert(result);

        where:
            tenantId   | name
            TENANT_ONE | TENANT_ONE
    }

    void "Create a new service"(String tenantId, String name) {
        when:"Create a new service"
			RestResult restResult = null;

			// Create ourselves a new service type
			RefdataValue serviceType = createRefererenceData(tenantId, "Service.Type", 'testService');

			// Create a map that represents a service that is to be created
			Map serviceMap = [
				type : [ id: serviceType.id ],
				address : "http://localhost/account",
				name : name
			];

			// Lets us call the base class to post it
			restResult = createNewObject(
				tenantId,
				PATH_SERVICE,
				serviceMap,
				CONTEXT_SERVICE_ID,
				FIELD_ID,
				CONTEXT_SERVICE_NAME,
				FIELD_NAME
			);

        then:"Check we have a valid response"
			assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | SERVICE_NAME
    }

    void "Fetch a specific service"(String tenantId, String ignore) {
        when:"Fetch the service"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(tenantId, PATH_SERVICE, testctx[CONTEXT_SERVICE_ID].toString());

        then:"Check we have a valid response"
            // Check the id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_SERVICE_ID]);
			assert(restResult.responseBody[FIELD_NAME] == testctx[CONTEXT_SERVICE_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for services"(String tenantId, String ignore) {

        when:"Search for services"
			RestResult restResult = searchForObjects(tenantId, PATH_SERVICE, FIELD_NAME, testctx[CONTEXT_SERVICE_NAME]);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_NAME] == testctx[CONTEXT_SERVICE_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update service slug"(String tenantId, String name) {
        when:"Update the name for the service"
            Map serviceAccount = [
                name : name
            ];

			RestResult restResult = updateObject(tenantId, PATH_SERVICE, testctx[CONTEXT_SERVICE_ID].toString(), serviceAccount);

        then:"Check we have a valid response"
            // Check the name
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | SERVICE_NAME_CHANGED
    }

// Have commented out the test for delete as delete is not allowed. If it is allowed in the future just uncomment the test
    void "Delete a service"(String tenantId, String ignore) {
        when:"Delete a service"
			RestResult restResult = deleteObject(tenantId, PATH_SERVICE, testctx[CONTEXT_SERVICE_ID].toString());

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

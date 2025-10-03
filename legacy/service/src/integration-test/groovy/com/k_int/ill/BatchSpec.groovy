package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class BatchSpec extends TestBase {

	private static final String FIELD_CONTEXT = "context";

	private static final String CONTEXT_BATCH_ID = "batchId";
	private static final String CONTEXT_BATCH_CONTEXT = "batchContext";

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

    void "Create a new batch"(String tenantId, String context, String description) {
        when:"Create a new batch"
            // Create a map that represents a batch that is to be created
            Map batch = [
                context : context,
                description : description
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_BATCH,
				batch,
				CONTEXT_BATCH_ID,
				FIELD_ID,
				CONTEXT_BATCH_CONTEXT,
				FIELD_CONTEXT
			);

        then:"Check we have a valid response"
			assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_CONTEXT] == context);

        where:
            tenantId   | context | description
            TENANT_ONE | 'test'  | "A simple batch created for the test"
    }

    void "Fetch a specific batch"(String tenantId, String ignore) {
        when:"Fetch the batch"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(tenantId, PATH_BATCH, testctx[CONTEXT_BATCH_ID].toString());

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_BATCH_ID]);
            assert(restResult.responseBody[FIELD_CONTEXT] == testctx[CONTEXT_BATCH_CONTEXT]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for batches"(String tenantId, String ignore) {

        when:"Search for batches"
			RestResult restResult = searchForObjects(tenantId, PATH_BATCH, FIELD_CONTEXT, testctx[CONTEXT_BATCH_CONTEXT]);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_CONTEXT] == testctx[CONTEXT_BATCH_CONTEXT]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update batch context"(String tenantId, String context) {
        when:"Update context for batch"
            Map batch = [
                context : context
            ];

			RestResult restResult = updateObject(tenantId, PATH_BATCH, testctx[CONTEXT_BATCH_ID].toString(), batch);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_CONTEXT] == context);

        where:
            tenantId   | context
            TENANT_ONE | "changed"
    }

    void "Delete a batch"(String tenantId, String ignore) {
        when:"Delete a batch"

			RestResult restResult = deleteObject(tenantId, PATH_BATCH, testctx[CONTEXT_BATCH_ID].toString());

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

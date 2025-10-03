package com.k_int.ill.itemSearch;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class SearchSpec extends TestBase {

	private static String CONTEXT_SEARCH_ID    = "searchId";
	private static String CONTEXT_DESCRIPTION  = "searchDescription";

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

    void "Create a new search"(String tenantId, String code, String description) {
        when:"Create a new search"
            // Create a map that represents a search that is to be created
            Map search = [
				code : code,
                description : description
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_SEARCH,
				search,
				CONTEXT_SEARCH_ID,
				FIELD_ID,
				CONTEXT_DESCRIPTION,
				FIELD_DESCRIPTION
			);

        then:"Check we have a valid response"
			assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_DESCRIPTION] == description);

        where:
            tenantId   | code      | description
            TENANT_ONE | "search1" | "Description for search 1"
    }

    void "Fetch a specific search"(String tenantId, String ignore) {
        when:"Fetch the search"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_SEARCH,
				testctx[CONTEXT_SEARCH_ID].toString()
			);

        then:"Check we have a valid response"
            // Check the id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_SEARCH_ID]);
			assert(restResult.responseBody[FIELD_DESCRIPTION] == testctx[CONTEXT_DESCRIPTION]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for searches"(String tenantId, String ignore) {

        when:"Search for searches"
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_SEARCH,
				FIELD_DESCRIPTION,
				testctx[CONTEXT_DESCRIPTION]
			);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_DESCRIPTION] == testctx[CONTEXT_DESCRIPTION]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update search description"(String tenantId, String description) {
        when:"Update description for search"
            Map search = [
                description : description
            ];

			RestResult restResult = updateObject(
				tenantId,
				PATH_SEARCH,
				testctx[CONTEXT_SEARCH_ID].toString(),
				search
			);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_DESCRIPTION] == description);

        where:
            tenantId   | description
            TENANT_ONE | "Changed description"
    }

    void "Delete a search"(String tenantId, String ignore) {
        when:"Delete a search"
			RestResult restResult = deleteObject(
				tenantId,
				PATH_SEARCH,
				testctx[CONTEXT_SEARCH_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

package com.k_int.directory;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class DirectoryGroupSpec extends TestBase {

	private static String CONTEXT_DIRECTORY_GROUP_ID = "directoryGroupId";
	private static String CONTEXT_DESCRIPTION        = "directoryGroupDescription";

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

    void "Create a new directory group"(String tenantId, String code, String description) {
        when:"Create a new directory group"
            // Create a map that represents a directory group that is to be created
            Map directoryGroup = [
				code : code,
                description : description
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_DIRECTORY_GROUP,
				directoryGroup,
				CONTEXT_DIRECTORY_GROUP_ID,
				FIELD_ID,
				CONTEXT_DESCRIPTION,
				FIELD_DESCRIPTION
			);

        then:"Check we have a valid response"
			assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_DESCRIPTION] == description);

        where:
            tenantId   | code     | description
            TENANT_ONE | "group1" | "Description for group 1"
    }

    void "Fetch a specific directory group"(String tenantId, String ignore) {
        when:"Fetch the directory group"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_DIRECTORY_GROUP,
				testctx[CONTEXT_DIRECTORY_GROUP_ID].toString()
			);

        then:"Check we have a valid response"
            // Check the id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_DIRECTORY_GROUP_ID]);
			assert(restResult.responseBody[FIELD_DESCRIPTION] == testctx[CONTEXT_DESCRIPTION]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for directory groups"(String tenantId, String ignore) {

        when:"Search for directory groups"
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_DIRECTORY_GROUP,
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

    void "Update directory group description"(String tenantId, String description) {
        when:"Update description for directory group"
            Map directoryGroup = [
                description : description
            ];

			RestResult restResult = updateObject(
				tenantId,
				PATH_DIRECTORY_GROUP,
				testctx[CONTEXT_DIRECTORY_GROUP_ID].toString(),
				directoryGroup
			);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_DESCRIPTION] == description);

        where:
            tenantId   | description
            TENANT_ONE | "Changed description"
    }

    void "Delete a directory group"(String tenantId, String ignore) {
        when:"Delete a directory group"
			RestResult restResult = deleteObject(
				tenantId,
				PATH_DIRECTORY_GROUP,
				testctx[CONTEXT_DIRECTORY_GROUP_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

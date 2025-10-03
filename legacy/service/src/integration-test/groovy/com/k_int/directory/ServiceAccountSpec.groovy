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
class ServiceAccountSpec extends TestBase {

	private static String FIELD_SLUG = "slug";

	private static String CONTEXT_SERVICE_ACCOUNT_ID = "serviceAccountId";
	private static String CONTEXT_SERVICE_ACCOUNT_SLUG = "serviceAccountSlug";

	private static final String SERVICE_ACCOUNT_SLUG = "testServiceAccount";
	private static final String SERVICE_ACCOUNT_SLUG_CHANGED = "changed";

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
			PATH_SERVICE_ACCOUNT,
			FIELD_SLUG,
			[ TAG_VALUE, TAG_CHANGED_VALUE ]
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

    void "Create a new service account"(String tenantId, String slug) {
        when:"Create a new service account"
			RestResult restResult = null;

			// Create ourselves a new service type
			RefdataValue serviceType = createRefererenceData(tenantId, "Service.Type", 'testServiceAccount');

		    // Now for a service
			RestResult restResultService = createNewObjectIfNotExists(
				tenantId,
				PATH_SERVICE,
				[ address : "http://testServiceAccount", name: "Test service account", type : [ id : serviceType.id ] ],
				"name"
			);
			if (restResultService.success) {
				// Now create a directory entry
				RestResult restResultDirectoryEntry = createNewObjectIfNotExists(
					tenantId,
					PATH_DIRECTORY_ENTRY,
					[ id : "testingServiceAccount",  name : "Testing service accounts", slug : "serviceAccountDE" ],
					"name"
				);
				if (restResultDirectoryEntry.success) {
					// Create a map that represents a service account that is to be created
					Map serviceAccountMap = [
						service : [ id : restResultService.responseBody[FIELD_ID] ],
						accountHolder : [ id : restResultDirectoryEntry.responseBody[FIELD_ID] ],
						slug : slug
					];

					// Lets us call the base class to post it
					restResult = createNewObject(
						tenantId,
						PATH_SERVICE_ACCOUNT,
						serviceAccountMap,
						CONTEXT_SERVICE_ACCOUNT_ID,
						FIELD_ID,
						CONTEXT_SERVICE_ACCOUNT_SLUG,
						FIELD_SLUG
					);
				}
			}

        then:"Check we have a valid response"
			assert(restResult != null);
			assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_SLUG] == slug);

        where:
            tenantId   | slug
            TENANT_ONE | SERVICE_ACCOUNT_SLUG
    }

    void "Fetch a specific service account"(String tenantId, String ignore) {
        when:"Fetch the service account"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(tenantId, PATH_SERVICE_ACCOUNT, testctx[CONTEXT_SERVICE_ACCOUNT_ID].toString());

        then:"Check we have a valid response"
            // Check the id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_SERVICE_ACCOUNT_ID]);
			assert(restResult.responseBody[FIELD_SLUG] == testctx[CONTEXT_SERVICE_ACCOUNT_SLUG]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for service accounts"(String tenantId, String ignore) {

        when:"Search for service accounts"
			RestResult restResult = searchForObjects(tenantId, PATH_SERVICE_ACCOUNT, FIELD_SLUG, testctx[CONTEXT_SERVICE_ACCOUNT_SLUG]);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_SLUG] == testctx[CONTEXT_SERVICE_ACCOUNT_SLUG]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update service account slug"(String tenantId, String slug) {
        when:"Update slug for service account"
            Map serviceAccount = [
                slug : slug
            ];

			RestResult restResult = updateObject(tenantId, PATH_SERVICE_ACCOUNT, testctx[CONTEXT_SERVICE_ACCOUNT_ID].toString(), serviceAccount);

        then:"Check we have a valid response"
            // Check the slug
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_SLUG] == slug);

        where:
            tenantId   | slug
            TENANT_ONE | SERVICE_ACCOUNT_SLUG_CHANGED
    }

    void "Delete a service account"(String tenantId, String ignore) {
        when:"Delete a service account"
			RestResult restResult = deleteObject(tenantId, PATH_SERVICE_ACCOUNT, testctx[CONTEXT_SERVICE_ACCOUNT_ID].toString());

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

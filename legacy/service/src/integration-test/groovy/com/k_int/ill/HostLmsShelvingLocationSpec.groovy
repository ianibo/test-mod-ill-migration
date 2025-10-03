package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class HostLmsShelvingLocationSpec extends TestBase {

	private static final String FIELD_NAME = "name";

	private static final String CONTEXT_HOST_LMS_SHELVING_LOCATIONS_ID = "hostLMSShelvingLocationsId";
	private static final String CONTEXT_HOST_LMS_SHELVING_LOCATIONS_NAME = "hostLMSShelvingLocationsName";

	private static final String HOST_LMS_SHELVING_LOCATIONS_NAME = "A shelving location";
	private static final String HOST_LMS_SHELVING_LOCATIONS_NAME_CHANGED = "Name has been changed";

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
			PATH_HOST_LMS_SHELVING_LOCATIONS,
			FIELD_NAME,
			[ HOST_LMS_SHELVING_LOCATIONS_NAME, HOST_LMS_SHELVING_LOCATIONS_NAME_CHANGED ]
		);
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

    void "Create a new HostLMSShelvingLocation"(String tenantId, String code, String name, long supplyPreference, boolean hidden) {
        when:"Create a new HostLMSShelvingLocation"

            // Create the HostLMSShelvingLocation
            Map hostLMSShelvingLocation = [
                code : code,
                name : name,
                supplyPreference: supplyPreference,
                hidden: hidden
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_HOST_LMS_SHELVING_LOCATIONS,
				hostLMSShelvingLocation,
				CONTEXT_HOST_LMS_SHELVING_LOCATIONS_ID,
				FIELD_ID,
				CONTEXT_HOST_LMS_SHELVING_LOCATIONS_NAME,
				FIELD_NAME
			);

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | code   | name                             | supplyPreference | hidden
            TENANT_ONE | 'test' | HOST_LMS_SHELVING_LOCATIONS_NAME | 105              | false
    }

    void "Fetch a specific HostLMSShelvingLocation"(String tenantId, String ignore) {
        when:"Fetch the HostLMSShelvingLocation"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_HOST_LMS_SHELVING_LOCATIONS,
				testctx[CONTEXT_HOST_LMS_SHELVING_LOCATIONS_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_HOST_LMS_SHELVING_LOCATIONS_ID]);
            assert(restResult.responseBody[FIELD_NAME] == testctx[CONTEXT_HOST_LMS_SHELVING_LOCATIONS_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for HostLMSShelvingLocations"(String tenantId, String ignore) {
        when:"Search for HostLMSShelvingLocations"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_HOST_LMS_SHELVING_LOCATIONS,
				FIELD_NAME,
				testctx[CONTEXT_HOST_LMS_SHELVING_LOCATIONS_NAME]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_HOST_LMS_SHELVING_LOCATIONS_ID]);
			assert(restResult.responseBody[0][FIELD_NAME] == testctx[CONTEXT_HOST_LMS_SHELVING_LOCATIONS_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update HostLMSShelvingLocation name"(String tenantId, String name) {
        when:"Update name for HostLMSShelvingLocation"

            Map hostLMSShelvingLocation = [
                name : name
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_HOST_LMS_SHELVING_LOCATIONS,
				testctx[CONTEXT_HOST_LMS_SHELVING_LOCATIONS_ID].toString(),
				hostLMSShelvingLocation
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | HOST_LMS_SHELVING_LOCATIONS_NAME_CHANGED
    }

    void "Delete a HostLMSShelvingLocation"(String tenantId, String ignore) {
        when:"Delete a HostLMSShelvingLocation"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_HOST_LMS_SHELVING_LOCATIONS,
				testctx[CONTEXT_HOST_LMS_SHELVING_LOCATIONS_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

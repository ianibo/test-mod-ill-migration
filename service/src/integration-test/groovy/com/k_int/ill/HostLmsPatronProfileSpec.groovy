package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class HostLmsPatronProfileSpec extends TestBase {

	private static final String FIELD_NAME = "name";

	private static final String CONTEXT_HOST_LMS_PATRON_PROFILE_ID = "hostLMSPatronProfileId";
	private static final String CONTEXT_HOST_LMS_PATRON_PROFILE_NAME = "hostLMSPatronProfileName";

	private static final String HOST_LMS_PATRON_PROFILE_NAME = "A patron profile";
	private static final String HOST_LMS_PATRON_PROFILE_NAME_CHANGED = "Name has been changed";

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
			PATH_HOST_LMS_PATRON_PROFILES,
			FIELD_NAME,
			[ HOST_LMS_PATRON_PROFILE_NAME, HOST_LMS_PATRON_PROFILE_NAME_CHANGED ]
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

    void "Create a new HostLMSPatronProfile"(String tenantId, String code, String name, boolean canCreateRequests, boolean hidden) {
        when:"Create a new HostLMSPatronProfile"

            // Create the HostLMSPatronProfile
            Map hostLMSPatronProfile = [
                code : code,
                name : name,
                canCreateRequests: canCreateRequests,
                hidden: hidden
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_HOST_LMS_PATRON_PROFILES,
				hostLMSPatronProfile,
				CONTEXT_HOST_LMS_PATRON_PROFILE_ID,
				FIELD_ID,
				CONTEXT_HOST_LMS_PATRON_PROFILE_NAME,
				FIELD_NAME
			);

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | code   | name                         | canCreateRequests | hidden
            TENANT_ONE | 'test' | HOST_LMS_PATRON_PROFILE_NAME | true              | false
    }

    void "Fetch a specific HostLMSPatronProfile"(String tenantId, String ignore) {
        when:"Fetch the HostLMSPatronProfile"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_HOST_LMS_PATRON_PROFILES,
				testctx[CONTEXT_HOST_LMS_PATRON_PROFILE_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_HOST_LMS_PATRON_PROFILE_ID]);
            assert(restResult.responseBody[FIELD_NAME] == testctx[CONTEXT_HOST_LMS_PATRON_PROFILE_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for HostLMSPatronProfiles"(String tenantId, String ignore) {
        when:"Search for HostLMSPatronProfiles"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_HOST_LMS_PATRON_PROFILES,
				FIELD_NAME,
				testctx[CONTEXT_HOST_LMS_PATRON_PROFILE_NAME]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_HOST_LMS_PATRON_PROFILE_ID]);
			assert(restResult.responseBody[0][FIELD_NAME] == testctx[CONTEXT_HOST_LMS_PATRON_PROFILE_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update HostLMSPatronProfile name"(String tenantId, String name) {
        when:"Update name for HostLMSPatronProfile"

            Map hostLMSPatronProfile = [
                name : name
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_HOST_LMS_PATRON_PROFILES,
				testctx[CONTEXT_HOST_LMS_PATRON_PROFILE_ID].toString(),
				hostLMSPatronProfile
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | HOST_LMS_PATRON_PROFILE_NAME_CHANGED
    }

    void "Delete a HostLMSPatronProfile"(String tenantId, String ignore) {
        when:"Delete a HostLMSPatronProfile"

			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_HOST_LMS_PATRON_PROFILES,
				testctx[CONTEXT_HOST_LMS_PATRON_PROFILE_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

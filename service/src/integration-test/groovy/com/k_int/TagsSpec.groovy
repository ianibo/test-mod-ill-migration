package com.k_int;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class TagsSpec extends TestBase {

	private static final String FIELD_VALUE = "value";

	private static final String CONTEXT_TAG_ID = "tagId";
	private static final String CONTEXT_TAG_VALUE = "tagValue";

	private static final String TAG_VALUE = "test tag";
	private static final String TAG_VALUE_CHANGED = "changed";

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
			PATH_TAGS,
			FIELD_VALUE,
			[ TAG_VALUE, TAG_VALUE_CHANGED ]
		);
	}

    void "Set up test tenants"(String tenantId, String name) {
        when:"We post a new tenant request to the OKAPI controller"
            boolean response = setupTenant(tenantId, name);
			if (response) {
				// Clear down any data that these test may have previously created, so we do not need to delete the tenants first

			}

        then:"The response is correct"
            assert(response);

        where:
            tenantId   | name
            TENANT_ONE | TENANT_ONE
    }

    void "Create a new tag"(String tenantId, String value) {
        when:"Create a new tag"
            // Create a map that represents a tag that is to be created
            Map tag = [
                value : value,
                norm_value : value.toLowerCase()
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_TAGS,
				tag,
				CONTEXT_TAG_ID,
				FIELD_ID,
				CONTEXT_TAG_VALUE,
				FIELD_VALUE
			);

        then:"Check we have a valid response"
			assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_VALUE] == value);

        where:
            tenantId   | value
            TENANT_ONE | TAG_VALUE
    }

    void "Fetch a specific tag"(String tenantId, String ignore) {
        when:"Fetch the tag"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(tenantId, PATH_TAGS, testctx[CONTEXT_TAG_ID].toString());

        then:"Check we have a valid response"
            // Check the value and id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_TAG_ID]);
            assert(restResult.responseBody[FIELD_VALUE] == testctx[CONTEXT_TAG_VALUE]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for tags"(String tenantId, String ignore) {

        when:"Search for tags"
			RestResult restResult = searchForObjects(tenantId, PATH_TAGS, FIELD_VALUE, testctx[CONTEXT_TAG_VALUE]);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_TAG_ID]);
            assert(restResult.responseBody[0][FIELD_VALUE] == testctx[CONTEXT_TAG_VALUE]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update tag value"(String tenantId, String value) {
        when:"Update value for tag"
            Map tag = [
                value : value
            ];

			RestResult restResult = updateObject(tenantId, PATH_TAGS, testctx[CONTEXT_TAG_ID].toString(), tag);

        then:"Check we have a valid response"
            // Check the value and id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_VALUE] == value);

        where:
            tenantId   | value
            TENANT_ONE | TAG_VALUE_CHANGED
    }

    void "Delete a tag"(String tenantId, String ignore) {
        when:"Delete a tag"
			RestResult restResult = deleteObject(tenantId, PATH_TAGS, testctx[CONTEXT_TAG_ID].toString());

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

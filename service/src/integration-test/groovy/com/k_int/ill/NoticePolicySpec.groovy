package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class NoticePolicySpec extends TestBase {

	private static final String FIELD_NAME = "name";

	private static final String CONTEXT_NOTICE_POLICY_ID = "noticePolicyId";
	private static final String CONTEXT_NOTICE_POLICY_NAME = "noticePolicyName";

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

    void "Create a new NoticePolicy"(String tenantId, String name, String description, boolean active) {
        when:"Create a new NoticePolicy"

            // Create the NoticePolicy
            Map noticePolicy = [
                name : name,
                description: description,
                active: active
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_NOTICE_POLICIES,
				noticePolicy,
				CONTEXT_NOTICE_POLICY_ID,
				FIELD_ID,
				CONTEXT_NOTICE_POLICY_NAME,
				FIELD_NAME
			);

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name   | description       | active
            TENANT_ONE | 'test' | "A notice policy" | true
    }

    void "Fetch a specific NoticePolicy"(String tenantId, String ignore) {
        when:"Fetch the NoticePolicy"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_NOTICE_POLICIES,
				testctx[CONTEXT_NOTICE_POLICY_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_NOTICE_POLICY_ID]);
            assert(restResult.responseBody[FIELD_NAME] == testctx[CONTEXT_NOTICE_POLICY_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for NoticePolicys"(String tenantId, String ignore) {
        when:"Search for NoticePolicys"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_NOTICE_POLICIES,
				FIELD_NAME,
				testctx[CONTEXT_NOTICE_POLICY_NAME]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_NOTICE_POLICY_ID]);
			assert(restResult.responseBody[0][FIELD_NAME] == testctx[CONTEXT_NOTICE_POLICY_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update NoticePolicy name"(String tenantId, String name) {
        when:"Update name for NoticePolicy"

            Map noticePolicy = [
                name : name
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_NOTICE_POLICIES,
				testctx[CONTEXT_NOTICE_POLICY_ID].toString(),
				noticePolicy
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | "Name has been changed"
    }

    void "Delete a NoticePolicy"(String tenantId, String ignore) {
        when:"Delete a NoticePolicy"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_NOTICE_POLICIES,
				testctx[CONTEXT_NOTICE_POLICY_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

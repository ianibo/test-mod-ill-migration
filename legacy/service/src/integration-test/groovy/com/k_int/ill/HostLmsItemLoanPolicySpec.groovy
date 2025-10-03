package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class HostLmsItemLoanPolicySpec extends TestBase {

	private static final String FIELD_NAME = "name";

	private static final String CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_ID = "hostLMSLoanPolicyId";
	private static final String CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_NAME = "hostLMSLoanPolicyName";

	private static final String HOST_LMS_ITEM_LOAN_POLICY_NAME = "A item loan policy";
	private static final String HOST_LMS_ITEM_LOAN_POLICY_NAME_CHANGED = "Name has been changed";

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
			PATH_HOST_LMS_ITEM_LOAN_POLICY,
			FIELD_NAME,
			[ HOST_LMS_ITEM_LOAN_POLICY_NAME, HOST_LMS_ITEM_LOAN_POLICY_NAME_CHANGED ]
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

    void "Create a new HostLMSItemLoanPolicy"(String tenantId, String code, String name, boolean lendable, boolean hidden) {
        when:"Create a new HostLMSItemLoanPolicy"

            // Create the HostLMSItemLoanPolicy
            Map hostLMSItemLoanPolicy = [
                code : code,
                name : name,
                lendable: lendable,
                hidden: hidden
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_HOST_LMS_ITEM_LOAN_POLICY,
				hostLMSItemLoanPolicy,
				CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_ID,
				FIELD_ID,
				CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_NAME,
				FIELD_NAME
			);

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | code   | name                           | lendable | hidden
            TENANT_ONE | 'test' | HOST_LMS_ITEM_LOAN_POLICY_NAME | true     | false
    }

    void "Fetch a specific HostLMSItemLoanPolicy"(String tenantId, String ignore) {
        when:"Fetch the HostLMSItemLoanPolicy"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_HOST_LMS_ITEM_LOAN_POLICY,
				testctx[CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_ID]);
            assert(restResult.responseBody[FIELD_NAME] == testctx[CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for hostLMSItemLoanPolicies"(String tenantId, String ignore) {
        when:"Search for hostLMSItemLoanPolicies"

            // Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_HOST_LMS_ITEM_LOAN_POLICY,
				FIELD_NAME,
				testctx[CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_NAME]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_ID]);
			assert(restResult.responseBody[0][FIELD_NAME] == testctx[CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update HostLMSItemLoanPolicy name"(String tenantId, String name) {
        when:"Update name for HostLMSItemLoanPolicy"

            Map hostLMSItemLoanPolicy = [
                name : name
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_HOST_LMS_ITEM_LOAN_POLICY,
				testctx[CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_ID].toString(),
				hostLMSItemLoanPolicy
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | HOST_LMS_ITEM_LOAN_POLICY_NAME_CHANGED
    }

    void "Delete a HostLMSItemLoanPolicy"(String tenantId, String ignore) {
        when:"Delete a HostLMSItemLoanPolicy"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_HOST_LMS_ITEM_LOAN_POLICY,
				testctx[CONTEXT_HOST_LMS_ITEM_LOAN_POLICY_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

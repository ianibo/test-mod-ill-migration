package com.k_int.directory;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class NamingAuthoritySpec extends TestBase {

	private static String FIELD_SYMBOL = "symbol";

	private static String CONTEXT_NAMING_AUTHORITY_ID = "namingAuthorityId";
	private static String CONTEXT_NAMING_AUTHORITY_SYMBOL = "namingAuthoritySymbol";

	private static String NAMING_AUTHORITY_SYMBOL = "test99";
	private static String NAMING_AUTHORITY_CHANGED_SYMBOL = "changed";

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
			PATH_NAMING_AUTHORITY,
			FIELD_SYMBOL,
			[ NAMING_AUTHORITY_SYMBOL, NAMING_AUTHORITY_CHANGED_SYMBOL ]
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

    void "Create a new naming authority"(String tenantId, String symbol) {
        when:"Create a new naming authority"
            // Create a map that represents a naming authority that is to be created
            Map namingAuthority = [
                symbol : symbol
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_NAMING_AUTHORITY,
				namingAuthority,
				CONTEXT_NAMING_AUTHORITY_ID,
				FIELD_ID,
				CONTEXT_NAMING_AUTHORITY_SYMBOL,
				FIELD_SYMBOL);

        then:"Check we have a valid response"
			assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_SYMBOL] == symbol.toUpperCase());

        where:
            tenantId   | symbol
            TENANT_ONE | NAMING_AUTHORITY_SYMBOL
    }

    void "Fetch a specific naming authority"(String tenantId, String ignore) {
        when:"Fetch the naming authority"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(tenantId, PATH_NAMING_AUTHORITY, testctx[CONTEXT_NAMING_AUTHORITY_ID].toString());

        then:"Check we have a valid response"
            // Check the id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_NAMING_AUTHORITY_ID]);
			assert(restResult.responseBody[FIELD_SYMBOL] == testctx[CONTEXT_NAMING_AUTHORITY_SYMBOL]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for naming authorities"(String tenantId, String ignore) {

        when:"Search for naming authorities"
			RestResult restResult = searchForObjects(tenantId, PATH_NAMING_AUTHORITY, FIELD_SYMBOL, testctx[CONTEXT_NAMING_AUTHORITY_SYMBOL]);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_SYMBOL] == testctx[CONTEXT_NAMING_AUTHORITY_SYMBOL]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update naming authority symbol"(String tenantId, String symbol) {
        when:"Update context for naming authority"
            Map namingAuthority = [
                symbol : symbol
            ];

			RestResult restResult = updateObject(tenantId, PATH_NAMING_AUTHORITY, testctx[CONTEXT_NAMING_AUTHORITY_ID].toString(), namingAuthority);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_SYMBOL] == symbol.toUpperCase());

        where:
            tenantId   | symbol
            TENANT_ONE | NAMING_AUTHORITY_CHANGED_SYMBOL
    }

    void "Delete a naming authority"(String tenantId, String ignore) {
        when:"Delete a naming authority"
			RestResult restResult = deleteObject(tenantId, PATH_NAMING_AUTHORITY, testctx[CONTEXT_NAMING_AUTHORITY_ID].toString());

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

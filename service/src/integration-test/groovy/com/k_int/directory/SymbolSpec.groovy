package com.k_int.directory;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class SymbolSpec extends TestBase {

	private static String FIELD_SYMBOL = "symbol";

	private static String CONTEXT_SYMBOL_ID = "symbolId";
	private static String CONTEXT_SYMBOL_SYMBOL = "symbol";

	private static final String SYMBOL_SYMBOL = "test99";
	private static final String SYMBOL_SYMBOL_CHANGED = "changed";

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
			PATH_SYMBOL,
			FIELD_SYMBOL,
			[ SYMBOL_SYMBOL, SYMBOL_SYMBOL_CHANGED ]
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

    void "Create a new symbol"(String tenantId, String symbol) {
        when:"Create a new symbol"
		RestResult restResult = null;

		    // Create a new naming authority first
			RestResult restResultNamingAuthority = createNewObjectIfNotExists(
				tenantId,
				PATH_NAMING_AUTHORITY,
				[ symbol : "TestSymbol" ],
				"symbol"
			);
			if (restResultNamingAuthority.success) {
				// Now create a directory entry
				RestResult restResultDirectoryEntry = createNewObjectIfNotExists(
					tenantId,
					PATH_DIRECTORY_ENTRY,
					[ id : "testingSymbols",  name : "Testing symbols", slug : "symbolDE" ],
					"name"
				);
				if (restResultDirectoryEntry.success) {
					// Create a map that represents a symbol that is to be created
					Map symbolMap = [
						authority : [ id : restResultNamingAuthority.responseBody[FIELD_ID] ],
						owner : [ id : restResultDirectoryEntry.responseBody[FIELD_ID] ],
						symbol : symbol
					];

					// Lets us call the base class to post it
					restResult = createNewObject(
						tenantId,
						PATH_SYMBOL,
						symbolMap,
						CONTEXT_SYMBOL_ID,
						FIELD_ID,
						CONTEXT_SYMBOL_SYMBOL,
						FIELD_SYMBOL
					);
				}
			}

        then:"Check we have a valid response"
			assert(restResult != null);
			assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_SYMBOL] == symbol.toUpperCase());

        where:
            tenantId   | symbol
            TENANT_ONE | SYMBOL_SYMBOL
    }

    void "Fetch a specific symbol"(String tenantId, String ignore) {
        when:"Fetch the symbol"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(tenantId, PATH_SYMBOL, testctx[CONTEXT_SYMBOL_ID].toString());

        then:"Check we have a valid response"
            // Check the id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_SYMBOL_ID]);
			assert(restResult.responseBody[FIELD_SYMBOL] == testctx[CONTEXT_SYMBOL_SYMBOL]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for symbols"(String tenantId, String ignore) {

        when:"Search for symbols"
			RestResult restResult = searchForObjects(tenantId, PATH_SYMBOL, FIELD_SYMBOL, testctx[CONTEXT_SYMBOL_SYMBOL]);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_SYMBOL] == testctx[CONTEXT_SYMBOL_SYMBOL]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update symbol symbol"(String tenantId, String symbol) {
        when:"Update context for symbol"
            Map symbolMap = [
                symbol : symbol
            ];

			RestResult restResult = updateObject(tenantId, PATH_SYMBOL, testctx[CONTEXT_SYMBOL_ID].toString(), symbolMap);

        then:"Check we have a valid response"
            // Check the context and id
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_SYMBOL] == symbol.toUpperCase());

        where:
            tenantId   | symbol
            TENANT_ONE | SYMBOL_SYMBOL_CHANGED
    }

    void "Delete a symbol"(String tenantId, String ignore) {
        when:"Delete a symbol"
			RestResult restResult = deleteObject(tenantId, PATH_SYMBOL, testctx[CONTEXT_SYMBOL_ID].toString());

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

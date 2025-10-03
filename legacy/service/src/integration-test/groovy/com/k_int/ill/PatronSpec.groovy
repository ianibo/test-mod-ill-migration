package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class PatronSpec extends TestBase {

	private static final String FIELD_SURNAME = "surname";

	private static final String CONTEXT_PATRON_ID = "patronId";
	private static final String CONTEXT_PATRON_SURNAME = "patronSurname";

	private static final String PATRON_SURNAME = "Surname";
	private static final String PATRON_SURNAME_CHANGED = "Smith";

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
			PATH_PATRON,
			FIELD_SURNAME,
			[ PATRON_SURNAME, PATRON_SURNAME_CHANGED ]
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

    void "Create a new Patron"(
        String tenantId,
        String givenName,
        String surname,
        String hostSystemIdentifier,
        String userProfile
    ) {
        when:"Create a new Patron"

            // Create the Patron
            Map patron = [
                givenname : givenName,
                surname : surname,
                hostSystemIdentifier: hostSystemIdentifier,
                userProfile: userProfile
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_PATRON,
				patron,
				CONTEXT_PATRON_ID,
				FIELD_ID,
				CONTEXT_PATRON_SURNAME,
				FIELD_SURNAME
			);

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_SURNAME] == surname);

        where:
            tenantId   | code   | givenName | surname        | hostSystemIdentifier | userProfile
            TENANT_ONE | 'test' | "bill"    | PATRON_SURNAME | "User0001"           | "profile for user"
    }

    void "Fetch a specific Patron"(String tenantId, String ignore) {
        when:"Fetch the Patron"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_PATRON,
				testctx[CONTEXT_PATRON_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_PATRON_ID]);
            assert(restResult.responseBody[FIELD_SURNAME] == testctx[CONTEXT_PATRON_SURNAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for Patrons"(String tenantId, String ignore) {
        when:"Search for Patrons"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_PATRON,
				FIELD_SURNAME,
				testctx[CONTEXT_PATRON_SURNAME]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_PATRON_ID]);
			assert(restResult.responseBody[0][FIELD_SURNAME] == testctx[CONTEXT_PATRON_SURNAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update Patron surname"(String tenantId, String surname) {
        when:"Update surname for Patron"

            Map patron = [
                surname : surname
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_PATRON,
				testctx[CONTEXT_PATRON_ID].toString(),
				patron
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_SURNAME] == surname);

        where:
            tenantId   | surname
            TENANT_ONE | PATRON_SURNAME_CHANGED
    }

    void "Delete a Patron"(String tenantId, String ignore) {
        when:"Delete a Patron"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_PATRON,
				testctx[CONTEXT_PATRON_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Can patron create request"(String tenantId, String ignore) {
        when:"Search for Patrons"
            // Perform a search
            RestResult restResult = fetchObject(
                tenantId,
                PATH_PATRON + "/1234/canCreateRequest",
                null
            );

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody.patronValid);
            assert(restResult.responseBody.status == "OK");

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

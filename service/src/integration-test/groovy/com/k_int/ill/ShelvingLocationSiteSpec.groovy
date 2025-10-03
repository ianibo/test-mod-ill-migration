package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class ShelvingLocationSiteSpec extends TestBase {

	private static final String FIELD_SUPPLY_PREFERENCE = "supplyPreference";

	private static final String CONTEXT_SHELVING_LOCATION_SITE_ID = "shelvingLocationSiteId";
	private static final String CONTEXT_SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE = "shelvingLocationSiteSupplyPreference";

	private static final long SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE = 102;
	private static final long SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE_CHANGED = 69;

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
			PATH_SHELVING_LOCATION_SITES,
			FIELD_SUPPLY_PREFERENCE,
			[ SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE, SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE_CHANGED ]
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

    void "Create a new ShelvingLocationSite"(String tenantId, long supplyPreference) {
        when:"Create a new ShelvingLocationSite"

			RestResult restResult = null;

			// First of all we need to create a new host lms shelving location
			RestResult restResultHostLMSShelvingLocation = createNewObjectIfNotExists(
				tenantId,
				PATH_HOST_LMS_SHELVING_LOCATIONS,
				[ code : "ShelvingLocation1", name : "Shelving Location 1", supplyPreference: 1, hidden: false ],
				"name"
			);
			if (restResultHostLMSShelvingLocation.success) {
				// Now lets us create a host lms location
				RestResult restResultHostLMSLocation = createNewObjectIfNotExists(
					tenantId,
					PATH_HOST_LMS_LOCATIONS,
					[ code : "Location 1", name : "Location name 1", icalRule: "ical rule", supplyPreference: 2,hidden: false ],
					"name"
				);
				if (restResultHostLMSLocation.success) {
					Map shelvingLocationSite = [
						shelvingLocation : [ id: restResultHostLMSShelvingLocation.responseBody[FIELD_ID] ],
						location : [ id: restResultHostLMSLocation.responseBody[FIELD_ID] ],
						supplyPreference : supplyPreference
					];

					// Lets us call the base class to post it
					restResult = createNewObject(
						tenantId,
						PATH_SHELVING_LOCATION_SITES,
						shelvingLocationSite,
						CONTEXT_SHELVING_LOCATION_SITE_ID,
						FIELD_ID,
						CONTEXT_SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE,
						FIELD_SUPPLY_PREFERENCE
					);
				}
			}

        then:"Check we have a valid response"
			assert(restResult != null)
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_SUPPLY_PREFERENCE] == supplyPreference);

        where:
            tenantId   | supplyPreference
            TENANT_ONE | SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE
    }

    void "Fetch a specific ShelvingLocationSite"(String tenantId, String ignore) {
        when:"Fetch the ShelvingLocationSite"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_SHELVING_LOCATION_SITES,
				testctx[CONTEXT_SHELVING_LOCATION_SITE_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_SHELVING_LOCATION_SITE_ID]);
            assert(restResult.responseBody[FIELD_SUPPLY_PREFERENCE] == testctx[CONTEXT_SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for ShelvingLocationSites"(String tenantId, String ignore) {
        when:"Search for ShelvingLocationSites"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_SHELVING_LOCATION_SITES,
				FIELD_SUPPLY_PREFERENCE,
				testctx[CONTEXT_SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_SHELVING_LOCATION_SITE_ID]);
			assert(restResult.responseBody[0][FIELD_SUPPLY_PREFERENCE] == testctx[CONTEXT_SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update ShelvingLocationSite supply preference"(String tenantId, long supplyPreference) {
        when:"Update supply preference for ShelvingLocationSite"

            Map shelvingLocationSite = [
                supplyPreference : supplyPreference
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_SHELVING_LOCATION_SITES,
				testctx[CONTEXT_SHELVING_LOCATION_SITE_ID].toString(),
				shelvingLocationSite
			);

        then:"Check we have a valid response"
            // Check the name
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_SUPPLY_PREFERENCE] == supplyPreference);

        where:
            tenantId   | supplyPreference
            TENANT_ONE | SHELVING_LOCATION_SITE_SUPPLY_PREFERENCE_CHANGED
    }

    void "Delete a ShelvingLocationSite"(String tenantId, String ignore) {
        when:"Delete a ShelvingLocationSite"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_SHELVING_LOCATION_SITES,
				testctx[CONTEXT_SHELVING_LOCATION_SITE_ID].toString()
			);


        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

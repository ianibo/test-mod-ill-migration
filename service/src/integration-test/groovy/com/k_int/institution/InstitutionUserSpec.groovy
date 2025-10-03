package com.k_int.institution;

import com.k_int.ill.constants.Institution;
import com.k_int.RestResult;
import com.k_int.TestBase;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.gorm.multitenancy.Tenants;
import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Shared
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class InstitutionUserSpec extends TestBase {

	private static final String FIELD_NAME = "name";

	private static final String CONTEXT_INSTITUTION_USER_ID = "institutionUserId";
	private static final String CONTEXT_INSTITUTION_USER_NAME = "institutionUserName";

    @Shared
	private InstitutionData institutionData = null;
	
	InstitutionService institutionService;
	
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
            boolean response = setupTenant(tenantId, name, false);
			boolean enabledMultipleInstitutions = false;

			if (response) {
				// Enable multiple institutions
				enabledMultipleInstitutions = enableMultipleInstitutions(tenantId);
			}

        then:"The response is correct"
            assert(response);
			assert(enabledMultipleInstitutions);

        where:
            tenantId   | name
            TENANT_ONE | TENANT_ONE
    }

    void "Create a new Institution User"(
        String tenantId,
        String name,
		String folioUserId
    ) {
        when:"Create a new Institution User"

            // Create the Institution User
            Map institutionUser = [
                name : name,
                folioUserId : folioUserId,
				institutionManaging : Institution.DEFAULT_INSTITUTION
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_INSTITUTION_USER,
				institutionUser,
				CONTEXT_INSTITUTION_USER_ID,
				FIELD_ID,
				CONTEXT_INSTITUTION_USER_NAME,
				FIELD_NAME
			);
			
        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name                | folioUserId
            TENANT_ONE | "InstitutionUser1" | "00000000-0000-0000-0000-000000000011"
    }

    void "Fetch a specific Institution User"(String tenantId, String ignore) {
        when:"Fetch the Institution User"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_INSTITUTION_USER,
				testctx[CONTEXT_INSTITUTION_USER_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_INSTITUTION_USER_ID]);
            assert(restResult.responseBody[FIELD_NAME] == testctx[CONTEXT_INSTITUTION_USER_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for Institution Users"(String tenantId, String ignore) {
        when:"Search for Institution Users"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_INSTITUTION_USER,
				FIELD_NAME,
				testctx[CONTEXT_INSTITUTION_USER_NAME]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_INSTITUTION_USER_ID]);
			assert(restResult.responseBody[0][FIELD_NAME] == testctx[CONTEXT_INSTITUTION_USER_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update Institution User name"(String tenantId, String name) {
        when:"Update institution User name"

            Map institutionUser = [
                name : name
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_INSTITUTION_USER,
				testctx[CONTEXT_INSTITUTION_USER_ID].toString(),
				institutionUser
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | "Updated User name"
    }

    void "Delete an Institution User"(String tenantId, String ignore) {
        when:"Delete an Institution User"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_INSTITUTION_USER,
				testctx[CONTEXT_INSTITUTION_USER_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
	
	void "Verify createEditDetails"(String tenantId, String ignore) {
		when:"Setup the test data"
		
			institutionData = new InstitutionData(tenantId, this);
			assert(institutionData.setupTestData());
			
			// Fetch the details required for creating / editing
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_USER}/createEditDetails", null);

		then:"Check we have a valid response"
			assert(restResult.success);
			
			// We can't delete institutions at the moment so there maybe more than 4
			assert(restResult.responseBody["institutions"].size() > 3);
			assert(restResult.responseBody["groups"].size() == 3);
			
			// We are not running mocking the other modules at the moment, so we will not get back any folio users
			assert(restResult.responseBody["folioUsers"].size() == 0);
			
		where:
			tenantId   | ignore
			TENANT_ONE | null
	}

	void "Check we can add groups to an InstitutionUser"(String tenantId, String ignore) {
		when:"Add groups to a institution User"
		
			// Obtain groups A and B and institution A
			Object institutionUserA = institutionData.getInstitutionUserA();
			Object groupA = institutionData.getInstitutionGroupA();
			Object groupB = institutionData.getInstitutionGroupB();
			
			// Now add these 2 groups to the institution			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_USER}/" + institutionUserA[FIELD_ID] + "/modifyGroups?group=" + groupA[FIELD_ID] + "&group=" + groupB[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_ADD, null);
			
		then:"Were the groups added"
			// Check we have been successful
            assert(restResult.success);
			assert(restResult.responseBody["responseResult"]["successful"] == 2);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
	
	void "Check we can add institutions to a group"(String tenantId, String ignore) {
		when:"Add user to a group"
		
			// Obtain group A and users B and C
			Object groupA = institutionData.getInstitutionGroupA();
			Object institutionB = institutionData.getInstitutionB();
			Object institutionC = institutionData.getInstitutionC();
			
			// Now add the user to the group			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_GROUP}/" + groupA[FIELD_ID] + "/modifyInstitutions?institution=" + institutionB[FIELD_ID] + "&institution=" + institutionC[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_ADD, null);
			
		then:"Was the institution added"
			// Check we have been successful
            assert(restResult.success);
			assert(restResult.responseBody["responseResult"]["successful"] == 2);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
	
	void "Check the user now has access to the institution"(String tenantId, String ignore) {
		when:"Check user has access to institution"
		
			// Obtain institution B and user A
			Object institutionB = institutionData.getInstitutionB();
			Object userA = institutionData.getInstitutionUserA();
			
			// Now add the user to the group			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_USER}/" + userA[FIELD_ID] + "/institutions", null);
			boolean userHasAccessToInstitution = false;
			for (Object institution in restResult.responseBody) {
				if (institution["institution"][FIELD_ID] == institutionB[FIELD_ID]) {
					userHasAccessToInstitution = true;
				}
			}
			
		then:"Was the user added"
			// Check we have been successful
            assert(restResult.success);
			assert(userHasAccessToInstitution);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}

	void "Remove the institutions from the group"(String tenantId, String ignore) {
		when:"Remove institutions from group"
		
			// Obtain group A and users B and C
			Object groupA = institutionData.getInstitutionGroupA();
			Object institutionB = institutionData.getInstitutionB();
			Object institutionC = institutionData.getInstitutionC();
			
			// Now remove the users from the group
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_GROUP}/" + groupA[FIELD_ID] + "/modifyInstitutions?institution=" + institutionB[FIELD_ID] + "&institution=" + institutionC[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_REMOVE, null);
			
		then:"Were the institutions removed"
			// Check we have been successful
            assert(restResult.success);
			assert(restResult.responseBody["responseResult"]["successful"] == 2);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}

	void "Remove groups from an Institution user"(String tenantId, String ignore) {
		when:"Remove groups from institution user"
			
			// Obtain groups A and B and institution A
			Object institutionUserA = institutionData.getInstitutionUserA();
			Object groupA = institutionData.getInstitutionGroupA();
			Object groupB = institutionData.getInstitutionGroupB();
			
			// Now remove these 2 groups from the institution			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_USER}/" + institutionUserA[FIELD_ID] + "/modifyGroups?group=" + groupA[FIELD_ID] + "&group=" + groupB[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_REMOVE, null);
			
		then:"Were the groups removed"
			// Check we have been successful
            assert(restResult.success);
			assert(restResult.responseBody["responseResult"]["successful"] == 2);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
}

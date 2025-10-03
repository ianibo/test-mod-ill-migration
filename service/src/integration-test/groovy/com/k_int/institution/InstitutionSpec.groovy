package com.k_int.institution;

import com.k_int.RestResult;
import com.k_int.TestBase;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Shared;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class InstitutionSpec extends TestBase {

	private static final String FIELD_NAME = "name";

	private static final String CONTEXT_INSTITUTION_ID = "institutionId";
	private static final String CONTEXT_INSTITUTION_NAME = "institutionName";

    @Shared
	private InstitutionData institutionData = null;
	
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

    void "Create a new Institution"(
        String tenantId,
        String name,
		String description
    ) {
        when:"Create a new Institution"

            // Create the Institution
            Map institution = [
                name : name,
                description : description
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_INSTITUTION,
				institution,
				CONTEXT_INSTITUTION_ID,
				FIELD_ID,
				CONTEXT_INSTITUTION_NAME,
				FIELD_NAME
			);

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name           | description
            TENANT_ONE | "Institution1" | "Institution 1 description"
    }

    void "Fetch a specific Institution"(String tenantId, String ignore) {
        when:"Fetch the Institution"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_INSTITUTION,
				testctx[CONTEXT_INSTITUTION_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_INSTITUTION_ID]);
            assert(restResult.responseBody[FIELD_NAME] == testctx[CONTEXT_INSTITUTION_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for Institutions"(String tenantId, String ignore) {
        when:"Search for Institutions"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_INSTITUTION,
				FIELD_NAME,
				testctx[CONTEXT_INSTITUTION_NAME]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_INSTITUTION_ID]);
			assert(restResult.responseBody[0][FIELD_NAME] == testctx[CONTEXT_INSTITUTION_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update Institution name"(String tenantId, String name) {
        when:"Update institution name"

            Map institution = [
                name : name
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_INSTITUTION,
				testctx[CONTEXT_INSTITUTION_ID].toString(),
				institution
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | "Updated name"
    }
	
	void "Verify createEditDetails"(String tenantId, String ignore) {
		when:"Setup the test data"
		
			institutionData = new InstitutionData(tenantId, this);
			assert(institutionData.setupTestData());
			
			// Fetch the details required for creating / editing
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION}/createEditDetails", null);

		then:"Check we have a valid response"
			assert(restResult.success);
			assert(restResult.responseBody["groups"].size() == 3);
			assert(restResult.responseBody["directoryEntries"].size() == 2);
			
		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
	
	void "Check we can add groups to an Institution"(String tenantId, String ignore) {
		when:"Add groups to institution"
		
			// Obtain groups A and B and institution A
			Object institutionA = institutionData.getInstitutionA();
			Object groupA = institutionData.getInstitutionGroupA();
			Object groupB = institutionData.getInstitutionGroupB();
			
			// Now add these 2 groups to the institution			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION}/" + institutionA[FIELD_ID] + "/modifyGroups?group=" + groupA[FIELD_ID] + "&group=" + groupB[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_ADD, null);
			
		then:"Were the groups added"
			// Check we have been successful
			assert(restResult.responseBody["responseResult"]["successful"] == 2);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
	
	void "Check we can add users to a group"(String tenantId, String ignore) {
		when:"Add user to a group"
		
			// Obtain group A and users B and C
			Object groupA = institutionData.getInstitutionGroupA();
			Object userB = institutionData.getInstitutionUserB();
			Object userC = institutionData.getInstitutionUserC();
			
			// Now add the user to the group			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_GROUP}/" + groupA[FIELD_ID] + "/modifyUsers?user=" + userB[FIELD_ID] + "&user=" + userC[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_ADD, null);
			
		then:"Was the user added"
			// Check we have been successful
			assert(restResult.responseBody["responseResult"]["successful"] == 2);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
	
	void "Check the user now has access to the institution"(String tenantId, String ignore) {
		when:"Check user has access to institution"
		
			// Obtain institution A and user C
			Object institutionA = institutionData.getInstitutionA();
			Object userC = institutionData.getInstitutionUserC();
			
			// Now add the user to the group			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION}/" + institutionA[FIELD_ID] + "/users", null);
			boolean userHasAccessToInstitution = false;
			for (Object user in restResult.responseBody) {
				if (user["user"][FIELD_ID] == userC[FIELD_ID]) {
					userHasAccessToInstitution = true;
				}
			}
			
		then:"Was the user added"
			// Check we have been successful
			assert(userHasAccessToInstitution);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
	
	void "Remove the users from the group"(String tenantId, String ignore) {
		when:"Remove users from group"
		
			// Obtain group A and users B and C
			Object groupA = institutionData.getInstitutionGroupA();
			Object userB = institutionData.getInstitutionUserB();
			Object userC = institutionData.getInstitutionUserC();
			
			// Now remove the users from the group
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_GROUP}/" + groupA[FIELD_ID] + "/modifyUsers?user=" + userB[FIELD_ID] + "&user=" + userC[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_REMOVE, null);
			
		then:"Was the users removed"
			// Check we have been successful
			assert(restResult.responseBody["responseResult"]["successful"] == 2);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}

	void "Remove groups from an Institution"(String tenantId, String ignore) {
		when:"Remove groups from institution"
			
			// Obtain groups A and B and institution A
			Object institutionA = institutionData.getInstitutionA();
			Object groupA = institutionData.getInstitutionGroupA();
			Object groupB = institutionData.getInstitutionGroupB();
			
			// Now remove these 2 groups from the institution			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION}/" + institutionA[FIELD_ID] + "/modifyGroups?group=" + groupA[FIELD_ID] + "&group=" + groupB[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_REMOVE, null);
			
		then:"Were the groups removed"
			// Check we have been successful
			assert(restResult.responseBody["responseResult"]["successful"] == 2);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
}

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
class InstitutionGroupSpec extends TestBase {

	private static final String FIELD_NAME = "name";

	private static final String CONTEXT_INSTITUTION_GROUP_ID = "institutionGroupId";
	private static final String CONTEXT_INSTITUTION_GROUP_NAME = "institutionGroupName";

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

    void "Create a new Institution Group"(
        String tenantId,
        String name,
		String description
    ) {
        when:"Create a new Institution Group"

            // Create the Institution Group
            Map institutionGroup = [
                name : name,
                description : description
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_INSTITUTION_GROUP,
				institutionGroup,
				CONTEXT_INSTITUTION_GROUP_ID,
				FIELD_ID,
				CONTEXT_INSTITUTION_GROUP_NAME,
				FIELD_NAME
			);

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name                | description
            TENANT_ONE | "InstitutionGroup1" | "Institution Group 1 description"
    }

    void "Fetch a specific Institution Group"(String tenantId, String ignore) {
        when:"Fetch the Institution Group"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_INSTITUTION_GROUP,
				testctx[CONTEXT_INSTITUTION_GROUP_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_INSTITUTION_GROUP_ID]);
            assert(restResult.responseBody[FIELD_NAME] == testctx[CONTEXT_INSTITUTION_GROUP_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for Institution Groups"(String tenantId, String ignore) {
        when:"Search for Institution Groups"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_INSTITUTION_GROUP,
				FIELD_NAME,
				testctx[CONTEXT_INSTITUTION_GROUP_NAME]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_INSTITUTION_GROUP_ID]);
			assert(restResult.responseBody[0][FIELD_NAME] == testctx[CONTEXT_INSTITUTION_GROUP_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update Institution Group name"(String tenantId, String name) {
        when:"Update institution Group name"

            Map institutionGroup = [
                name : name
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_INSTITUTION_GROUP,
				testctx[CONTEXT_INSTITUTION_GROUP_ID].toString(),
				institutionGroup
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | "Updated group name"
    }

    void "Delete an Institution Group"(String tenantId, String ignore) {
        when:"Delete an Institution Group"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_INSTITUTION_GROUP,
				testctx[CONTEXT_INSTITUTION_GROUP_ID].toString()
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
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_GROUP}/createEditDetails", null);

		then:"Check we have a valid response"
			assert(restResult.success);
			// As we cannot delete institutions, there should be at least 3
			assert(restResult.responseBody["institutions"].size() > 2);
			assert(restResult.responseBody["users"].size() == 3);
			
		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
	
	void "Check we can add institutions to a group"(String tenantId, String ignore) {
		when:"Add institutions to a group"
		
			// Obtain group C and institutions A and B
			Object institutionA = institutionData.getInstitutionA();
			Object institutionB = institutionData.getInstitutionB();
			Object groupC = institutionData.getInstitutionGroupC();
			
			// Now add these 2 institutions to the group			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_GROUP}/" + groupC[FIELD_ID] + "/modifyInstitutions?institution=" + institutionA[FIELD_ID] + "&institution=" + institutionB[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_ADD, null);
			
		then:"Were the institutions added"
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
	
	void "Remove the users from the group"(String tenantId, String ignore) {
		when:"Add user to a group"
		
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

	void "Remove Institutions from a group"(String tenantId, String ignore) {
		when:"Remove groups from institution"
		
			// Obtain group C and institutions A and B
			Object institutionA = institutionData.getInstitutionA();
			Object institutionB = institutionData.getInstitutionB();
			Object groupC = institutionData.getInstitutionGroupC();
			
			// Now remove these 2 institutions from the group			
			RestResult restResult = fetchObject(tenantId, "${PATH_INSTITUTION_GROUP}/" + groupC[FIELD_ID] + "/modifyInstitutions?institution=" + institutionA[FIELD_ID] + "&institution=" + institutionB[FIELD_ID] + "&addRemove=" + com.k_int.ill.constants.Institution.ACTION_REMOVE, null);
			
		then:"Were the groups removed"
			// Check we have been successful
			assert(restResult.responseBody["responseResult"]["successful"] == 2);

		where:
			tenantId   | ignore
			TENANT_ONE | null
	}
}

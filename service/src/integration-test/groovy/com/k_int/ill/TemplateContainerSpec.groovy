package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class TemplateContainerSpec extends TestBase {

	private static final String FIELD_NAME = "name";

	private static final String CONTEXT_TEMPLATE_ID = "templateId";
	private static final String CONTEXT_TEMPLATE_NAME = "templateName";

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

    void "Create a new TemplateContainer"(
        String tenantId,
        String name,
        String templateResolver,
        String description,
        String context
    ) {
        when:"Create a new TemplateContainer"

            // Lookup the reference data value
            RefdataValue templateResolverValue = createRefererenceData(tenantId, "TemplateContainer.TemplateResolver", templateResolver);

            // Create the TemplateContainer
            Map templateContainer = [
                name : name,
                templateResolver : [ id: templateResolverValue.id ],
                description : description,
                context : context
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_TEMPLATE,
				templateContainer,
				CONTEXT_TEMPLATE_ID,
				FIELD_ID,
				CONTEXT_TEMPLATE_NAME,
				FIELD_NAME
			);

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name   | templateResolver | description            | context
            TENANT_ONE | 'test' | "Handlebars"     | "A template container" | "testing"
    }

    void "Fetch a specific TemplateContainer"(String tenantId, String ignore) {
        when:"Fetch the TemplateContainer"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_TEMPLATE,
				testctx[CONTEXT_TEMPLATE_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_TEMPLATE_ID]);
            assert(restResult.responseBody[FIELD_NAME] == testctx[CONTEXT_TEMPLATE_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for TemplateContainers"(String tenantId, String ignore) {
        when:"Search for TemplateContainers"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_TEMPLATE,
				FIELD_NAME,
				testctx[CONTEXT_TEMPLATE_NAME]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_TEMPLATE_ID]);
			assert(restResult.responseBody[0][FIELD_NAME] == testctx[CONTEXT_TEMPLATE_NAME]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update TemplateContainer name"(String tenantId, String name) {
        when:"Update description for TemplateContainer"

            Map templateContainer = [
                name : name
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_TEMPLATE,
				testctx[CONTEXT_TEMPLATE_ID].toString(),
				templateContainer
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_NAME] == name);

        where:
            tenantId   | name
            TENANT_ONE | "name has been changed"
    }

    void "Delete a TemplateContainer"(String tenantId, String ignore) {
        when:"Delete a TemplateContainer"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_TEMPLATE,
				testctx[CONTEXT_TEMPLATE_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

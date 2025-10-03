package com.k_int.ill;

import com.k_int.TestBase;
import com.k_int.ill.statemodel.StateModel;
import com.k_int.ill.statemodel.Status;

import grails.testing.mixin.integration.Integration;
import groovy.json.JsonBuilder;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class StateModelSpec extends TestBase {

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

    void "Attempt to delete any old tenants"(tenantid, name) {
        when:"We post a delete request"
            boolean result = deleteTenant(tenantid, name);

        then:"Any old tenant removed"
            assert(result);

        where:
            tenantid     | name
            TENANT_ONE   | TENANT_ONE
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

    void "Get valid actions for state model and state"(String tenantId, String  stateModel, String fromStatus, boolean includeSystemActions) {
        when:"Search for actions"
            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Perform a search
            def response = doGet(
                "${baseUrl}/ill/stateModel/getValidActions",
                [
                    stateModel : stateModel,
                    status : fromStatus,
                    includeSystemActions : includeSystemActions
                ]
            );
            log.debug("Response from searching for valid actions for a state model: " + response.toString());

        then:"Check we have a valid response"
            // Check the various fields
            assert(response != null);
            assert(response.validActions.size() > 0);

        where:
            tenantId   | stateModel                 | fromStatus                    | includeSystemActions
            TENANT_ONE | StateModel.MODEL_REQUESTER | Status.PATRON_REQUEST_PENDING | true
            TENANT_ONE | StateModel.MODEL_RESPONDER | Status.RESPONDER_IDLE         | false
    }

    void "export a state model"(String tenantId, String  stateModel) {
        when:"Export state model"
            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Export
            Map queryParameters = (stateModel == null) ? [ : ] : [ stateModel : stateModel ];
            def response = doGet("${baseUrl}/ill/stateModel/export", queryParameters);
            log.debug("Response from exporting a state model: " + response.toString());

        then:"Check we have a valid response"
            // Check the various fields
            assert(response != null);
            if (stateModel == null) {
                assert(response.stateModels.size() > 1);
            } else {
                assert(response.stateModels.size() == 1);
                assert(response.stateModels[0].code == stateModel);
            }

        where:
            tenantId   | stateModel
            TENANT_ONE | StateModel.MODEL_REQUESTER
            TENANT_ONE | StateModel.MODEL_RESPONDER
            TENANT_ONE | null
    }

    void "Import state models"(String tenantId) {
        when:"Import state models"
            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // We first export
            def response = doGet("${baseUrl}/ill/stateModel/export");
            log.debug("Response from exporting the state models: " + response.toString());

            // We now import them
            String json = new JsonBuilder(response).toString();
            log.debug("Posting json: " + json);
            def importResponse = doPost("${baseUrl}/ill/stateModel/import", json);
            log.debug("Response from importing the state models: " + (new JsonBuilder(importResponse).toString()));

        then:"Check we have a valid response"
            // Check the various fields
            assert(importResponse != null);
            assert(importResponse.errors.size() == 0);
            assert(importResponse.stati[0].indexOf("errors: 0") != -1);
            assert(importResponse.actionEventResults[0].indexOf("errors: 0") != -1);
            assert(importResponse.actionEventResultLists[0].indexOf("errors: 0") != -1);
            assert(importResponse.actions[0].indexOf("errors: 0") != -1);
            assert(importResponse.events[0].indexOf("errors: 0") != -1);
            importResponse.stateModels.each{ stateModelActionResult ->
                assert(stateModelActionResult.indexOf("errors: 0") != -1);
            }

        where:
            tenantId   | ignore
            TENANT_ONE | ''
    }
}

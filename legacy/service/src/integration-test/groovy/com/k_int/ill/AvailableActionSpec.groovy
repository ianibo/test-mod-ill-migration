package com.k_int.ill;

import com.k_int.TestBase;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.GraphVizService;
import com.k_int.ill.statemodel.StateModel;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class AvailableActionSpec extends TestBase {

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

    void "Fetch the states we can reach from an action"(String tenantId, String stateModel, String actionEvent) {
        when:"Fetch the sttes we can transition to"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Fetch the states
            def response = doGet("${baseUrl}/ill/availableAction/toStates/" + stateModel + "/" + actionEvent);
            log.debug("Response from Get application: " + response.toString());

        then:"Check we have a valid response"
            // Check we have received the to states
            assert(response != null);
            assert(response.toStates != null);

        where:
            tenantId   | stateModel                 | actionEvent
            TENANT_ONE | StateModel.MODEL_REQUESTER | Actions.ACTION_REQUESTER_REQUESTER_CANCEL
            TENANT_ONE | StateModel.MODEL_RESPONDER | Actions.ACTION_RESPONDER_RESPOND_YES
    }

    void "Fetch the states an action can be performed for the action"(String tenantId, String stateModel, String actionEvent) {
        when:"Fetch the states we can transition to"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Fetch the states
            def response = doGet("${baseUrl}/ill/availableAction/fromStates/" + stateModel + "/" + actionEvent);
            log.debug("Response from Get application: " + response.toString());

        then:"Check we have a valid response"
            // Check we have received the from states
            assert(response != null);
            assert(response.fromStates != null);

        where:
            tenantId   | stateModel                 | actionEvent
            TENANT_ONE | StateModel.MODEL_REQUESTER | Actions.ACTION_REQUESTER_REQUESTER_CANCEL
            TENANT_ONE | StateModel.MODEL_RESPONDER | Actions.ACTION_RESPONDER_RESPOND_YES
    }

    void "Generate a graph of the state model"(String tenantId, String stateModel, String format) {
        when:"Fetch the graph for the state model"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Fetch the states
            def response = doGet("${baseUrl}/ill/availableAction/createGraph/" + stateModel, [ outputFormat : format ]);
            log.debug("Response type from createGraph: " + response?.class?.name);

        then:"Check we have a valid response"
            // Check we have received the correct type
            assert(response != null);
            if (format == GraphVizService.FORMAT_DOT) {
                assert(response instanceof String);
            } else {
                assert(response instanceof byte[]);
            }

        where:
            tenantId   | stateModel                 | format
            TENANT_ONE | StateModel.MODEL_REQUESTER | GraphVizService.FORMAT_DOT
            // PNG and SVG generation seems to cause excessive CPU usage - fine on phat laptops with graphics cards
	    // but breaks server builds on CI/CD nodes
            // TENANT_ONE | StateModel.MODEL_REQUESTER | GraphVizService.FORMAT_PNG
            // TENANT_ONE | StateModel.MODEL_RESPONDER | GraphVizService.FORMAT_SVG
    }
}

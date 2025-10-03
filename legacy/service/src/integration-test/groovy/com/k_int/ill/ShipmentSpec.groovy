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
class ShipmentSpec extends TestBase {

	private static final String FIELD_TRACKING_NUMBER = "trackingNumber";

	private static final String CONTEXT_SHIPMENT_ID = "shipmentId";
	private static final String CONTEXT_SHIPMENT_TRACKING_NUMBER = "shipmentTrackingNumber";

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

    void "Create a new Shipment"(
        String tenantId,
        String shipmentMethod,
        String trackingNumber,
        String status,
        String shipDate,
        String receivedDate
    ) {
        when:"Create a new Shipment"

            // Lookup the reference data values
            RefdataValue shipmentMethodValue = createRefererenceData(tenantId, "Shipment.Method", shipmentMethod);
            RefdataValue statusValue = createRefererenceData(tenantId, "Shipment.Status", status);

            // Create the Shipment
            Map shipment = [
                shipmentMethod : [ id: shipmentMethodValue.id ],
                trackingNumber : trackingNumber,
                status : [ id: statusValue.id ],
                shipDate : shipDate,
                receivedDate : receivedDate
            ];
			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_SHIPMENTS,
				shipment,
				CONTEXT_SHIPMENT_ID,
				FIELD_ID,
				CONTEXT_SHIPMENT_TRACKING_NUMBER,
				FIELD_TRACKING_NUMBER
			);

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody[FIELD_ID] != null);
			assert(restResult.responseBody[FIELD_TRACKING_NUMBER] == trackingNumber);

        where:
            tenantId   | shipmentMethod | trackingNumber | status     | shipDate              | receivedDate
            TENANT_ONE | "Courier"      | "A0001"        | "Received" | "2022-06-01T00:00:00" | "2023-01-31T00:00:00"
    }

    void "Fetch a specific Shipment"(String tenantId, String ignore) {
        when:"Fetch the Shipment"
			// Call the base method to fetch it
			RestResult restResult = fetchObject(
				tenantId,
				PATH_SHIPMENTS,
				testctx[CONTEXT_SHIPMENT_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_SHIPMENT_ID]);
            assert(restResult.responseBody[FIELD_TRACKING_NUMBER] == testctx[CONTEXT_SHIPMENT_TRACKING_NUMBER]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for Shipments"(String tenantId, String ignore) {
        when:"Search for Shipments"
			// Perform a search
			RestResult restResult = searchForObjects(
				tenantId,
				PATH_SHIPMENTS,
				FIELD_TRACKING_NUMBER,
				testctx[CONTEXT_SHIPMENT_TRACKING_NUMBER]
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_ID] == testctx[CONTEXT_SHIPMENT_ID]);
			assert(restResult.responseBody[0][FIELD_TRACKING_NUMBER] == testctx[CONTEXT_SHIPMENT_TRACKING_NUMBER]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update Shipment tracking number"(String tenantId, String trackingNumber) {
        when:"Update tracking number for Shipment"

            Map shipment = [
                trackingNumber : trackingNumber
            ];
			RestResult restResult = updateObject(
				tenantId,
				PATH_SHIPMENTS,
				testctx[CONTEXT_SHIPMENT_ID].toString(),
				shipment
			);

        then:"Check we have a valid response"
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_TRACKING_NUMBER] == trackingNumber);

        where:
            tenantId   | trackingNumber
            TENANT_ONE | "B1564"
    }

    void "Delete a Shipment"(String tenantId, String ignore) {
        when:"Delete a Shipment"
			// Just call the base class
			RestResult restResult = deleteObject(
				tenantId,
				PATH_SHIPMENTS,
				testctx[CONTEXT_SHIPMENT_ID].toString()
			);

        then:"Check we have a valid response"
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }
}

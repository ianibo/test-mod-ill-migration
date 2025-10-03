package com.k_int.swagger;

import com.k_int.RestResult
import com.k_int.TestBase;

import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class SwaggerSpec extends TestBase {

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

  void "Set up test tenants "(tenantid, name) {
    when:"We post a new tenant request to the OKAPI controller"
      boolean response = setupTenant(tenantid, name);

    then:"The response is correct"
      assert(response);

    where:
      tenantid     | name
      TENANT_ONE   | TENANT_ONE
  }

  void "Fetch the swagger doc"() {
    when:"Fetch the document"
      // Call the base method to fetch it
      RestResult restResult = fetchObject(TENANT_ONE, "swagger/api", null);

    then:"Check we have a valid response"
      // Check the context and id
      assert(restResult.success);
      assert(restResult.responseBody.swagger != null);
      assert(restResult.responseBody.swagger == "2.0");
      assert(restResult.responseBody.paths != null);
  }
}

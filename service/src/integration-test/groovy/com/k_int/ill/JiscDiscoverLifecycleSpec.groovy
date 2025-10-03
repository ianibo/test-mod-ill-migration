package com.k_int.ill;

import org.springframework.beans.factory.annotation.Value;

import com.k_int.TestBase;
import com.k_int.ill.sharedindex.JiscDiscoverSharedIndexService;
import com.k_int.ill.sharedindex.SharedIndexResult;
import com.k_int.institution.InstitutionService;

import grails.gorm.multitenancy.Tenants;
import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.*;

@Slf4j
@Integration
@Stepwise
class JiscDiscoverLifecycleSpec extends TestBase {

  def grailsApplication
  InstitutionService institutionService;
  JiscDiscoverSharedIndexService jiscDiscoverSharedIndexService;

  @Value('${local.server.port}')
  Integer serverPort

  def setupSpec() {
  }

  def setup() {
  }

  void "Attempt to delete any old tenants"(tenantid, name) {
    when:"We post a delete request"
      try {
        setHeaders(['X-Okapi-Tenant': tenantid, 'accept': 'application/json; charset=UTF-8'])
        def resp = doDelete("${baseUrl}_/tenant".toString(),null)
      }
      catch ( Exception e ) {
        // If there is no Tenant we'll get an exception here, it's fine
      }

    then:"Any old tenant removed"
      1==1

    where:
      tenantid    | name
      TENANT_FOUR | TENANT_FOUR
  }

  void "Set up test tenants "(tenantid, name) {
    when:"We post a new tenant request to the OKAPI controller"
	  // Let the base class do the work
      boolean response = setupTenant(tenantid, name);

    then:"The response is correct"
      assert(response);

    where:
      tenantid    | name
      TENANT_FOUR | TENANT_FOUR
  }

  void "Test Library Hub Discover Lookup"() {
    when: "we try to look up an item by id"
      List lookup_result = null;
      Tenants.withId((TENANT_FOUR + '_mod_ill').toLowerCase()) {
        // In a test profile, this will invoke the mock provider and give back static data - check that works first
        lookup_result = jiscDiscoverSharedIndexService.findAppropriateCopies(
            institutionService.getDefaultInstitution(),
            [systemInstanceIdentifier:'2231751908']
        );
      }

    then: "service returns an appropriate record"
      log.debug("Lookup result: ${lookup_result}");
      lookup_result.size() == 11
  }

  void "Test record attachment"() {
    when: "we try to look up an item by id"
      SharedIndexResult lookup_result = null;
      Tenants.withId((TENANT_FOUR + '_mod_ill').toLowerCase()) {
        // In a test profile, this will invoke the mock provider and give back static data - check that works first
        lookup_result = jiscDiscoverSharedIndexService.fetchSharedIndexRecords([systemInstanceIdentifier:'2231751908'])
      }

    then: "service returns an appropriate record"
      log.debug("Lookup result: ${lookup_result}");
      lookup_result.results.size() == 1
  }
}

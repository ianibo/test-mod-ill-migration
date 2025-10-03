package com.k_int.ill;

import java.text.SimpleDateFormat;

import javax.sql.DataSource

import org.grails.orm.hibernate.HibernateDatastore;

import com.k_int.TestBase;
import com.k_int.directory.DirectoryEntry;
import com.k_int.ill.dynamic.DynamicGroovyService;
import com.k_int.ill.lms.HostLMSActions;
import com.k_int.ill.logging.DoNothingHoldingLogDetails;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.ill.routing.RankedSupplier;
import com.k_int.ill.routing.StaticRouterService;
import com.k_int.ill.settings.ISettings;
import com.k_int.ill.statemodel.Status;
import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionService;
import com.k_int.settings.InstitutionSettingsService

import grails.databinding.SimpleMapDataBindingSource;
import grails.gorm.multitenancy.Tenants;
import grails.testing.mixin.integration.Integration;
import grails.web.databinding.GrailsWebDataBinder;
import groovy.util.logging.Slf4j;
import spock.lang.*
import spock.util.concurrent.PollingConditions;

@Slf4j
@Integration
@Stepwise
class RSLifecycleSpec extends TestBase {

    // The scenario details that are maintained between tests
    private static final String SCENARIO_PATRON_REFERENCE = "scenario-patronReference";
    private static final String SCENARIO_REQUESTER_ID = "scenario-requesterId";
    private static final String SCENARIO_RESPONDER_ID = "scenario-responderId";

  private static String LONG_300_CHAR_TITLE = '123456789A123456789B123456789C123456789D123456789E123456789F123456789G123456789H123456789I123456789J123456789k123456789l123456789m123456789n123456789o123456789p123456789q123456789r123456789s123456789t123456789U123456789V123456789W123456789Y123456789Y12345XXXXX'
  private SimpleDateFormat scenarioDateFormatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS");

  // Warning: You will notice that these directory entries carry and additional customProperty: AdditionalHeaders
  // When okapi fronts the /ill/externalApi/iso18626 endpoint it does so through a root path like
  // _/invoke/tenant/TENANT_ID/ill/externalApi/iso18626 - it then calls the relevant path with the TENANT_ID as a header
  // Because we want our tests to run without an OKAPI, we need to supply the tenant-id that OKAPI normally would and that
  // is the function of the AdditionalHeaders custom property here
  @Shared
  private static List<Map> DIRECTORY_INFO = [
    [ id:'RS-T-D-0001', name: TENANT_ONE, slug:'RS_INST_ONE', type:'institution',
      symbols: [[ authority:'ISIL', symbol:'RST1', priority:'a'] ],
      services:[
        [
          slug: TENANT_ONE + '_ISO18626',
          service:[ 'name':'ILL ISO18626 Service', 'address':'${baseUrl}/ill/externalApi/iso18626', 'type':'ISO18626-2017', 'businessFunction':'ILL' ],
          customProperties:[
            'ILLPreferredNamespaces':['ISIL', 'ILL', 'PALCI', 'IDS'],
            'AdditionalHeaders':['X-Okapi-Tenant:' + TENANT_ONE]
          ]
        ],
        [
          slug: TENANT_ONE + '_STATS',
          service:[ 'name':'Stats Service', 'address':'${baseUrl}/ill/externalApi/statistics', 'type':'HTTP', 'businessFunction':'RS_STATS' ],
          customProperties:[
            'AdditionalHeaders':['X-Okapi-Tenant:' + TENANT_ONE]
          ]
        ]
      ]
    ],
    [ id:'RS-T-D-0002', name: TENANT_TWO, slug:'RS_INST_TWO',     symbols: [[ authority:'ISIL', symbol:'RST2', priority:'a'] ],
      services:[
        [
          slug: TENANT_TWO + '_ISO18626',
          service:[ 'name':'ILL ISO18626 Service', 'address':'${baseUrl}/ill/externalApi/iso18626', 'type':'ISO18626-2017', 'businessFunction':'ILL' ],
          customProperties:[
            'ILLPreferredNamespaces':['ISIL', 'ILL', 'PALCI', 'IDS'],
            'AdditionalHeaders':['X-Okapi-Tenant:' + TENANT_TWO]
          ]
        ]
      ]
    ],
    [ id:'RS-T-D-0003', name: TENANT_THREE, slug:'RS_INST_THREE', symbols: [[ authority:'ISIL', symbol:'RST3', priority:'a'] ],
      services:[
        [
          slug: TENANT_THREE + '_ISO18626',
          service:[ 'name':'ILL ISO18626 Service', 'address':'${baseUrl}/ill/externalApi/iso18626', 'type':'ISO18626-2017', 'businessFunction':'ILL' ],
          customProperties:[
            'ILLPreferredNamespaces':['ISIL', 'ILL', 'PALCI', 'IDS'],
            'AdditionalHeaders':['X-Okapi-Tenant:' + TENANT_THREE]
          ]
        ]
      ]
    ],
    [
		id: 'RS-L-D-0005',
		name: 'Local Institution Branch',
		slug:'LocalInstitutionBranch',
		status: 'managed',
		symbols: [
			[ authority:'ISIL', symbol:'LOCALBRANCH', priority:'a']
		],
		services:[
			[
				slug: 'LocalInstitutionBranch_ISO18626',
				service:[
					'name': 'ILL ISO18626 Service',
					'address': '${baseUrl}/ill/externalApi/iso18626',
					'type': 'ISO18626-2017',
					'businessFunction':'ILL'
				],
				customProperties:[
					'ILLPreferredNamespaces': ['ISIL', 'ILL', 'PALCI', 'IDS']
				]
			]
		]
    ]
  ]

  def grailsApplication
  DynamicGroovyService dynamicGroovyService;
  GrailsWebDataBinder grailsWebDataBinder
  HibernateDatastore hibernateDatastore
  DataSource dataSource
  EmailService emailService
  HostLmsService hostLmsService
  HostLmsLocationService hostLmsLocationService
  HostLmsShelvingLocationService hostLmsShelvingLocationService
  InstitutionService institutionService;
  StaticRouterService staticRouterService
  Z3950Service z3950Service
  InstitutionSettingsService institutionSettingsService;

  // This method is declared in the HttpSpec
  def setupSpecWithSpring() {
      super.setupSpecWithSpring();
  }

  def setupSpec() {
  }

  def setup() {
    if ( testctx.initialised == null ) {
      log.debug("Inject actual runtime port number (${serverPort}) into directory entries (${baseUrl}) ");
      for ( Map entry: DIRECTORY_INFO ) {
        if ( entry.services != null ) {
          for ( Map svc: entry.services ) {
            svc.service.address = svc.service.address.replace('''${baseUrl}/''', "${baseUrl}".toString())
            log.debug("${entry.id}/${entry.name}/${svc.slug}/${svc.service.name} - address updated to ${svc.service.address}");
          }
        }
      }
      testctx.initialised = true
    }
  }

  def cleanup() {
  }

  private def searchForRequest(Map requestAttributes) {
	  // We need to include requests that are in the terminal state
	  if (requestAttributes == null) {
		  requestAttributes = [ : ];
	  }
	  requestAttributes.put("includeTerminal", "true");

      def resp = doGet("${baseUrl}ill/patronrequests", requestAttributes);
	  if (resp?.size() == 1) {
		  testctx.foundRequestId = resp[0].id;
		  testctx.foundStateCode = resp[0].state?.code;
	  }
	  return(resp);
  }

  // For the given tenant, block up to timeout ms until the given request is found in the given state
  private String waitForRequestState(String tenant, String patron_reference, String required_state) {

    log.info("waitForRequestState(${tenant},${patron_reference},${required_state}");
    testctx.foundRequestId = null;
	testctx.foundStateCode = null;
	Map requestAttributes = [
      'max': '100',
      'offset': '0',
      'match': 'patronReference',
      'term': patron_reference
    ];
    setHeaders([ 'X-Okapi-Tenant': tenant ]);
    PollingConditions conditions = new PollingConditions(timeout: 300, delay: 2);
	conditions.eventually {
      def resp = searchForRequest(requestAttributes);

	  // Should have 1 hit
      assert(resp?.size() == 1);

	  // Request state must match the required state
      assert(required_state == resp[0].state?.code);
    }

	// This should not happen as an exception should have been thrown when the timeout expires
    if ( required_state != testctx.foundStateCode ) {
      throw new Exception("Expected ${required_state} but timed out waiting, current state is ${request_state}");
    }

    return(testctx.foundRequestId);
  }

  // For the given tenant fetch the specified request
  private Map fetchRequest(String tenant, String requestId) {

    setHeaders([ 'X-Okapi-Tenant': tenant ]);
    // https://east-okapi.folio-dev.indexdata.com/ill/patronrequests/{id}
    def response = doGet("${baseUrl}ill/patronrequests/${requestId}")
    return response;
  }

  void "Attempt to delete any old tenants"(tenantid, name) {
    when:"We post a delete request"
      boolean result = deleteTenant(tenantid, name);

    then:"Any old tenant removed"
      assert(result);

    where:
      tenantid     | name
      TENANT_ONE   | TENANT_ONE
      TENANT_TWO   | TENANT_TWO
      TENANT_THREE | TENANT_THREE
  }

  void "Set up test tenants "(tenantid, name) {
    when:"We post a new tenant request to the OKAPI controller"
      boolean response = setupTenant(tenantid, name);

    then:"The response is correct"
      assert(response);

    where:
      tenantid     | name
      TENANT_ONE   | TENANT_ONE
      TENANT_TWO   | TENANT_TWO
      TENANT_THREE | TENANT_THREE
  }

  void "test presence of HOST LMS adapters"(String name, boolean should_be_found) {

    when: "We try to look up ${name} as a host adapter"
      log.debug("Lookup LMS adapter ${name}");
      HostLMSActions actions = hostLmsService.getHostLMSActionsFor(name.toLowerCase());
      log.debug("result of lookup : ${actions}");

    then: "We expect that the adapter should ${should_be_found ? 'BE' : 'NOT BE'} found. result was ${actions}."
      if ( should_be_found ) {
        assert(actions != null);
      }
      else {
        assert(actions == null);
      }

    where:
      name                                                    | should_be_found
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_ALEPH     | true
	  RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_ALMA      | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_FOLIO     | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_HORIZON   | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_KOHA      | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_MANUAL    | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_MILLENIUM | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_NCSU      | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_SIERRA    | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_SYMPHONY  | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_TLC       | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_VOYAGER   | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_WMS       | true
      RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_WMS2      | true
      'default'                                               | true
      'wibble'                                                | false
  }

  void "Bootstrap directory data for integration tests"(String tenant_id, List<Map> dirents) {
    when:"Load the default directory (test url is ${baseUrl})"
    boolean result = true

    Tenants.withId(tenant_id+'_mod_ill') {
      log.info("Filling out dummy directory entries for tenant ${tenant_id}");

      dirents.each { entry ->

        /*
        entry.symbols.each { sym ->

          String symbol_string = sym.authority instanceof String ? sym.authority : sym.authority.symbol;

          NamingAuthority na = NamingAuthority.findBySymbol(symbol_string)

          if ( na != null ) {
            log.debug("[${tenant_id}] replace symbol string ${symbol_string} with a reference to the object (${na.id},${na.symbol}) to prevent duplicate creation");
            sym.authority = [ id: na.id, symbol: na.symbol ]
          }
          else {
            sym.authority = symbol_string;
          }
        }
        */

        log.debug("Sync directory entry ${entry} - Detected runtime port is ${serverPort}")
        def SimpleMapDataBindingSource source = new SimpleMapDataBindingSource(entry)
        DirectoryEntry de = new DirectoryEntry()
        grailsWebDataBinder.bind(de, source)

        // log.debug("Before save, ${de}, services:${de.services}");
        try {
          de.save(flush:true, failOnError:true)
          log.debug("Result of bind: ${de} ${de.id}");
        }
        catch ( Exception e ) {
          log.error("problem bootstrapping directory data",e);
          result = false;
        }

        if ( de.errors ) {
          de.errors?.allErrors?.each { err ->
            log.error(err?.toString())
          }
        }
      }
    }

    then:"Test directory entries are present"
      assert result == true

    where:
    tenant_id    | dirents
    TENANT_ONE   | DIRECTORY_INFO
    TENANT_TWO   | DIRECTORY_INFO
    TENANT_THREE | DIRECTORY_INFO
  }

  /** Grab the settings for each tenant so we can modify them as needed and send back,
   *  then work through the list posting back any changes needed for that particular tenant in this testing setup
   *  for now, disable all auto responders
   *  N.B. that the test "Send request using static router" below RELIES upon the static routes assigned to TENANT_ONE.
   *  changing this data may well break that test.
   */
  void "Configure Tenants for Mock Lending"(String tenant_id, Map changes_needed) {
    when:"We fetch the existing settings for ${tenant_id}"
        changeSettings(tenant_id, changes_needed);

    then:"Tenant is configured"
      1==1

    where:
      tenant_id     | changes_needed
      TENANT_ONE    | [ 'auto_responder_status':'off', 'auto_responder_cancel': 'off', 'routing_adapter':'static', 'static_routes':'ISIL:RST3,ISIL:RST2' ]
      TENANT_TWO    | [ 'auto_responder_status':'off', 'auto_responder_cancel': 'off', 'routing_adapter':'static', 'static_routes':'ISIL:RST1,ISIL:RST3' ]
      TENANT_THREE  | [ 'auto_responder_status':'off', 'auto_responder_cancel': 'off', 'routing_adapter':'static', 'static_routes':'ISIL:RST1' ]
  }

  void "Validate Static Router"() {

    when:"We call the static router"
      List<RankedSupplier> resolved_rota = null;
      Tenants.withId((TENANT_ONE + '_mod_ill')) {
		PatronRequest patronRequest = new PatronRequest();
		patronRequest.institution = institutionService.getDefaultInstitution();
		patronRequest.title = 'Test';
        resolved_rota = staticRouterService.findMoreSuppliers(
            patronRequest
        );
      }
      log.debug("Static Router resolved to ${resolved_rota}");

    then:"The expected result is returned"
      resolved_rota.size() == 2;
  }


  /**
   * Send a test request from TENANT_ONE(ISIL:RST1) to TENANT_THREE (ISIL:RST3)
   * This test bypasses the request routing component by providing a pre-established rota
   */
  void "Send request with preset rota"(String tenant_id,
                                       String peer_tenant,
                                       String p_title,
                                       String p_author,
                                       String p_systemInstanceIdentifier,
                                       String p_patron_id,
                                       String p_patron_reference,
                                       String requesting_symbol,
                                       String responder_symbol) {
    when:"post new request"
      log.debug("Create a new request ${tenant_id} ${p_title} ${p_patron_id}");

      // Create a request from OCLC:PPPA TO OCLC:AVL
      def req_json_data = [
        requestingInstitutionSymbol:requesting_symbol,
        title: p_title,
        author: p_author,
        systemInstanceIdentifier: p_systemInstanceIdentifier,
        bibliographicRecordId: p_systemInstanceIdentifier,
        patronReference:p_patron_reference,
        patronIdentifier:p_patron_id,
        isRequester:true,
        rota:[
          [directoryId:responder_symbol, rotaPosition:"0", 'instanceIdentifier': '001TagFromMarc', 'copyIdentifier':'COPYBarcode from 9xx']
        ],
        tags: [ 'RS-TESTCASE-1' ]
      ]

      setHeaders([
                   'X-Okapi-Tenant': tenant_id,
                   'X-Okapi-Token': 'dummy',
                   'X-Okapi-User-Id': 'dummy',
                   'X-Okapi-Permissions': '[ "directory.admin", "directory.user", "directory.own.read", "directory.any.read" ]'
                 ])

      log.debug("Post to patronrequests: ${req_json_data}");
      def resp = doPost("${baseUrl}/ill/patronrequests".toString(), req_json_data)

      log.debug("CreateReqTest1 -- Response: RESP:${resp} ID:${resp.id}");

      // Stash the ID
      this.testctx.request_data[p_patron_reference] = resp.id

      String peer_request = waitForRequestState(peer_tenant, p_patron_reference, 'RES_IDLE')
      log.debug("Created new request for with-rota test case 1. REQUESTER ID is : ${this.testctx.request_data[p_patron_reference]}")
      log.debug("                                               RESPONDER ID is : ${peer_request}");


    then:"Check the return value"
      assert this.testctx.request_data[p_patron_reference] != null;
      assert peer_request != null

    where:
      tenant_id  | peer_tenant  | p_title             | p_author         | p_systemInstanceIdentifier | p_patron_id | p_patron_reference        | requesting_symbol | responder_symbol
      TENANT_ONE | TENANT_THREE | 'Brain of the firm' | 'Beer, Stafford' | '1234-5678-9123-4566'      | '1234-5678' | 'RS-LIFECYCLE-TEST-00001' | 'ISIL:RST1'       | 'ISIL:RST3'
  }

  /**
   * Important note for this test case:: peer_tenant is set to TENANT_THREE and this works because TENANT_ONE has a static rota set up
   * so that TENANT_THREE is the first option for sending a request to. Any changes in the test data will likely break this test. Watch out
   */
  void "Send request using static router"(String tenant_id,
                                          String peer_tenant,
                                          String p_title,
                                          String p_author,
                                          String p_systemInstanceIdentifier,
                                          String p_patron_id,
                                          String p_patron_reference,
                                          String requesting_symbol,
                                          String[] tags) {
    when:"post new request"
      log.debug("Create a new request ${tenant_id} ${tags} ${p_title} ${p_patron_id}");

      // Create a request from OCLC:PPPA TO OCLC:AVL
      def req_json_data = [
        requestingInstitutionSymbol:requesting_symbol,
        title: p_title,
        author: p_author,
        systemInstanceIdentifier: p_systemInstanceIdentifier,
        patronReference:p_patron_reference,
        patronIdentifier:p_patron_id,
        isRequester:true,
        tags: tags
      ]

      setHeaders([
                   'X-Okapi-Tenant': tenant_id,
                   'X-Okapi-Token': 'dummy',
                   'X-Okapi-User-Id': 'dummy',
                   'X-Okapi-Permissions': '[ "directory.admin", "directory.user", "directory.own.read", "directory.any.read" ]'
                 ])
      def resp = doPost("${baseUrl}/ill/patronrequests".toString(), req_json_data)

      log.debug("CreateReqTest2 -- Response: RESP:${resp} ID:${resp.id}");

      // Stash the ID
      this.testctx.request_data[p_patron_reference] = resp.id

      String peer_request = waitForRequestState(peer_tenant, p_patron_reference, 'RES_IDLE')
      log.debug("Created new request for with-rota test case 1. REQUESTER ID is : ${this.testctx.request_data[p_patron_reference]}")
      log.debug("                                               RESPONDER ID is : ${peer_request}");


    then:"Check the return value"
      assert this.testctx.request_data[p_patron_reference] != null;
      assert peer_request != null

    where:
      tenant_id  | peer_tenant  | p_title               | p_author         | p_systemInstanceIdentifier | p_patron_id | p_patron_reference        | requesting_symbol | tags
      TENANT_ONE | TENANT_THREE | 'Platform For Change' | 'Beer, Stafford' | '1234-5678-9123-4577'      | '1234-5679' | 'RS-LIFECYCLE-TEST-00002' | 'ISIL:RST1'       | [ 'RS-TESTCASE-2' ]
      TENANT_ONE | TENANT_THREE | LONG_300_CHAR_TITLE   | 'Author, Some'   | '1234-5678-9123-4579'      | '1234-567a' | 'RS-LIFECYCLE-TEST-00003' | 'ISIL:RST1'       | [ 'RS-TESTCASE-3' ]
  }

  // For TENANT_THREE tenant should return the sample data loaded
  void "test API for retrieving shelving locations for #tenant_id"() {

    when:"We post to the shelvingLocations endpoint for tenant"
      setHeaders([
                   'X-Okapi-Tenant': tenant_id
                 ])
      def resp = doGet("${baseUrl}ill/shelvingLocations".toString());

    then:"Got results"
      resp != null;
      log.debug("Got get shelving locations response: ${resp}");

    where:
      tenant_id    | _
      TENANT_THREE | _
  }

  void "test API for creating shelving locations for #tenant_id"() {
    when:"We post to the shelvingLocations endpoint for tenant"
      setHeaders([
                   'X-Okapi-Tenant': tenant_id
                 ])
      def resp = doPost("${baseUrl}ill/shelvingLocations".toString(),
                        [
                          code:'stacks',
                          name:'stacks',
                          supplyPreference:1
                        ])
    then:"Created"
      resp != null;
      log.debug("Got create shelving locations response: ${resp}");
    where:
      tenant_id  | _
      TENANT_ONE | _
  }

  void "test API for creating patron profiles for #tenant_id"() {
    when:"We post to the hostLmsPatronProfiles endpoint for tenant"
      setHeaders([
                   'X-Okapi-Tenant': tenant_id
                 ])
      def resp = doPost("${baseUrl}ill/hostLMSPatronProfiles".toString(),
                        [
                          code:'staff',
                          name:'staff'
                        ])
    then:"Created"
      resp != null;
      log.debug("Got create hostLMSPatronProfiles response: ${resp}");
    where:
      tenant_id  | _
      TENANT_ONE | _
  }

  // For TENANT_THREE tenant should return the sample data loaded
  void "test API for retrieving patron profiles for #tenant_id"() {

    when:"We GET to the hostLMSPatronProfiles endpoint for tenant"
      setHeaders([
                   'X-Okapi-Tenant': tenant_id
                 ])
      def resp = doGet("${baseUrl}ill/hostLMSPatronProfiles".toString());

    then:"Got results"
      resp != null;
      log.debug("Got get hostLMSPatronProfiles response: ${resp}");

    where:
      tenant_id    | _
      TENANT_THREE | _
  }

  void "test determineBestLocation for LMS adapters"(
      String tenant_id,
      String lms,
      String zResponseFile,
      String location,
      String shelvingLocation
  ) {
    when:"We mock z39 and run determineBestLocation"
      z3950Service.metaClass.query = { Institution institution, ISettings settings, String query, int max, String schema, IHoldingLogDetails holdingLogDetails ->
          new XmlSlurper().parseText(new File("src/test/resources/zresponsexml/${zResponseFile}").text)
      };
      def result = [:];
      Tenants.withId(tenant_id.toLowerCase()+'_mod_ill') {
        Institution defaultInstitution = institutionService.getDefaultInstitution();
        // perhaps generalise this to set preferences per test-case, for now we're just using it to see a temporaryLocation respected
        def nonlendingVoyager = hostLmsLocationService.ensureActive(defaultInstitution, 'BASS, Lower Level','');
//        def nonlendingVoyager = hostLmsLocationService.ensureActive(defaultInstitution, 'BASS, Lower Level, 24-Hour Reserve','');
        nonlendingVoyager.setSupplyPreference(-1);

        def nonlendingFolioShelvingLocation = hostLmsShelvingLocationService.ensureExists(defaultInstitution, 'Olin','', -1);
//        def nonlendingFolioShelvingLocation = hostLmsShelvingLocationService.ensureExists(defaultInstitution, 'Olin Reserve','', -1);

        def actions = hostLmsService.getHostLMSActionsFor(lms);
        def pr = new PatronRequest(supplierUniqueRecordId: '123', institution: defaultInstitution);
        result['viaId'] = actions.determineBestLocation(institutionSettingsService, pr, new DoNothingHoldingLogDetails());
        pr = new PatronRequest(isbn: '123', institution: defaultInstitution);
        result['viaPrefix'] = actions.determineBestLocation(institutionSettingsService, pr, new DoNothingHoldingLogDetails());
        result['location'] = HostLMSLocation.findByCodeAndInstitution(location, defaultInstitution);
        result['shelvingLocation'] = HostLMSShelvingLocation.findByCodeAndInstitution(shelvingLocation, defaultInstitution);
      }

    then:"Confirm location and shelving location were created and properly returned"
      result?.viaId?.location == location;
      result?.viaPrefix?.location == location;
      result?.location?.code == location;
      result?.viaId?.shelvingLocation == shelvingLocation;
      result?.viaPrefix?.shelvingLocation == shelvingLocation;
      result?.shelvingLocation?.code == shelvingLocation;

    where:
      tenant_id    | lms        | zResponseFile                 | location            | shelvingLocation            | _
      TENANT_THREE | 'alma'     | 'alma-princeton.xml'          | 'Firestone Library' | 'stacks: Firestone Library' | _
      TENANT_THREE | 'alma'     | 'alma-princeton-notfound.xml' | null                | null                        | _
      TENANT_THREE | 'alma'     | 'alma-dickinson-multiple.xml' | null                | null                        | _
      TENANT_THREE | 'horizon'  | 'horizon-jhu.xml'             | 'Eisenhower'        | null                        | _
      TENANT_THREE | 'symphony' | 'symphony-stanford.xml'       | 'SAL3'              | 'STACKS'                    | _
      TENANT_THREE | 'voyager'  | 'voyager-temp.xml'            | null                | null                        | _
      TENANT_THREE | 'folio'    | 'folio-not-requestable.xml'   | null                | null                        | _
  }

    /**
     * Important note for the scenario test case, as we are relying on the routing and directory entries that have been setup earlier
     * so if the scenario test is moved out we will also need to setup the directories and settings in that spec file as well
     */
    private void createScenarioRequest(String requesterTenantId, int scenarioNo, String patronIdentifier = null, String deliveryMethod = null) {
        // Create the request based on the scenario
        Map request = [
            patronReference: 'Scenario-' + scenarioNo + '-' + scenarioDateFormatter.format(new Date()),
			serviceType: 'Loan',
            title: 'Testing-Scenario-' + scenarioNo,
            author: 'Author-Scenario-' + scenarioNo,
            requestingInstitutionSymbol: 'ISIL:RST1',
            systemInstanceIdentifier: '123-Scenario-' + scenarioNo,
            patronIdentifier: ((patronIdentifier == null) ? '987-Scenario-' + scenarioNo : patronIdentifier),
            deliveryMethod: deliveryMethod,
            isRequester: true
        ];
//        deliveryMethod && (request.deliveryMethod = deliveryMethod);

        log.debug("Create a new request for ${requesterTenantId}, patronReference: ${request.patronReference}, title: ${request.title}");

        setHeaders([ 'X-Okapi-Tenant': requesterTenantId ]);
        def requestResponse = doPost("${baseUrl}/ill/patronrequests".toString(), request);

        log.debug("${request.title} -- Response: Response: ${requestResponse} Id: ${requestResponse.id}");

        // Stash the id and patron reference
        this.testctx.request_data[SCENARIO_REQUESTER_ID] = requestResponse.id;
        this.testctx.request_data[SCENARIO_PATRON_REFERENCE] = requestResponse.patronReference;
    }

    private String performScenarioAction(
        String requesterTenantId,
        String responderTenantId,
        boolean isRequesterAction,
        String actionFile
    ) {
        String actionRequestId = this.testctx.request_data[SCENARIO_REQUESTER_ID]
        String actionTenant = requesterTenantId;
        String peerTenant = responderTenantId;
        if (!isRequesterAction) {
            // It is the responder performing the action
            actionRequestId = this.testctx.request_data[SCENARIO_RESPONDER_ID];
            actionTenant = responderTenantId;
            peerTenant = requesterTenantId;
        }

        String jsonAction = new File("src/integration-test/resources/scenarios/${actionFile}").text;
        log.debug("Action json: ${jsonAction}");
        setHeaders([ 'X-Okapi-Tenant': actionTenant ]);

        // Execute the action
        def actionResponse = doPost("${baseUrl}/ill/patronrequests/${actionRequestId}/performAction".toString(), jsonAction);
        return(actionResponse.toString());
    }

    private String doScenarioAction(
        String requesterTenantId,
        String responderTenantId,
        int scenario,
        boolean isRequesterAction,
        String actionFile,
        String requesterStatus,
        String responderStatus,
        String newResponderTenant,
        String newResponderStatus,
        String patronIdentifier = null,
        String deliveryMethod = null,
		String requestId = null,
		boolean allowHttpException = false
    ) {
        String actionResponse = null;

        try {
            log.debug("Performing action for scenario " + scenario + " using file " + actionFile + ", expected requester status " + requesterStatus + ", expected responder status " + responderStatus);
            // Are we creating a fresh request
            if (responderTenantId == null && requestId == null) {
                // So we need to create  new request
                createScenarioRequest(requesterTenantId, scenario, patronIdentifier, deliveryMethod);
            } else {
                // We need to perform an action
                actionResponse = performScenarioAction(requesterTenantId, responderTenantId, isRequesterAction, actionFile);
            }

            // Wait for this side of the request to move to the appropriate Action
            waitForRequestState(isRequesterAction ? requesterTenantId : responderTenantId, this.testctx.request_data[SCENARIO_PATRON_REFERENCE], isRequesterAction ? requesterStatus : responderStatus);

            // Wait for the other side to change state if it is not a new request
            if (actionFile != null && responderTenantId != null) {
                waitForRequestState(isRequesterAction ? responderTenantId : requesterTenantId, this.testctx.request_data[SCENARIO_PATRON_REFERENCE], isRequesterAction ? responderStatus : requesterStatus);
            }

            // Are we moving onto a new responder
            if (newResponderTenant != null) {
                // Wait for the status and get the responder id
                this.testctx.request_data[SCENARIO_RESPONDER_ID] = waitForRequestState(newResponderTenant, this.testctx.request_data[SCENARIO_PATRON_REFERENCE], newResponderStatus);
            }
        } catch(groovyx.net.http.HttpException e) {
			if (allowHttpException) {
				log.info("HttpException thrown, status code: " + e.getStatusCode() + ", body: " + e.getBody(), e);
				actionResponse = e.getBody();
			} else {
				log.error("HttpExceptione Performing action for scenario " + scenario + " using file " + actionFile + ", expected requester status " + requesterStatus + ", expected responder status " + responderStatus, e);
				throw(e);
			}
        } catch(Exception e) {
            log.error("Exceptione Performing action for scenario " + scenario + " using file " + actionFile + ", expected requester status " + requesterStatus + ", expected responder status " + responderStatus, e);
            throw(e);
        }
        return(actionResponse);
    }

    /**
     * This test case is actually being executed multiple times as it works through the scenarios, so it could actually take a long time to run
     */
    void "test scenarios"(
        String requesterTenantId,
        String responderTenantId,
        int scenario,
        boolean isRequesterAction,
        String actionFile,
        String requesterStatus,
        String responderStatus,
        String newResponderTenant,
        String newResponderStatus,
        String deliveryMethod,
        String expectedActionResponse
    ) {
        when:"Progress the request"

            String actionResponse = doScenarioAction(
                requesterTenantId,
                responderTenantId,
                scenario,
                isRequesterAction,
                actionFile,
                requesterStatus,
                responderStatus,
                newResponderTenant,
                newResponderStatus,
                null,
                deliveryMethod
            );

            log.debug("Scenario: ${scenario}, Responder id: ${this.testctx.request_data[SCENARIO_RESPONDER_ID]}, action file: ${actionFile}");
            log.debug("Expected Action response: ${expectedActionResponse}, action response: ${actionResponse}");

        then:"Check the response value"
            assert this.testctx.request_data[SCENARIO_REQUESTER_ID] != null;
            assert this.testctx.request_data[SCENARIO_RESPONDER_ID] != null;
            assert this.testctx.request_data[SCENARIO_PATRON_REFERENCE] != null;
            if (expectedActionResponse != null) {
                assert expectedActionResponse == actionResponse;
            }

        where:
            requesterTenantId | responderTenantId | scenario | isRequesterAction | actionFile                                        | requesterStatus                                   | responderStatus                             | newResponderTenant | newResponderStatus    | deliveryMethod                      | expectedActionResponse
            TENANT_ONE       | null               | 1        | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
            TENANT_ONE       | TENANT_THREE       | 1        | false             | "supplierConditionalSupply.json"                  | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_PENDING_CONDITIONAL_ANSWER | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | true              | "requesterAgreeConditions.json"                   | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | true              | "requesterCancel.json"                            | Status.PATRON_REQUEST_CANCEL_PENDING              | Status.RESPONDER_CANCEL_REQUEST_RECEIVED    | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | false             | "supplierRespondToCancelNo.json"                  | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | false             | "supplierAddCondition.json"                       | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_PENDING_CONDITIONAL_ANSWER | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | false             | "supplierMarkConditionsAgreed.json"               | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | false             | "supplierPrintPullSlip.json"                      | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_AWAIT_PICKING              | null               | null                  | null                                | "{status=true}"
            TENANT_ONE       | TENANT_THREE       | 1        | false             | "supplierCheckInToIll.json"                       | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_AWAIT_SHIP                 | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | false             | "supplierMarkShipped.json"                        | Status.PATRON_REQUEST_SHIPPED                     | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | false             | "message.json"                                    | Status.PATRON_REQUEST_SHIPPED                     | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | true              | "requesterReceived.json"                          | Status.PATRON_REQUEST_CHECKED_IN                  | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 1        | true              | "messagesAllSeen.json"                            | Status.PATRON_REQUEST_CHECKED_IN                  | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null                                | "{status=true}"
            TENANT_ONE       | TENANT_THREE       | 1        | true              | "patronReturnedItem.json"                         | Status.PATRON_REQUEST_AWAITING_RETURN_SHIPPING    | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null                                | "{status=true}"
            TENANT_ONE       | TENANT_THREE       | 1        | true              | "shippedReturn.json"                              | Status.PATRON_REQUEST_SHIPPED_TO_SUPPLIER         | Status.RESPONDER_ITEM_RETURNED              | null               | null                  | null                                | "{status=true}"
            TENANT_ONE       | TENANT_THREE       | 1        | false             | "supplierCheckOutOfIll.json"                      | Status.PATRON_REQUEST_REQUEST_COMPLETE            | Status.RESPONDER_COMPLETE                   | null               | null                  | null                                | "{status=true}"
            TENANT_ONE       | null               | 2        | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
            TENANT_ONE       | TENANT_THREE       | 2        | false             | "supplierAnswerYes.json"                          | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 2        | false             | "supplierCannotSupply.json"                       | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | Status.RESPONDER_UNFILLED                   | TENANT_TWO         | Status.RESPONDER_IDLE | null                                | "{}"
            TENANT_ONE       | null               | 3        | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
            TENANT_ONE       | TENANT_THREE       | 3        | false             | "supplierConditionalSupply.json"                  | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_PENDING_CONDITIONAL_ANSWER | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 3        | true              | "requesterRejectConditions.json"                  | Status.PATRON_REQUEST_CANCEL_PENDING              | Status.RESPONDER_CANCEL_REQUEST_RECEIVED    | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 3        | false             | "supplierRespondToCancelYes.json"                 | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | Status.RESPONDER_CANCELLED                  | TENANT_TWO         | Status.RESPONDER_IDLE | null                                | "{}"
            TENANT_ONE       | null               | 4        | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
            TENANT_ONE       | TENANT_THREE       | 4        | true              | "requesterCancel.json"                            | Status.PATRON_REQUEST_CANCEL_PENDING              | Status.RESPONDER_CANCEL_REQUEST_RECEIVED    | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 4        | false             | "supplierRespondToCancelYes.json"                 | Status.PATRON_REQUEST_CANCELLED                   | Status.RESPONDER_CANCELLED                  | null               | null                  | null                                | null
            TENANT_ONE       | null               | 5        | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | "URL"                               | null
            TENANT_ONE       | TENANT_THREE       | 5        | false             | "supplierAnswerYes.json"                          | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | "URL"                               | "{}"
            TENANT_ONE       | TENANT_THREE       | 5        | false             | "supplierPrintPullSlip.json"                      | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_AWAIT_PICKING              | null               | null                  | "URL"                               | "{status=true}"
            TENANT_ONE       | TENANT_THREE       | 5        | false             | "supplierCheckInToIll.json"                       | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_SEQUESTERED                | null               | null                  | "URL"                               | "{}"
            TENANT_ONE       | TENANT_THREE       | 5        | false             | "supplierFillDigitalLoan.json"                    | Status.REQUESTER_LOANED_DIGITALLY                 | Status.RESPONDER_LOANED_DIGITALLY           | null               | null                  | "URL"                               | "{}"
			TENANT_ONE       | null               | 6        | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
            TENANT_ONE       | TENANT_THREE       | 6        | false             | "supplierAnswerYes.json"                          | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null                                | "{}"
			TENANT_ONE       | TENANT_THREE       | 6        | false             | "supplierPrintPullSlip.json"                      | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_AWAIT_PICKING              | null               | null                  | null                                | "{status=true}"
			TENANT_ONE       | TENANT_THREE       | 6        | false             | "supplierCheckInToIllAndSupplierMarkShipped.json" | Status.PATRON_REQUEST_SHIPPED                     | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null                                | "{}"
			TENANT_ONE       | TENANT_THREE       | 6        | true              | "requesterReceived.json"                          | Status.PATRON_REQUEST_CHECKED_IN                  | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null                                | "{}"
			TENANT_ONE       | TENANT_THREE       | 6        | true              | "patronReturnedItemAndShippedReturn.json"         | Status.PATRON_REQUEST_SHIPPED_TO_SUPPLIER         | Status.RESPONDER_ITEM_RETURNED              | null               | null                  | null                                | "{status=true}"
			TENANT_ONE       | TENANT_THREE       | 6        | false             | "supplierCheckOutOfIll.json"                      | Status.PATRON_REQUEST_REQUEST_COMPLETE            | Status.RESPONDER_COMPLETE                   | null               | null                  | null                                | "{status=true}"
			TENANT_ONE       | null               | 7        | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
			TENANT_ONE       | TENANT_THREE       | 7        | true              | "manualCloseFilledLocally.json"                   | Status.PATRON_REQUEST_FILLED_LOCALLY              | Status.RESPONDER_IDLE                       | null               | null                  | null                                | "{status=true}"
			TENANT_ONE       | null               | 8        | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
			TENANT_ONE       | TENANT_THREE       | 8        | true              | "manualCloseComplete.json"                        | Status.PATRON_REQUEST_REQUEST_COMPLETE            | Status.RESPONDER_IDLE                       | null               | null                  | null                                | "{status=true}"
			TENANT_ONE       | null               | 9        | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
			TENANT_ONE       | TENANT_THREE       | 9        | true              | "manualCloseEndOfRota.json"                       | Status.PATRON_REQUEST_END_OF_ROTA                 | Status.RESPONDER_IDLE                       | null               | null                  | null                                | "{status=true}"
			TENANT_ONE       | null               | 10       | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
			TENANT_ONE       | TENANT_THREE       | 10       | true              | "manualCloseCancelled.json"                       | Status.PATRON_REQUEST_CANCELLED                   | Status.RESPONDER_IDLE                       | null               | null                  | null                                | "{status=true}"
			TENANT_ONE       | null               | 11       | true              | null                                              | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | null                                | null
            TENANT_ONE       | TENANT_THREE       | 11       | false             | "supplierAnswerYes.json"                          | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null                                | "{}"
			TENANT_ONE       | TENANT_THREE       | 11       | false             | "supplierPrintPullSlip.json"                      | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_AWAIT_PICKING              | null               | null                  | null                                | "{status=true}"
            TENANT_ONE       | TENANT_THREE       | 11       | false             | "supplierCheckInToIll.json"                       | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_AWAIT_SHIP                 | null               | null                  | null                                | "{}"
            TENANT_ONE       | TENANT_THREE       | 11       | false             | "undo.json"                                       | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_AWAIT_PICKING              | null               | null                  | null                                | "{}"
    }

    void "test Dynamic Groovy"() {
        when: "Initialise the groovy source"
            String scriptSource =
'''
    parameter1.equals("Test");
''';
            String scriptAsClassSource =
'''
    arguments.parameter1.equals("Test");
''';
            Map ScriptArguments = ["parameter1": "Test"];
            Object scriptResult = dynamicGroovyService.executeScript(scriptSource, ScriptArguments);
            Object scriptResultAsClass = dynamicGroovyService.executeScript("scriptCacheKey", scriptAsClassSource, ScriptArguments);
            Object scriptResultAsClassCache = dynamicGroovyService.executeScript("scriptCacheKey", scriptAsClassSource, ScriptArguments);

            String classSource = '''
class DosomethingSimple {
    public String perform(Map args) {
        return(args.parameter1 + "-" + args.secondParameter);
    }
    public String toString() {
        return("Goodness gracious me");
    }
}
''';

            Object classResultDefaultMethod = dynamicGroovyService.executeClass("cacheKey", classSource, ["parameter1": "request", "secondParameter": 4]);
            Object classResultCacheMethod = dynamicGroovyService.executeClass("cacheKey", "As long as its null it will be taken from the cache", null, "toString");

        then:"Confirm confirm we get the expected results from executing dynamic groovy"
            assert(scriptResult instanceof Boolean);
            assert (scriptResult == true);
            assert(scriptResultAsClass instanceof Boolean);
            assert (scriptResultAsClass == true);
            assert(scriptResultAsClassCache instanceof Boolean);
            assert (scriptResultAsClassCache == true);
            assert(classResultDefaultMethod instanceof String);
            assert(classResultDefaultMethod == "request-4");
            assert(classResultCacheMethod instanceof String);
            assert(classResultCacheMethod == "Goodness gracious me");
    }

    void "test Import"(String tenantId, String importFile) {
        when: "Perform the import"
            String jsonImportFile = new File("src/integration-test/resources/stateModels/${importFile}").text;
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Perform the import
            def responseJson = doPost("${baseUrl}/ill/stateModel/import".toString(), jsonImportFile);

        then:"Confirm the result json has no errors"
            if (responseJson instanceof Map) {
                responseJson.each { arrayElement ->
                    // The value should be of type ArrayList
                    if (arrayElement.value instanceof ArrayList) {
                        arrayElement.value.each { error ->
                            if (error instanceof String) {
                                // Ignore lines beginning with "No array of "
                                if (!error.startsWith("No array of ")) {
                                    // It should end with ", errors: 0" otherwise we have problems
                                    assert(error.endsWith(", errors: 0"));
                                }
                            } else {
                                log.debug("List element is not of type String, it has type " + error.getClass());
                                assert(false);
                            }
                        }
                    } else {
                        // For some reason we did not get an array list
                        log.debug("Map element with key " + arrayElement.key + " is not an ArrayList");
                        assert(false);
                    }
                }
            } else {
                // We obviously did not get json returned
                log.debug("Json returned by import is not a Map");
                assert(false);
            }

        where:
            tenantId      | importFile
            TENANT_THREE | "testResponder.json"
    }

    void "set_responder_state_model"(String tenantId, String stateModel) {
        when:"Progress the request"
            // Ensure we have the correct model for the responder
            log.debug("Setting responder state model to " + stateModel);
            changeSettings(tenantId, [ state_model_responder : stateModel ], true);

        then:"If no exception assume it has been set"
            assert(true);

        where:
            tenantId      | stateModel
            TENANT_THREE | "testResponder"
    }

    /**
     * This test case is actually being executed multiple times as it works through the scenarios, so it could actually take a long time to run
     */
    void "test inherited_scenario"(
        String requesterTenantId,
        String responderTenantId,
        int scenario,
        boolean isRequesterAction,
        String actionFile,
        String requesterStatus,
        String responderStatus,
        String newResponderTenant,
        String newResponderStatus,
        String patronIdentifier,
        String expectedActionResponse
    ) {
        when:"Progress the request"
            // Default state model for instance 3 should have been set to testResponder

            String actionResponse = doScenarioAction(
                requesterTenantId,
                responderTenantId,
                scenario,
                isRequesterAction,
                actionFile,
                requesterStatus,
                responderStatus,
                newResponderTenant,
                newResponderStatus,
                patronIdentifier
            );

            log.debug("Scenario: ${scenario}, Responder id: ${this.testctx.request_data[SCENARIO_RESPONDER_ID]}, action file: ${actionFile}");
            log.debug("Expected Action response: ${expectedActionResponse}, action response: ${actionResponse}");

        then:"Check the response value"
            assert this.testctx.request_data[SCENARIO_REQUESTER_ID] != null;
            assert this.testctx.request_data[SCENARIO_RESPONDER_ID] != null;
            assert this.testctx.request_data[SCENARIO_PATRON_REFERENCE] != null;
            if (expectedActionResponse != null) {
                assert expectedActionResponse == actionResponse;
            }

        where:
            requesterTenantId | responderTenantId | scenario | isRequesterAction | actionFile                          | requesterStatus                                   | responderStatus                             | newResponderTenant | newResponderStatus    | patronIdentifier | expectedActionResponse
            TENANT_ONE        | null              | 101      | true              | null                                | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE      | Status.RESPONDER_IDLE | "Unknown"        | null
            TENANT_ONE        | TENANT_THREE      | 10101    | false             | "supplierConditionalSupply.json"    | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_PENDING_CONDITIONAL_ANSWER | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10102    | true              | "requesterAgreeConditions.json"     | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10103    | false             | "goSwimming.json"                   | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | "goneSwimming"                              | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10104    | false             | "finishedSwimming.json"             | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10105    | false             | "supplierAddCondition.json"         | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_PENDING_CONDITIONAL_ANSWER | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10106    | false             | "supplierMarkConditionsAgreed.json" | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10107    | false             | "supplierPrintPullSlip.json"        | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_AWAIT_PICKING              | null               | null                  | null             | "{status=true}"
            TENANT_ONE        | TENANT_THREE      | 10108    | false             | "goSwimming.json"                   | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | "goneSwimming"                              | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10109    | false             | "finishedSwimming.json"             | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_AWAIT_PICKING              | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10110    | false             | "supplierCheckInToIll.json"         | Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED | Status.RESPONDER_AWAIT_SHIP                 | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10111    | false             | "supplierMarkShipped.json"          | Status.PATRON_REQUEST_SHIPPED                     | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10112    | false             | "doSudoku.json"                     | Status.PATRON_REQUEST_SHIPPED                     | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10114    | true              | "requesterReceived.json"            | Status.PATRON_REQUEST_CHECKED_IN                  | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10115    | true              | "patronReturnedItem.json"           | Status.PATRON_REQUEST_AWAITING_RETURN_SHIPPING    | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null             | "{status=true}"
            TENANT_ONE        | TENANT_THREE      | 10116    | false             | "doSudoku.json"                     | Status.PATRON_REQUEST_AWAITING_RETURN_SHIPPING    | Status.RESPONDER_ITEM_SHIPPED               | null               | null                  | null             | "{}"
            TENANT_ONE        | TENANT_THREE      | 10118    | true              | "shippedReturn.json"                | Status.PATRON_REQUEST_SHIPPED_TO_SUPPLIER         | Status.RESPONDER_ITEM_RETURNED              | null               | null                  | null             | "{status=true}"
            TENANT_ONE        | TENANT_THREE      | 10119    | false             | "supplierCheckOutOfIll.json"        | Status.PATRON_REQUEST_REQUEST_COMPLETE            | Status.RESPONDER_COMPLETE                   | null               | null                  | null             | "{status=true}"
    }

    void "check_is_available_groovy"(
        String requesterTenantId,
        String responderTenantId
    ) {
        when:"Create the request and get valid actions"
            // For the patron identifier NoSwimming, the action goSwimming should not be available
            String actionResponse = doScenarioAction(
                requesterTenantId,
                null,
                100001,
                true,
                null,
                Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER,
                null,
                responderTenantId,
                Status.RESPONDER_IDLE,
                "NoSwimming"
            );

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': responderTenantId ]);

            String validActionsUrl = "${baseUrl}/ill/patronrequests/${this.testctx.request_data[SCENARIO_RESPONDER_ID]}/validActions";
            log.debug("doGet(" + validActionsUrl + "");
            // Get hold of the valid actions for the responder
            def validActionsResponse = doGet(validActionsUrl);

            log.debug("Valid responder actions response: ${validActionsResponse.actions.toString()}");

        then:"Check that goSwimmin is not a valid action"
            assert(!validActionsResponse.actions.contains("goSwimming"));

        where:
            requesterTenantId | responderTenantId
            TENANT_ONE       | TENANT_THREE
    }

    void "Check_Statistics_returned"(String tenantId, String ignore) {
        when:"We download the statistics"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Request the statistics
            def statisticsResponse = doGet("${baseUrl}ill/statistics");
            log.debug("Response from statistics: " + statisticsResponse.toString());

        then:"Check we have received some statistics"
            // Should have the current statistics
            assert(statisticsResponse?.current != null);

            // We should also have the requests by state
            assert(statisticsResponse.requestsByState != null);

            // We should have the number of requests that are actively borrowing
            assert(statisticsResponse?.current.find { statistic -> statistic.context.equals("/activeBorrowing") } != null);

            // We should also have the number of requests that are currently on loan
            assert(statisticsResponse?.current.find { statistic -> statistic.context.equals("/activeLoans") } != null);

        where:
            tenantId      | ignore
            TENANT_THREE | ''
    }

    void "Check_Statistics_for_Symbol"(String tenantId, String symbol, String reason) {
        when:"We download the statistics"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Request the statistics
            log.debug("Chas: Sending to url: ${baseUrl}ill/statistics/forSymbol?symbol=${symbol}");
            def statisticSymbolResponse = doGet("${baseUrl}ill/statistics/forSymbol", [ symbol : symbol ]);
            log.debug("Response from statistics for symbol " + symbol + ": " + statisticSymbolResponse.toString());

        then:"Check we have received some statistics"
            // Should have the current statistics
            assert(statisticSymbolResponse != null);
            assert(statisticSymbolResponse.reason != null);
            assert(statisticSymbolResponse.reason == reason);

        where:
            tenantId   | symbol          | reason
            TENANT_ONE | "ISIL:RST1"     | "Statistics collected from stats service"
            TENANT_ONE | "ISIL:RST2"     | "No stats service available"
            TENANT_ONE | "ISIL:NOTEXIST" | "Unable to determine statistics"
    }

    void "setup requests awaiting to be printed"(
        String requesterTenantId,
        String responderTenantId,
        int scenario,
        boolean isRequesterAction,
        String actionFile,
        String requesterStatus,
        String responderStatus,
        String newResponderTenant,
        String newResponderStatus,
        String patronIdentifier,
        String expectedActionResponse
    ) {
        when:"Progress the request"
            // Default state model for instance 3 should have been set to testResponder

            String actionResponse = doScenarioAction(
                requesterTenantId,
                responderTenantId,
                scenario,
                isRequesterAction,
                actionFile,
                requesterStatus,
                responderStatus,
                newResponderTenant,
                newResponderStatus,
                patronIdentifier
            );

            log.debug("Scenario: ${scenario}, Responder id: ${this.testctx.request_data[SCENARIO_RESPONDER_ID]}, action file: ${actionFile}");
            log.debug("Expected Action response: ${expectedActionResponse}, action response: ${actionResponse}");

        then:"Check the response value"
            assert this.testctx.request_data[SCENARIO_REQUESTER_ID] != null;
            assert this.testctx.request_data[SCENARIO_RESPONDER_ID] != null;
            assert this.testctx.request_data[SCENARIO_PATRON_REFERENCE] != null;
            if (expectedActionResponse != null) {
                assert expectedActionResponse == actionResponse;
            }

        where:
            requesterTenantId | responderTenantId | scenario | isRequesterAction | actionFile                          | requesterStatus                                   | responderStatus                             | newResponderTenant | newResponderStatus    | patronIdentifier | expectedActionResponse
            TENANT_ONE       | null               | 201      | true              | null                                | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | "Unknown"        | null
            TENANT_ONE       | TENANT_THREE       | 20101    | false             | "supplierAnswerYes.json"            | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null             | "{}"
            TENANT_ONE       | null               | 202      | true              | null                                | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | "Unknown"        | null
            TENANT_ONE       | TENANT_THREE       | 20201    | false             | "supplierAnswerYes.json"            | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null             | "{}"
            TENANT_ONE       | null               | 203      | true              | null                                | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER    | null                                        | TENANT_THREE       | Status.RESPONDER_IDLE | "Unknown"        | null
            TENANT_ONE       | TENANT_THREE       | 20301    | false             | "supplierAnswerYes.json"            | Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY           | Status.RESPONDER_NEW_AWAIT_PULL_SLIP        | null               | null                  | null             | "{}"
    }

    void "Generate a Batch"(String tenantId, String ignore) {
        when:"We generate a batch"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Create a batch
            def generateBatchResponse = doGet("${baseUrl}ill/patronrequests/generatePickListBatch?filters=isRequester==false&filters=state.terminal==false&filters=state.code==RES_NEW_AWAIT_PULL_SLIP");
            log.debug("Response from generate batch: " + generateBatchResponse.toString());

            // set the batch id in the test context so the next text can retrieve it
            testctx.batchId = generateBatchResponse.batchId;

        then:"Check we have a batch id"
            // Should have the a response
            assert(generateBatchResponse != null);

            // Should have a batch id as part of the response
            assert(generateBatchResponse.batchId != null);

        where:
            tenantId      | ignore
            TENANT_THREE | ''
    }

    void "Print pullslip from batch"(String tenantId, String ignore) {
        when:"Request the pull slip from a batch"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Generate the picklist
            def generatePickListResponse = doGet("${baseUrl}/ill/report/generatePicklist?batchId=${testctx.batchId}");
            log.debug("Response from generatePickList (valid): " + generatePickListResponse.toString());

        then:"Check we have file in response"
            // We should have a byte array
            assert(generatePickListResponse instanceof byte[]);

        where:
            tenantId      | ignore
            TENANT_THREE | ''
    }

    void "Print pullslip from batch generate error"(String tenantId, String ignore) {
        when:"Request the pull slip from a batch"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Generate the picklist
            def generatePickListResponse = null;
            int statusCode = 200;
            try {
                generatePickListResponse = doGet("${baseUrl}/ill/report/generatePicklist?batchId=nonExistantBatchId");
            } catch (groovyx.net.http.HttpException e) {
                statusCode = e.getStatusCode();
                generatePickListResponse = e.getBody();
            }
            log.debug("Response from generatePickList (invalid): " + generatePickListResponse.toString());

        then:"Check we have an error response"
            // The error element should exist
            assert(generatePickListResponse?.error != null);
            assert(statusCode == 400);

        where:
            tenantId     | ignore
            TENANT_THREE | ''
    }

    void "Action requsts as printed from batch"(String tenantId, String ignore) {
        when:"Action requests in batch to be marked as printed"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Action the requests in the batch as printed
            def markBatchAsPrintedResponse = doGet("${baseUrl}/ill/patronrequests/markBatchAsPrinted?batchId=${testctx.batchId}");
            log.debug("Response from markBatchAsPrinted: " + markBatchAsPrintedResponse.toString());

        then:"Check we have file in response"
            // The error element should exist
            assert(markBatchAsPrintedResponse.successful.size() == 3);
            assert(markBatchAsPrintedResponse.failed.size() == 0);
            assert(markBatchAsPrintedResponse.notValid.size() == 0);

        where:
            tenantId      | ignore
            TENANT_THREE | ''
    }

    void "Action requests as printed invalid batch generate error"(String tenantId, String ignore) {
        when:"Request the pull slip from a batch"

            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Generate the picklist
            def markBatchAsPrintedResponse = null;
            int statusCode = 200;
            try {
                markBatchAsPrintedResponse = doGet("${baseUrl}/ill/patronrequests/markBatchAsPrinted?batchId=nonExistantBatchId");
            } catch (groovyx.net.http.HttpException e) {
                statusCode = e.getStatusCode();
                markBatchAsPrintedResponse = e.getBody();
            }
            log.debug("Response from markBatchAsPrinted: " + markBatchAsPrintedResponse.toString());

        then:"Check we have a valid error response"
            // The error element should exist
            assert(markBatchAsPrintedResponse?.error != null);
            assert(statusCode == 400);

        where:
            tenantId     | ignore
            TENANT_THREE | ''
    }

    void "test editing a reuqest"(
        String requesterTenantId,
        String responderTenantId,
        int scenario,
		String author,
		String edition,
		String isbn,
		String issn,
		String neededBy,
		String oclcNumber,
		String patronNote,
		String pickupLocationSlug,
		String placeOfPublication,
		String publicationDate,
		String publisher,
		String systemInstanceIdentifier,
		String title,
		String volume
    ) {
        when:"Progress the request"
			// We will use doScenarioAction to create the request
            String actionResponse = doScenarioAction(
                requesterTenantId,
                null,
                scenario,
                true,
                null,
                Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER,
                null,
                responderTenantId,
                Status.RESPONDER_IDLE,
                null,
                null
            );

			// Now create the message we will put to modify the request
			Map editedRequest = [
				'id': this.testctx.request_data[SCENARIO_REQUESTER_ID],
				'author': author,
				'edition': edition,
				'isbn': isbn,
				'issn': issn,
				'neededBy': neededBy,
				'oclcNumber': oclcNumber,
				'patronNote': patronNote,
				'pickupLocationSlug': pickupLocationSlug,
				'placeOfPublication': placeOfPublication,
				'publicationDate': publicationDate,
				'publisher': publisher,
				'systemInstanceIdentifier': systemInstanceIdentifier,
				'title': title,
				'volume': volume
			];

			setHeaders([ 'X-Okapi-Tenant': requesterTenantId ]);

			// Execute the action
			def editResponse = doPut("${baseUrl}/ill/patronrequests/${this.testctx.request_data[SCENARIO_REQUESTER_ID]}", editedRequest);

			// Fetch the request
			def editedRequestResponse = doGet("${baseUrl}/ill/patronrequests/${this.testctx.request_data[SCENARIO_REQUESTER_ID]}");

        then:"Check the response value"
            assert(this.testctx.request_data[SCENARIO_REQUESTER_ID] != null);
            assert(editResponse != null);
            assert(editResponse.toString() == "{}");
			assert(editedRequestResponse != null);
			// We are only checking the fields that are not being manipulated
			assert(editedRequestResponse.author == author);
			assert(editedRequestResponse.edition == edition);
			assert(editedRequestResponse.isbn == isbn);
			assert(editedRequestResponse.issn == issn);
			assert(editedRequestResponse.oclcNumber == oclcNumber);
			assert(editedRequestResponse.patronNote == patronNote);
			assert(editedRequestResponse.placeOfPublication == placeOfPublication);
			assert(editedRequestResponse.publicationDate == publicationDate);
			assert(editedRequestResponse.publisher == publisher);
			assert(editedRequestResponse.systemInstanceIdentifier == systemInstanceIdentifier);
			assert(editedRequestResponse.title == title);
			assert(editedRequestResponse.volume == volume);

        where:
            requesterTenantId | responderTenantId | scenario | author          | edition          | isbn          | issn          | neededBy     | oclcNumber           | patronNote                         | pickupLocationSlug | placeOfPublication            | publicationDate      | publisher          | systemInstanceIdentifier | title          | volume
            TENANT_ONE        | TENANT_THREE      | 301      | "author edited" | "Edition edited" | "ISBN edited" | "ISSN edited" | "2023-09-28" | "OCLC number edited" | "Patron note this has been edited" | "A-Pickup"         | "Place of publication edited" | "date of pub edited" | "Publisher edited" | "123456"                 | "Title edited" | "Volume 1"
    }

	/* Note: Any tests after the next couple, may need to reset the configuration to "manual" to work correctly */
    void "Configure host lms to be folio"(String tenantId, Map changes_needed) {
		when:"Change host lme for ${tenantId}"
        	changeSettings(tenantId, changes_needed);

		then:"Tenant is configured"
			1==1

		where:
			tenantId    | changes_needed
			TENANT_ONE | [ 'host_lms_integration': 'folio', 'ncip_server_address': 'localhost:12345', 'ncip_from_agency': 'fromAgency', 'ncip_to_agency': 'toAgency', 'ncip_app_profile': 'appProfile', 'borrower_check': 'ncip' ]
  }

    void "scenarios for borrower override"(
		boolean isNewRequest,
        String requesterTenantId,
        String responderTenantId,
        int scenario,
        boolean isRequesterAction,
        String actionFile,
        String requesterStatus,
        String responderStatus,
        String newResponderTenant,
        String newResponderStatus,
		String patronIdentifier,
        String expectedActionResponse
    ) {
        when:"Progress the request"
            // Default state model for instance 3 should have been set to testResponder

            String actionResponse = doScenarioAction(
                requesterTenantId,
                responderTenantId,
                scenario,
                isRequesterAction,
                actionFile,
                requesterStatus,
                responderStatus,
                newResponderTenant,
                newResponderStatus,
                patronIdentifier,
				null,
				isNewRequest ? null : this.testctx.request_data[SCENARIO_REQUESTER_ID],
				true
            );

            log.debug("Scenario: ${scenario}, Responder id: ${this.testctx.request_data[SCENARIO_RESPONDER_ID]}, action file: ${actionFile}");
            log.debug("Expected Action response: ${expectedActionResponse}, action response: ${actionResponse}");

        then:"Check the response value"
            assert this.testctx.request_data[SCENARIO_REQUESTER_ID] != null;
            if (responderTenantId != null || newResponderTenant != null) {
			    assert(this.testctx.request_data[SCENARIO_RESPONDER_ID] != null);
            }
            if (expectedActionResponse != null) {
                assert expectedActionResponse == actionResponse;
            }

        where:
            isNewRequest | requesterTenantId | responderTenantId | scenario | isRequesterAction | actionFile                   | requesterStatus                                | responderStatus | newResponderTenant | newResponderStatus    | patronIdentifier | expectedActionResponse
            true         | TENANT_ONE        | null              | 401      | true              | null                         | Status.PATRON_REQUEST_INVALID_PATRON           | null            | null               | null                  | "unknown"        | null
            false        | TENANT_ONE        | null              | 401      | true              | "borrowerCheck.json"         | Status.PATRON_REQUEST_INVALID_PATRON           | null            | null               | null                  | null             | "{status=false}"
            false        | TENANT_ONE        | null              | 401      | true              | "borrowerCheckOverride.json" | Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER | null            | TENANT_THREE       | Status.RESPONDER_IDLE | null             | "{status=true}"
    }

	/* Note: We are changing the static rota, so that it gets treated as locally held */
	void "Configure for locally held"(String tenantId, Map changes_needed) {
		when:"Change host lme for ${tenantId}"
			changeSettings(tenantId, changes_needed);

		then:"Tenant is configured"
			1==1

		where:
			tenantId    | changes_needed
			TENANT_ONE | [ 'host_lms_integration': 'manual', 'static_routes': 'ISIL:LOCALBRANCH' ]
    }

    void "scenarios for locally held"(
		boolean isNewRequest,
        String requesterTenantId,
        int scenario,
        String actionFile,
        String requesterStatus,
        String expectedActionResponse
    ) {
        when:"Progress the request"
            // Default state model for instance 3 should have been set to testResponder

            String actionResponse = doScenarioAction(
                requesterTenantId,
                null,
                scenario,
                true,
                actionFile,
                requesterStatus,
                null,
                null,
                null,
                null,
				null,
				isNewRequest ? null : this.testctx.request_data[SCENARIO_REQUESTER_ID]
            );

            log.debug("Scenario: ${scenario}, Responder id: ${this.testctx.request_data[SCENARIO_RESPONDER_ID]}, action file: ${actionFile}");
            log.debug("Expected Action response: ${expectedActionResponse}, action response: ${actionResponse}");

        then:"Check the response value"
            assert this.testctx.request_data[SCENARIO_REQUESTER_ID] != null;
            if (expectedActionResponse != null) {
                assert expectedActionResponse == actionResponse;
            }

        where:
            isNewRequest | requesterTenantId | scenario | actionFile                       | requesterStatus                      | expectedActionResponse
            true         | TENANT_ONE        | 501      | null                             | Status.PATRON_REQUEST_LOCAL_REVIEW   | null
            false        | TENANT_ONE        | 501      | "fillLocally.json"               | Status.PATRON_REQUEST_FILLED_LOCALLY | "{status=true}"
            true         | TENANT_ONE        | 502      | null                             | Status.PATRON_REQUEST_LOCAL_REVIEW   | null
            false        | TENANT_ONE        | 502      | "localSupplierCannotSupply.json" | Status.PATRON_REQUEST_END_OF_ROTA    | "{}"
            true         | TENANT_ONE        | 503      | null                             | Status.PATRON_REQUEST_LOCAL_REVIEW   | null
            false        | TENANT_ONE        | 503      | "cancelLocal.json"               | Status.PATRON_REQUEST_CANCELLED      | "{}"
    }

    void "OpenUrl 0.1 tests"(
        String tenantId,
        String artnum,
        String aufirst,
        String auinitl,
        String auinit,
        String auinitm,
        String aulast,
        String bici,
        String coden,
        String genre,
        String issn,
        String eissn,
        String isbn,
        String issue,
        String epage,
        String spage,
        String pages,
        String part,
        String pickupLocation,
        String date,
        String quarter,
        String ssn,
        String sici,
        String title,
        String stitle,
        String atitle,
        String volume

    ) {
        when:"Create the request"
            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Setup the request attributes
            Map requestAttributes = [
                artnum : artnum,
                aufirst : aufirst,
                auinitl : auinitl,
                auinit : auinit,
                auinitm : auinitm,
                aulast : aulast,
                bici : bici,
                coden : coden,
                genre : genre,
                issn : issn,
                eissn : eissn,
                isbn : isbn,
                issue : issue,
                epage : epage,
                spage : spage,
                pages : pages,
                part : part,
                pickupLocation : pickupLocation,
                date : date,
                quarter : quarter,
                ssn : ssn,
                sici : sici,
                title : title,
                stitle : stitle,
                atitle : atitle,
                volume : volume
            ];
            def resp = doGet("${baseUrl}ill/patronrequests/openURL", requestAttributes);

            log.debug("OpenURL response: ${resp}");

            def patronRequest;
            if (resp.id != null) {
                patronRequest = doGet("${baseUrl}/ill/patronrequests/${resp.id}");
                log.debug("Patron request: ${patronRequest}");
            }

        then:"Check successful"
            assert(resp != null);
            assert(resp.result != null);
            assert(resp.result == "SUCCESS");
            assert(resp.id != null);
            assert(patronRequest.artnum == artnum);
            assert(patronRequest.bici == bici);
            assert(patronRequest.issn == issn);
            assert(patronRequest.eissn == eissn);
            assert(patronRequest.coden == coden);
            assert(patronRequest.isbn == isbn);
            assert(patronRequest.issue == issue);
            assert(patronRequest.startPage == spage);
            assert(patronRequest.numberOfPages == pages);
            assert(patronRequest.part == part);
            assert(patronRequest.publicationDate == date);
            assert(patronRequest.quarter == quarter);
            assert(patronRequest.sici == sici);
            assert(patronRequest.title == title);
            assert(patronRequest.titleOfComponent == atitle);
            assert(patronRequest.stitle == stitle);
            assert(patronRequest.volume == volume);
            assert(patronRequest.pickupLocationSlug == pickupLocation);

        where:
            tenantId   | artnum   | aufirst   | auinitl   | auinit   | auinitm   | aulast   | bici   | coden   | genre   | issn   | eissn   | isbn   | issue   | epage   | spage   | pages   | part   | pickupLocation | date   | quarter   | ssn   | sici   | title   | stitle   | atitle   | volume
            TENANT_ONE | "artnum" | "aufirst" | "auinitl" | "auinit" | "auinitm" | "aulast" | "bici" | "coden" | "genre" | "issn" | "eissn" | "isbn" | "issue" | "epage" | "spage" | "pages" | "part" | "RS_INST_ONE"  | "date" | "quarter" | "ssn" | "sici" | "title" | "stitle" | "atitle" | "volume"
    }

    void "OpenUrl 1.0 tests"(
        String tenantId,
        String artnum,
        String aufirst,
        String auinitl,
        String auinit,
        String auinitm,
        String aulast,
        String bici,
        String coden,
        String genre,
        String issn,
        String eissn,
        String isbn,
        String issue,
        String epage,
        String spage,
        String pages,
        String part,
        String pickupLocation,
        String date,
        String quarter,
        String ssn,
        String sici,
        String title,
        String atitle,
        String volume,
        String note,
        String serviceType,
        String emailAddress,
        String patronIdentifier
    ) {
        when:"Create the request"
            // Set the headers
            setHeaders([ 'X-Okapi-Tenant': tenantId ]);

            // Setup the request attributes
            Map requestAttributes = [
                "rft.artnum" : artnum,
                "rft.aufirst" : aufirst,
                "rft.auinitl" : auinitl,
                "rft.auinit" : auinit,
                "rft.auinitm" : auinitm,
                "rft.aulast" : aulast,
                "rft.bici" : bici,
                "rft.coden" : coden,
                "rft.genre" : genre,
                "rft.issn" : issn,
                "rft.eissn" : eissn,
                "rft.isbn" : isbn,
                "rft.issue" : issue,
                "rft.epage" : epage,
                "rft.spage" : spage,
                "rft.pages" : pages,
                "rft.part" : part,
                "rft.date" : date,
                "rft.quarter" : quarter,
                "rft.ssn" : ssn,
                "rft.sici" : sici,
                "rft.title" : title,
                "rft.atitle" : atitle,
                "rft.volume" : volume,
                "svc.pickupLocation" : pickupLocation,
                "svc.note" : note,
                "svc.id" : serviceType,
                "req.emailAddress" : emailAddress,
                "req.id" : patronIdentifier
            ];
            def resp = doGet("${baseUrl}ill/patronrequests/openURL", requestAttributes);

            log.debug("OpenURL response: ${resp}");

            def patronRequest;
            if (resp.id != null) {
                patronRequest = doGet("${baseUrl}/ill/patronrequests/${resp.id}");
                log.debug("Patron request: ${patronRequest}");
            }

        then:"Check successful"
            assert(resp.result == "SUCCESS");
            assert(resp.id != null);
            assert(patronRequest.artnum == artnum);
            assert(patronRequest.bici == bici);
            assert(patronRequest.issn == issn);
            assert(patronRequest.eissn == eissn);
            assert(patronRequest.coden == coden);
            assert(patronRequest.isbn == isbn);
            assert(patronRequest.issue == issue);
            assert(patronRequest.startPage == spage);
            assert(patronRequest.numberOfPages == pages);
            assert(patronRequest.part == part);
            assert(patronRequest.publicationDate == date);
            assert(patronRequest.quarter == quarter);
            assert(patronRequest.sici == sici);
            assert(patronRequest.title == title);
            assert(patronRequest.titleOfComponent == atitle);
            assert(patronRequest.volume == volume);
            assert(patronRequest.pickupLocationSlug == pickupLocation);
            assert(patronRequest.serviceType.label == serviceType);
            assert(patronRequest.patronEmail == emailAddress);
            assert(patronRequest.patronIdentifier == patronIdentifier);
            assert(patronRequest.patronNote == note);

        where:
            tenantId   | artnum   | aufirst   | auinitl   | auinit   | auinitm   | aulast   | bici   | coden   | genre   | issn   | eissn   | isbn   | issue   | epage   | spage   | pages   | part   | pickupLocation | date   | quarter   | ssn   | sici   | title   | atitle   | volume   | note   | serviceType | emailAddress | patronIdentifier
            TENANT_ONE | "artnum" | "aufirst" | "auinitl" | "auinit" | "auinitm" | "aulast" | "bici" | "coden" | "genre" | "issn" | "eissn" | "isbn" | "issue" | "epage" | "spage" | "pages" | "part" | "RS_INST_ONE"  | "date" | "quarter" | "ssn" | "sici" | "title" | "atitle" | "volume" | "Note" | "Loan"      | "a@b"        | "1234"
    }
}

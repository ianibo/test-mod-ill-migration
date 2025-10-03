package com.k_int;

import org.springframework.beans.factory.annotation.Value;

import com.k_int.ill.GrailsEventIdentifier;
import com.k_int.web.toolkit.refdata.RefdataValue;
import com.k_int.web.toolkit.testing.HttpSpec;

import grails.events.bus.EventBus;
import grails.gorm.multitenancy.Tenants;
import groovy.json.JsonBuilder;
import groovy.util.logging.Slf4j;
import spock.lang.Shared;
import spock.util.concurrent.PollingConditions;

@Slf4j
class TestBase extends HttpSpec {

    protected static final String FIELD_CODE        = "code";
    protected static final String FIELD_DESCRIPTION = "description";
	protected static final String FIELD_ID          = "id";

	protected static final String PATH_BATCH                       = "batch";
	protected static final String PATH_DIRECTORY_ENTRY             = "directory/entry";
	protected static final String PATH_DIRECTORY_GROUP             = "directoryGroup";
	protected static final String PATH_DIRECTORY_GROUPS            = "directoryGroups";
	protected static final String PATH_HOST_LMS_ITEM_LOAN_POLICY   = "hostLMSItemLoanPolicy";
	protected static final String PATH_HOST_LMS_LOCATIONS          = "hostLMSLocations";
	protected static final String PATH_HOST_LMS_PATRON_PROFILES    = "hostLMSPatronProfiles";
	protected static final String PATH_HOST_LMS_SHELVING_LOCATIONS = "shelvingLocations";
    protected static final String PATH_INSTITUTION                 = "/institution";
    protected static final String PATH_INSTITUTION_GROUP           = "/institutionGroup";
    protected static final String PATH_INSTITUTION_SETTING         = "settings/institutionSetting";
    protected static final String PATH_INSTITUTION_USER            = "/institutionUser";
	protected static final String PATH_NAMING_AUTHORITY            = "directory/namingAuthority";
	protected static final String PATH_NOTICE_POLICIES             = "noticePolicies";
    protected static final String PATH_PATRON                      = "patron";
	protected static final String PATH_REF_DATA                    = "refdata";
	protected static final String PATH_SEARCH                      = "search";
	protected static final String PATH_SEARCH_ATTRIBUTE            = "searchAttribute";
	protected static final String PATH_SEARCH_GROUP                = "searchGroup";
	protected static final String PATH_SEARCH_TREE                 = "searchTree";
	protected static final String PATH_SERVICE                     = "directory/service";
	protected static final String PATH_SERVICE_ACCOUNT             = "directory/serviceAccount";
    protected static final String PATH_SETTINGS_WORKER             = "settings/worker";
	protected static final String PATH_SHELVING_LOCATION_SITES     = "shelvingLocationSites";
	protected static final String PATH_SHIPMENTS                   = "shipments";
	protected static final String PATH_SYMBOL                      = "directory/symbol";
    protected static final String PATH_SYSTEM_SETTINGS             = "settings/systemSetting";
	protected static final String PATH_TAGS                        = "tags";
	protected static final String PATH_TEMPLATE                    = "template";
	protected static final String PATH_TIMERS                      = "timers";
    protected static final String PATH_TIMERS_EXECUTE              = PATH_TIMERS + "/execute";

    protected static final String TENANT_ONE   = 'ILL_Inst_One'.toLowerCase();
    protected static final String TENANT_TWO   = 'ILL_Inst_Two'.toLowerCase();
    protected static final String TENANT_THREE = 'ILL_Inst_Three'.toLowerCase();
    protected static final String TENANT_FOUR  = 'ILL_Inst_Four'.toLowerCase();

    @Shared
    protected static Map testctx = [
        request_data : [ : ]
    ]

    // Default grails event bus is named targetEvenBus to avoid collision with reactor's event bus.
    @Autowired
    private EventBus targetEventBus

    @Value('${local.server.port}')
    public Integer serverPort;

    /** Contains the tenants that have the ref data loaded */
    static final List<String> refDataLoadedTenants = [];

    // This method is declared in the HttpSpec
    def synchronized setupSpecWithSpring() {

      log.debug("setupSpecWithSpring - starting refDataLoadedTenants=${refDataLoadedTenants}");

      targetEventBus.subscribe(GrailsEventIdentifier.REFERENCE_DATA_LOADED) { final String tenant ->
        log.debug("Ref data loaded for ${tenant}/${refDataLoadedTenants}");
        if ( !refDataLoadedTenants.contains(tenant.replace("_mod_ill", ""))) {
          log.debug("Adding ${tenant.replace("_mod_ill", "")} to refDataLoadedTenants");
          refDataLoadedTenants.add(tenant.replace("_mod_ill", ""));
        }
        else {
          log.warn("Not adding repeated ${tenant}");
        }

        log.debug("RefData loaded tenants: ${refDataLoadedTenants}");
      }
      super.setupSpecWithSpring();
    }

    def setupSpec() {
        httpClientConfig = {
            client.clientCustomizer { HttpURLConnection conn ->
                conn.connectTimeout = 300000 // 5 minutes
                conn.readTimeout = 720000 // 12 minutes
            }
        }
    }

    def cleanup() {
    }

    protected boolean deleteTenant(String tenantId, String name) {

        log.debug("deleteTenant(${tenantId},${name})");

        try {
            setHeaders(['X-Okapi-Tenant': tenantId, 'accept': 'application/json; charset=UTF-8'])
            def resp = doDelete("${baseUrl}_/tenant".toString(),null)
            refDataLoadedTenants.remove(tenantId.toLowerCase());
			log.debug("Ref data marked as removed for tenant ${tenantId}/${refDataLoadedTenants}");
        } catch ( Exception e ) {
            // If there is no Tenant we'll get an exception here, it's fine
        }
        return(true);
    }

    protected boolean setupTenant(String tenantId, String name, boolean deleteTenantFirst = false) {

		// Do we need to delete the tenant first
		if (deleteTenantFirst) {
			deleteTenant(tenantId, name);
		}
		
        log.debug("Post new tenant request for ${tenantId} to ${baseUrl}_/tenant");

        // Lets record how long it took to get the lock
        long startTime = System.currentTimeMillis();

        setHeaders(['X-Okapi-Tenant': tenantId]);
        // post to tenant endpoint
        def resp = doPost("${baseUrl}_/tenant".toString(), ['parameters':[[key:'loadSample', value:'true'],[key:'loadReference',value:'true']]]);

        // Wait for the refdata to be loaded.
        PollingConditions conditions = new PollingConditions(timeout: 240, delay: 1)
        log.debug("Polling for loaded tenant data ${tenantId}/${refDataLoadedTenants}");
        conditions.eventually {
	        // The tenant id sent through the event handler comes through as lowercase
            log.debug("Waiting for ${refDataLoadedTenants} to contain ${tenantId}");
            assert refDataLoadedTenants.contains(tenantId);
        }

        log.info("Ref data loaded for ${tenantId} after " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds, so it should be safe to assume the reference data has been setup");
        log.debug("Got response for new tenant: ${resp}");
        log.debug("refDataLoadedTenants: " + refDataLoadedTenants.toString());

		boolean success = ((resp != null) && refDataLoadedTenants.contains(tenantId));
		if (success) {
			// Sleep for a second, to give a chance for any transactions to complete
			Thread.sleep(1000);
			
			// Give them a chance to tidy up any data that may have been left around from a previous run
			try {
				tidyUpOnTenantSetup(tenantId);
			} catch (Exception e) {
				// We ignore any failures as that probably means there was no data to tidy up
				log.error("Exception thrown in TidyUpOnTenantSetup", e);
			}
		}
        return(success);
    }

	protected void tidyUpOnTenantSetup(String tenantId) {
		// By default we do nothing
	}

	protected boolean enableMultipleInstitutions(String tenantId) {

		// Update the setting that enables multiple institutions on the system, setting the value to yes
		changeSettings(tenantId, [ institution_multiple_enabled : "yes" ]);
		RestResult restResult = searchForObjects(
			tenantId,
			PATH_SYSTEM_SETTINGS,
			"key",
			"institution_multiple_enabled"
		);

		if (restResult.success) {
			// The value we need to set
	        Map setting = [
	            value : "yes"
	        ];
	
			// Lets us call the updateObject to post it
			restResult = updateObject(
				tenantId,
				PATH_SYSTEM_SETTINGS,
				(restResult.responseBody[0][FIELD_ID]).toString(),
				setting
			);
		}
		return(restResult.success);
	}
	
	/**
	 * Searches for a record and if it finds one record, then deletes it
	 * @param tenantId The tenant id we will be looking at
	 * @param path The path to perform the search and delete against
	 * @param searchAttribute The attribute to be searched
	 * @param attributeValues The value we are looking for to delete
	 * @param fieldId The field that holds the id of the record which will be used for the deletion, default: id
	 */
	protected void searchAndDelete(
		String tenantId,
		String path,
		String searchAttribute,
		List attributeValues,
		String fieldId = FIELD_ID
	) {
		// Look to see if the specified record exists
		attributeValues.each { String value ->
		RestResult restResult = searchForObjects(tenantId, path, searchAttribute, value);
			if (restResult.success &&
				(restResult.responseBody != null) &&
				(restResult.responseBody.size() == 1)
				) {
				// We have found a single record so delete it
				deleteObject(tenantId, path, restResult.responseBody[0][fieldId].toString());
			}
		}
	}

    protected List changeSettings(String tenantId, Map changesNeeded, boolean hidden = false) {
        // RequestRouter = Static
        setHeaders(['X-Okapi-Tenant': tenantId]);
        def resp = doGet("${baseUrl}ill/settings/institutionSetting", [ 'max':'100', 'offset':'0', 'filters' : "hidden==${hidden}"]);
        log.debug("Number of settings found: " + resp.size() + ", hidden: " + hidden + ", results: " + resp.toString());
        if ( changesNeeded != null ) {
            resp.each { setting ->
                // log.debug("Considering updating setting ${setting.id}, ${setting.section} ${setting.key} (currently = ${setting.value})");
                if ( changesNeeded.containsKey(setting.key) ) {
                    def new_value = changesNeeded[setting.key];
                    //log.debug("Post update to ${setting} ==> ${new_value}");
                    setting.value = new_value;
                    def update_setting_result = doPut("${baseUrl}ill/settings/institutionSetting/${setting.id}".toString(), setting);
                    log.debug("Result of settings update: ${update_setting_result}");
                }
            }

            // Get hold of the updated settings and return them
            resp = doGet("${baseUrl}ill/settings/institutionSetting", [ 'max':'100', 'offset':'0', 'filters' : "hidden==${hidden}"]);
        }

        // Return the settings
        return(resp);
    }

	/**
	 * Creates a reference value for the given tenant, category and label
	 * @param tenantId The tenant id that the reference value is to be created for
	 * @param CategoryDescription The description of the category
	 * @param label The label associated with the reference data
	 * @return The reference data value object that was created
	 */
	protected RefdataValue createRefererenceData(String tenantId, String CategoryDescription, String label) {
		RefdataValue refDataValue;

		Tenants.withId(tenantId+'_mod_ill') {
			// Create ourselves a new service type
			refDataValue = RefdataValue.lookupOrCreate(CategoryDescription, label);
		}
		return(refDataValue);
	}

	/**
	 * Creates a new object by posting it to the end point
	 * @param tenantId The tenant that the object is to be created for
	 * @param path the path to be used to create the object, only include the part after "/ill/"
	 * @param objectDefinition A map defining the object the object to be created
	 * @param contextIdAttribute The attribute to set in the context object that will represent the id of this object or null if the context object does not need to be updated
	 * @param responseIdAtribute The attribute where we will pull the id from the response, default: "id"
	 * @param contextAttribute An attribute to set in the context object
	 * @param responseAtribute The field where we will extract the value from the response body
	 * @return The result of creating the record
	 */
	protected RestResult createNewObject(
		String tenantId,
		String path,
		Map objectDefinition,
		String contextIdAttribute = null,
		String responseIdAtribute = "id",
		String contextAttribute = null,
		String responseAtribute = null,
		String permissions = null
	)
	{
		// Set the headers
		Map localHeaders = [ 'X-Okapi-Tenant': tenantId ]
		if (permissions != null) {
			localHeaders.put("X-Okapi-Permissions", permissions)
		}
		setHeaders(localHeaders);

		// Turn the map that represents the object into a string
		String json = (new JsonBuilder(objectDefinition)).toString();

		RestResult result = null;
		String url = "${baseUrl}ill/${path}";
		log.debug("Creating new object, posting to ${url} with body\n${json}");
		try {
			def response = doPost(url, null, null, {
				// Note: request is of type groovyx.net.http.HttpConfigs$BasicRequest
				request.setBody(json);
			});

			// Create ourselves a new result object
			result = new RestResult(response);

			// Do we have a response body
			if (result.responseBody != null) {
				// Is there an id value they want to set in the context object
				if ((contextIdAttribute != null) &&
					(responseIdAtribute != null))
				{
					testctx[contextIdAttribute] = result.responseBody[responseIdAtribute];
				}

				// Do we have a secondary attribute we need to set
				if ((contextAttribute != null) &&
				    (responseAtribute != null))
				{
					testctx[contextAttribute] = result.responseBody[responseAtribute];
				}
			}
		} catch (groovyx.net.http.HttpException e) {
			result = new RestResult(e);
		}

		// Output an appropriate log message
		log.debug("Response from post, Status Code: ${result.statusCode}, Body: ${result.responseBody.toString()}");

		// Finally return the result to the caller
		return(result);
	}

	/**
	 * First searches for the object and if it does not exists, attempts to create it
	 * @param tenantId The tenant that the object is to be created for
	 * @param path the path to be used to create the object, only include the part after "/ill/"
	 * @param objectDefinition A map defining the object the object to be created
	 * @param searchAttribute The attribute to perform the search against
	 * @return The object that was found or created
	 */
	protected RestResult createNewObjectIfNotExists(
		String tenantId,
		String path,
		Map objectDefinition,
		String searchAttribute
	)
	{
		RestResult result = null;

		// First of all search for the record
		RestResult resultSearch = searchForObjects(tenantId, path, searchAttribute, objectDefinition[searchAttribute]);
		if (resultSearch.success && (resultSearch.responseBody.size() == 1)) {
			result = new RestResult(resultSearch.responseBody[0]);

		} else {
			// Just call create new object
			result = createNewObject(tenantId, path, objectDefinition);
		}

		// Finally return the result to the caller
		return(result);
	}

	/**
	 * Fetches an object using the supplied path and id
	 * @param tenantId The tenant that the object is to be created for
	 * @param path the path to be used to fetch the object, only include the part after "/ill/"
	 * @param id The id of the object to be fetched
     * @param attributes A map of any query attributes that need to be passed in (default: null)
	 * @return The result of performing the fetch
	 */
    protected RestResult fetchObject(String tenantId, String path, String id, Map attributes = null) {
        // Set the headers
        setHeaders([ 'X-Okapi-Tenant': tenantId ]);

		RestResult result = null;
		String url = "${baseUrl}/ill/${path}";
        if (id != null) {
            url += "/${id}";
        }
		log.debug("Fetching object from ${url}");

		try {
	        // Fetch the object
	        def response = doGet(url, attributes);

			// Create ourselves a new result object
			result = new RestResult(response);
		} catch (groovyx.net.http.HttpException e) {
			result = new RestResult(e);
		}

		// Output an appropriate log message
		log.debug("Response from GET, Status Code: ${result.statusCode}, Body: ${result.responseBody.toString()}");

		return(result);
    }

	/**
	 * Performs a search using the supplied and value
	 * @param tenantId The tenant that the object is to be created for
	 * @param path the path to be used to search for objects, only include the part after "/ill/"
	 * @param searchAttribute The attribute to perform the search against
	 * @param attributeValue The value to search for
	 * @return The result of performing the search
	 */
	protected RestResult searchForObjects(String tenantId, String path, String searchAttribute, String attributeValue) {
        // Set the headers
        setHeaders([ 'X-Okapi-Tenant': tenantId ]);

		RestResult result = null;
		String url = "${baseUrl}/ill/${path}";
		String filters = "${searchAttribute}==${attributeValue}";
		log.debug("Searching for objects from ${url} with filters ${filters}");

		try {
			// Perform a search
			def response = doGet(url, [ filters : filters ]);

			// Create ourselves a new result object
			result = new RestResult(response);

		} catch (groovyx.net.http.HttpException e) {
			result = new RestResult(e);
		}

		// Output an appropriate log message
		log.debug("Response from search, Status Code: ${result.statusCode}, Body: ${result.responseBody.toString()}");

		return(result);
	}

	/**
	 * Updates an object by putting the supplied objectDefinition to the end point
	 * @param tenantId The tenant that the object is to be created for
	 * @param path the path to be used to update the object, only include the part after "/ill/"
	 * @param objectDefinition A map defining the object the object to be updated
	 * @return The result of updating the record
	 */
    protected RestResult updateObject(String tenantId, String path, String id, Map objectDefinition) {

        // Set the headers
        setHeaders([ 'X-Okapi-Tenant': tenantId ]);

		RestResult result = null;
		String url = "${baseUrl}/ill/${path}/${id}";
        String json = (new JsonBuilder(objectDefinition)).toString();
		log.debug("Updating object for path ${url} with id ${id} and body ${json}");

		try {
            // Perform the update
            def response = doPut(url, null, null, {
                // Note: request is of type groovyx.net.http.HttpConfigs$BasicRequest
                request.setBody(json);
            });

			// Create ourselves a new result object
			result = new RestResult(response);

		} catch (groovyx.net.http.HttpException e) {
			result = new RestResult(e);
		}

		// Output an appropriate log message
		log.debug("Response from update, Status Code: ${result.statusCode}, Body: ${result.responseBody.toString()}");

		return(result);
    }

	/**
	 * Deletes an object using the supplied path and id
	 * @param tenantId The tenant that the object is to be created for
	 * @param path the path to be used to delete the object, only include the part after "/ill/"
	 * @param id The id of the object to be deleted
	 * @return The result of performing the delete
	 */
	protected RestResult deleteObject(String tenantId, String path, String id) {
        // Set the headers
        setHeaders([ 'X-Okapi-Tenant': tenantId ]);

		RestResult result = null;
		String url = "${baseUrl}/ill/${path}/${id}";
		log.debug("Deleting object ${id} with path ${url}");

		try {
			def response = doDelete("${url}");

			// Create ourselves a new result object
			result = new RestResult(response);

		} catch (groovyx.net.http.HttpException e) {
			result = new RestResult(e);
		}

		// Output an appropriate log message
		log.debug("Response from delete, Status Code: ${result.statusCode}, Body: ${result.responseBody.toString()}");

		return(result);
	}
}

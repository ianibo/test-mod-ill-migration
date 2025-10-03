package com.k_int.ill;

import static groovyx.net.http.HttpBuilder.configure;

import javax.servlet.http.HttpServletRequest;

import com.k_int.directory.Symbol;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.sharedindex.SharedIndexAvailabilityResult;
import com.k_int.ill.sharedindex.SharedIndexBibRecord;
import com.k_int.ill.sharedindex.SharedIndexResult;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService
import com.k_int.settings.SystemSettingsService

import groovyx.net.http.FromServer;
import groovyx.net.http.HttpBuilder;

/**
 * The interface between mod-ill and the shared index is defined by this service.
 *
 */
public class FolioSharedIndexService implements SharedIndexActions {

    InstitutionSettingsService institutionSettingsService;
    SystemSettingsService systemSettingsService;

 /**
   * findAppropriateCopies - Accept a map of name:value pairs that describe an instance and see if we can locate
   * any appropriate copies in the shared index.
   * @param institution The institution the call is for
   * @param description A Map of properies that describe the item. Currently understood properties:
   *                         systemInstanceIdentifier - Instance identifier for the title we want copies of
   *                         title - the title of the item
   * @return instance of SharedIndexAvailability which tells us where we can find the item.
   */
  public List<AvailabilityStatement> findAppropriateCopies(Institution institution, Map description) {

    List<AvailabilityStatement> result = []
    log.debug("findAppropriateCopies(${description})");

    // Use the shared index to try and obtain a list of locations
    try {
      log.debug("Try graphql")
      if ( description?.systemInstanceIdentifier != null ) {
        log.debug("Query shared index for holdings of system instance identifier: ${description?.systemInstanceIdentifier}");

        sharedIndexHoldings(description?.systemInstanceIdentifier).each { shared_index_availability ->
          log.debug("add shared index availability: ${shared_index_availability}");

          // We need to look through the identifiers to see if there is an identifiier where identifierTypeObject.name == shared_index_availability.symbol
          // If so, that identifier is the instanceIdentifier in the shared index for this item - I know - it makes my brain hurt too


          result.add(new AvailabilityStatement(
            symbol: shared_index_availability.symbol,
            instanceIdentifier: shared_index_availability.instanceIdentifier,
            copyIdentifier: shared_index_availability.copyIdentifier,
            illPolicy: shared_index_availability.illPolicy
          ));
        }
      }
      else {
        log.warn("No shared index identifier for record. Cannot use shared index");
      }
    }
    catch ( Exception e ) {
      log.error("Graphql failed",e);
    }

    // See if we have an app setting for lender of last resort
    String last_resort_lenders = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_LAST_RESORT_LENDERS);
    if ( last_resort_lenders && ( last_resort_lenders.length() > 0 ) ) {
      String[] additionals = last_resort_lenders.split(',');
      additionals.each { al ->
        if ( ( al != null ) && ( al.trim().length() > 0 ) ) {
          result.add(new AvailabilityStatement(symbol:al.trim(), instanceIdentifier:null, copyIdentifier:null));
        }
      }
    }

    return result;
  }

  /**
   * ToDo: This method should expose critical errors like 404 not found so that we can cleanly
   * log the problem in the activity log
   */
  public SharedIndexResult fetchSharedIndexRecords(Map description) {
    String id = description?.systemInstanceIdentifier;
    if (!id) {
      log.debug("Unable to retrieve shared index record, systemInstanceIdentifier not specified and other fields not supported by this implementation");
      return null;
    }

    log.debug("fetchSharedIndexRecord(${id})");

    SharedIndexResult result = new SharedIndexResult();

    String shared_index_base_url = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_BASE_URL);
    String shared_index_user = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_USER);
    String shared_index_pass = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_PASS);
    String shared_index_tenant =  systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_TENANT);

    if ( ( shared_index_base_url != null ) &&
         ( shared_index_user != null ) &&
         ( shared_index_pass != null ) &&
         ( id != null ) &&
         ( id.length() > 0 ) ) {
      log.debug("Attempt to retrieve shared index record ${id} from ${shared_index_base_url} ${shared_index_user}/${shared_index_pass}");
      String token = getOkapiToken(shared_index_base_url, shared_index_user, shared_index_pass, shared_index_tenant);
      if ( token ) {
        def r1 = configure {
           request.headers['X-Okapi-Tenant'] = shared_index_tenant;
           request.headers['X-Okapi-Token'] = token
          request.uri = shared_index_base_url+'/inventory/instances/'+(id.trim());
        }.get() {
          response.success { FromServer fs, Object body ->
            log.debug("Success response from shared index");
            // Do not know the format of a record from the folio shared index to convert at the moment,
            // when we do add a conversion, for time being just adding a blank record
            result.totalRecords = 1;
            result.results.add(new SharedIndexBibRecord());
          }
          response.failure { FromServer fs ->
            log.debug("Failure response from shared index ${fs.getStatusCode()} when attempting to GET ${id}");
          }
        }
      }
      else {
        log.warn("Unable to login to remote shared index");
      }
    }
    else {
      log.debug("Unable to contact shared index - no url/user/pass");
    }

    return result;
  }

  // Proxy a discovery request to an appropriate SI endpoint
  //
  // Doesn't seem to be a clear way to just disable the parser irrespective of
  // content-type, will probably ultimately want to eschew HttpBuilder entirely
  // for this interface
  public HttpBuilder queryPassthrough(HttpServletRequest request) {
    return sharedIndexRequest('/inventory/instances', request.getQueryString(), null);
  }

    public SharedIndexAvailabilityResult availability(String id) {

        log.debug("availabilty(${id})");
        // Not implemented so we just return an empty result
        SharedIndexAvailabilityResult sharedIndexAvailabilityResult = new SharedIndexAvailabilityResult();

        return(sharedIndexAvailabilityResult);
    }

  private String getOkapiToken(String baseUrl, String user, String pass, String tenant) {
    String result = null;
    def postBody = [username: user, password: pass]
    log.debug("getOkapiToken(${baseUrl},${postBody},..,${tenant})");
    try {
      def r1 = configure {
        request.headers['X-Okapi-Tenant'] = tenant
        request.headers['accept'] = 'application/json'
        request.contentType = 'application/json'
        request.uri = baseUrl+'/authn/login'
        request.uri.query = [expandPermissions:true,fullPermissions:true]
        request.body = postBody
      }.get() {
        response.success { resp ->
          if ( resp == null ) {
            log.error("Response null from http post");
          }
          else {
            log.debug("Try to extract token - ${resp} ${resp?.headers}");
            def tok_header = resp.headers?.find { h-> h.key == 'x-okapi-token' }
            if ( tok_header ) {
              result = tok_header.value;
            }
            else {
              log.warn("Unable to locate okapi token header amongst ${r1?.headers}");
            }
          }

        }
        response.failure { resp ->
          log.error("RESP ERROR: ${resp.getStatusCode()}, ${resp.getMessage()}, ${resp.getHeaders()}")
        }
      }
    }
    catch ( Exception e ) {
        log.error("problem trying to obtain auth token for shared index",e);
      }

    log.debug("Result of okapi login: ${result}");
    return result;
  }


  private HttpBuilder sharedIndexRequest(String path, String query, String body) {
    String shared_index_base_url = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_BASE_URL);
    String shared_index_user = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_USER);
    String shared_index_pass = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_PASS);
    String shared_index_tenant =  systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_TENANT);

    if ( ( shared_index_base_url != null ) &&
         ( shared_index_user != null ) &&
         ( shared_index_pass != null ) ) {
      long start_time = System.currentTimeMillis();
      try {
        String token = getOkapiToken(shared_index_base_url, shared_index_user, shared_index_pass, shared_index_tenant);
        if ( token ) {
          log.debug("Attempt to initiate shared index request to ${path}");
          return configure {
            request.headers['X-Okapi-Tenant'] = shared_index_tenant;
            request.headers['X-Okapi-Token'] = token
            request.uri = shared_index_base_url + path + ("?${query}" ?: '')
            request.contentType = 'application/json'
            if (body != null) {
              request.body = body
            }
          }
        }
        else {
          log.warn("Unable to login to remote shared index");
          return null;
        }
      }
      catch ( Exception e ) {
        log.error("Problem attempting get in shared index",e);
        return null;
      }
    }
    else {
      def missingSettings = [];
      if( shared_index_base_url == null ) {
        missingSettings += "url";
      }
      if( shared_index_user == null ) {
        missingSettings += "user";
      }
      if( shared_index_pass == null ) {
        missingSettings += "password";
      }
      log.debug("Unable to contact shared index - Missing settings: ${missingSettings}");
    }

    return null;
  }


  // https://github.com/folio-org/mod-graphql/blob/master/doc/example-queries.md#using-curl-from-the-command-line

  private List<Map> sharedIndexHoldings(String id) {

    List<Map> result = []

  // "query": "query($id: String!) { instance_storage_instances_SINGLE(instanceId: $id) { id title holdingsRecord2 { holdingsInstance { id callNumber holdingsStatements } } } }",
    String query='''{
  "query": "query($id: String!) { instance_storage_instances_SINGLE(instanceId: $id) { id title identifiers { value identifierTypeObject { id name } } holdingsRecords2 { id callNumber illPolicy { name }  permanentLocation { name code } holdingsStatements { note statement } bareHoldingsItems { id barcode enumeration } } } }",
  "variables":{ "id":"'''+id+'''" } }'''

    log.debug("Sending graphql to get holdings for (${id}) \n${query}\n");

    String shared_index_base_url = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_BASE_URL);
    String shared_index_user = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_USER);
    String shared_index_pass = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_PASS);
    String shared_index_tenant =  systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_TENANT);

    if ( ( shared_index_base_url != null ) &&
         ( shared_index_user != null ) &&
         ( shared_index_pass != null ) ) {
      long start_time = System.currentTimeMillis();
      try {
        String token = getOkapiToken(shared_index_base_url, shared_index_user, shared_index_pass, shared_index_tenant);
        if ( token ) {
          log.debug("Attempt to retrieve shared index record ${id}");
          def r1 = configure {
            request.headers['X-Okapi-Tenant'] = shared_index_tenant;
            request.headers['X-Okapi-Token'] = token
            request.uri = shared_index_base_url+'/graphql'
            request.contentType = 'application/json'
            request.body = query
          }.get() {
            response.success { FromServer fs, Object r1 ->
              if ( ( r1 != null ) &&
                   ( r1.data != null ) ) {
                // We got a response from the GraphQL service - example {"data":
                // {"instance_storage_instances_SINGLE":
                //     {"id":"5be100af-1b0a-43fe-bcd6-09a67fb9c779","title":"A history of the twentieth century in 100 maps",
                //      "holdingsRecords2":[
                //         {"id":"d045fd86-fdcf-455f-8f42-e7bbaaf5ddd6","callNumber":" GA793.7.A1 ","permanentLocationId":"87038e41-0990-49ea-abd9-1ad00a786e45","holdingsStatements":[]}
                //      ]}}}

                log.debug("Response for holdings on ${id}\n\n${r1.data}\n\n");

                r1.data.instance_storage_instances_SINGLE?.holdingsRecords2?.each { hr ->
                  log.debug("Process holdings record ${hr}");
                  String location = hr.permanentLocation.code
                  String[] split_location = location.split('/')
                  if ( split_location.length == 4 ) {
                    // If we successfully parsed the location as a 4 part string: TempleI/TempleC/Temple/Temple

                    String local_symbol = convertSILocationToSymbol(split_location[0])
                    if ( local_symbol != null ) {

                      // Do we already have an entry in the result for the given location? If not, Add it
                      if ( result.find { it.symbol==local_symbol } == null ) {
                        // And we don't already have the location

                        // Iterate through identifiers to try and find one with the same identifierTypeObject.name as our symbol
                        // Very unsure about this, so wrapping for now
                        def instance_identifier = null;
                        try {
                          instance_identifier = r1.data.instance_storage_instances_SINGLE?.identifiers.find { it.identifierTypeObject?.name?.equalsIgnoreCase(local_symbol)} ?. value
                        }
                        catch ( Exception e ) {
                          log.error("Exception iterating through the identifierse", e);
                        }

                        log.debug("adding ${local_symbol} - ${instance_identifier} with policy ${hr.illPolicy?.name}");
                        result.add([
                                    symbol:local_symbol,
                                    illPolicy:hr.illPolicy?.name,
                                    instanceIdentifier:instance_identifier,
                                    copyIdentifier:null ])
                      }
                      else {
                        log.debug("Located existing entry in result for ${location} - not adding another");
                      }
                    }
                    else {
                      log.warn("Unable to resolve shared index symbol ${split_location}");
                    }
                  }
                  else {
                    log.warn("Location code does not split into 4: ${location}");
                  }
                }
              }
              else {
                log.error("Unexpected data back from shared index");
              }
            }

            response.failure { FromServer fs, Object r1 ->
              log.warn("** Failure response from shared index ${fs.getStatusCode()} when attempting to send Graphql to ${shared_index_base_url}/graphql - query: ${query}");
              log.warn("** Response object: ${r1}");
            }

          }
        }
        else {
          log.warn("Unable to login to remote shared index");
        }
      }
      catch ( Exception e ) {
        log.error("Problem attempting get in shared index",e);
      }
      finally {
        log.debug("Shared index known item lookup returned. ${System.currentTimeMillis() - start_time} elapsed");
      }
    }
    else {
      def missingSettings = [];
      if( shared_index_base_url == null ) {
        missingSettings += "url";
      }
      if( shared_index_user == null ) {
        missingSettings += "user";
      }
      if( shared_index_pass == null ) {
        missingSettings += "password";
      }
      log.debug("Unable to contact shared index - Missing settings: ${missingSettings}");
    }

    log.debug("Result: ${result}");
    return result;
  }

  private String convertSILocationToSymbol(String si_location) {
    log.debug("convertSILocationToSymbol(${si_location})");
    // return 'ILL:'+si_location
    String result = null;
    // Try to resolve the symbol without a namespace
    List<Symbol> r = Symbol.findAllBySymbol(si_location.trim().toUpperCase())
    if ( r.size() == 1 ) {
      log.debug("Located unique symbol without namespace");
      Symbol s = r.get(0)
      result = "${s.authority.symbol}:${s.symbol}".toString()
    }
    else if ( r.size() > 1 ) {
      log.debug("Symbol is not unique over namespace");
      // The symbol was not uniqe over namespaces, try our priority list
      ['ILL', 'ISIL'].each {
        log.debug("Trying to locate symbol ${si_location} in ns ${it}");
        if ( result == null ) {
          try {
            List<Symbol> r2 = Symbol.executeQuery('select s from Symbol as s where s.authority.symbol=:a and s.symbol=s',[a:it, s:si_location.trim().toUpperCase()]);
            if ( r2.size() == 1 ) {
              Symbol s = r2.get(0)
              result = "${s.authority.symbol}:${s.symbol}".toString()
            }
          } catch(Exception e) {
            log.error("Error trying to locate symbol ${si_location} in ${it}: ${e.getMessage()}")
          }
        }
      }
    }
    log.debug("convertSILocationToSymbol result: ${si_location}");
    return result
  }

}


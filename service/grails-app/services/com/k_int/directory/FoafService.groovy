package com.k_int.directory

import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.k_int.web.toolkit.custprops.CustomPropertyDefinition;
import com.k_int.web.toolkit.custprops.types.CustomPropertyText;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.events.annotation.Subscriber;
import grails.gorm.multitenancy.Tenants;
import grails.web.databinding.DataBinder;
import groovyx.net.http.ContentType;
import groovyx.net.http.HTTPBuilder;
import groovyx.net.http.Method;

/**
 *
 */
class FoafService implements DataBinder {

  def sessionFactory
  def grailsApplication

  private long MIN_READ_INTERVAL = 60 * 60 * 24 * 2 * 1000; // 2 days between directory reads

  // This is important! without it, all updates will be batched inside a single transaction and
  // we don't want that.
  static transactional = false;

  private static String GROUP_MEMBER_QUERY = '''select gm.id
from GroupMember as gm
where gm.groupOrg.slug=:group
and gm.memberOrg.slug=:member
'''

  private static List MANAGED_CUSTPROPS = ['local_institutionalPatronId',
     'policy.ill.loan_policy',
     'policy.ill.last_resort',
     'policy.ill.returns',
     'policy.ill.InstitutionalLoanToBorrowRatio']

  private ThreadPoolExecutor executor = null;

  @javax.annotation.PostConstruct
  public void init() {
    log.info("FoafService::init");
    executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    if ( grailsApplication.config?.folio?.directory?.defualtttl ) {
      MIN_READ_INTERVAL = Long.parseLong("${grailsApplication.config?.folio?.directory?.defualtttl}")?.longValue();
    }
    log.info("Default ttl for directory entries will be ${MIN_READ_INTERVAL}");
  }

  @javax.annotation.PreDestroy
  void shutdown() {
    if ( executor ) {
      log.info("Cleaning up foaf harvester thread pool");
      executor.shutdownNow()
    }
  }



  @Subscriber('okapi:dataload:sample')
  public void afterSampleLoaded (final String tenantId, final String value, final boolean existing_tenant, final boolean upgrading, final String toVersion, final String fromVersion) {
    log.debug("FoafService::afterSampleLoaded");
    // See if we can find the URL of our seed entry in the directory
    // Tenants.withId(tenantId+'_mod_ill') {
    //   checkFriend('https://raw.githubusercontent.com/openlibraryenvironment/mod-directory/master/seed_data/olf.json');
    // }
    log.debug("FoafService::afterSampleLoaded COMPLETE");
  }


  /**
   * Check a URL to see if it needs to be visited.
   */
  public void checkFriend(String url, int depth = 0) {
    if ( ( url != null ) &&
         ( url.length() > 5 ) &&
         ( url.toLowerCase().startsWith('http') ) &&
         ( depth < 4 ) ) {
      if ( shouldVisit(url) ) {
        processFriend(url, depth);
      }
      else {
        log.debug("Skip: ${url}");
      }
    }
  }

  // return true if we should attempt to visit this URL - currently
  // returns false if we already know about this URL and have recently visited it
  private boolean shouldVisit(String url) {
    boolean result = false;

    // Count all the directory entries for url=X and cache still valid
    int p = DirectoryEntry.executeQuery('select count(de.id) from DirectoryEntry as de where de.foafUrl=:url and ( :now < ( de.foafTimestamp + :min_interval ) )',
                                                    [url:url, min_interval:MIN_READ_INTERVAL, now: System.currentTimeMillis()])[0];
    if ( p == 0 ) {
      // unable to find that URL OR the cache has expired, so we should re-visit
      result = true;
    }
    else {
      // We found that URL and it's cache data is still within MIN_READ_INTERVAL, so we don't need to check
      result = false;
    }

    log.debug("shouldVisit(${url}) : ${result}(${p})");

    return result;
  }

  private void processFriend(String url, int depth=0) {
    log.debug("processFriend(${url},${depth})");
    try {
      def http = new HTTPBuilder(url)

      log.debug("HTTP GET ${url}");
      //http.auth.basic ('username','password')
      http.request(Method.GET, ContentType.JSON) {
        headers.'Content-Type' = 'application/json'
        response.success = { resp, json ->
          log.debug("HTTP get ${url} :: Success");
          // Make sure that the JSON really is an array of foaf descriptions
          if ( validateJson(json) ) {
            def sdf = new SimpleDateFormat('yyyy-MM-dd\'T\'HH:mm:ssX')
            Long parsed_last_modified = null;
            try {
              parsed_last_modified = json.lastModified ? sdf.parse(json.lastModified).getTime() : null;
            }
            catch ( Exception e ) {}

            log.debug("Entry has last_modified : ${parsed_last_modified}");

			// We handle the addresses separately
            Object addresses_list = json.remove('addresses')

            // Remove friends before processing
            Object friends_list = json.remove('friends')
            Object member_list = json.remove('members')

            // Remove the friends list - we will process it later on
            Object announcements = json.remove('announcements')

			// Uppercase all the symbols
			upperCaseSymbols(json.symbols);

			// Check the unit symbols
			checkUnitsSymbols(json.units);

            // StatelessSession session = sessionFactory.openStatelessSession();
            // Transaction tx = session.beginTransaction();

            DirectoryEntry.withSession { session ->

              session.clear();
              DirectoryEntry.withTransaction { status ->
                log.info("processing directory entry ${url} - see if we have an entry for that URL or slug ${json.slug}\n\n");

                // Look up the directory entry for the root
                DirectoryEntry de = DirectoryEntry.findByFoafUrlOrSlug(url, json.slug)


                log.debug("Result of DirectoryEntry.findByFoafUrlOrSlug(${url},${json.slug}) : ${de?.id}(${de?.version})")

                if ( de == null ) {
                  String new_id = java.util.UUID.randomUUID().toString();
                  log.debug("Create a new directory entry(id:${new_id},foafUrl:${url}, name:${json.name})")
                  de = new DirectoryEntry( foafUrl:url, name: json.name, pubLastUpdate:parsed_last_modified)
                  de.id=new_id;
                }
                else {

                  if ( ( parsed_last_modified != null ) &&
                       ( de.pubLastUpdate != null ) &&
                       ( de.pubLastUpdate == parsed_last_modified ) ) {
                    log.debug("Entry has not changed since last visit");
                    return;
                  }

                  if ( de.status?.value?.equalsIgnoreCase('Managed') ) {
                    // This directory entry is managed by this installation - do not accept updates from the the network
                    log.debug("Update detected for Managed directory entry - Skip");
                    return;
                  }

                  log.debug("DE already exists - lock and refresh (${de.id},${de.version})");
                  de.lock()
                  de.pubLastUpdate = parsed_last_modified;

				  // Clear out the current values, they should get repopulated
				  clearDirectoryEntryRecord(de);
                }

                log.debug("bind json ${json}");

                // Custom properties need special attention
                def custprops = json.remove('customProperties');

                bindData (de, json)

                json.customProperties = custprops;
                bindCustomProperties(de, json)
                expireRemovedSymbols(de, json)
				bindAddresses(de, addresses_list);

                if ( ( de.foafUrl == null ) && ( url != null ) )
                  de.foafUrl = url;

                // update the touched timestamp
                de.foafTimestamp = System.currentTimeMillis();

                // dumpDE(de);

                // save
                log.debug("Save de - ID is ${de.id}");
                de.save(flush:true, failOnError:true);
                session.flush();
              }
            }

            // tx.commit();
            // session.close();

            // Process any friends
            log.debug("Processing friends list from entry at (${url}): ${friends_list}");
            if ( friends_list != null ) {
              friends_list.each { fr ->
                if ( fr.foaf ) {
                  checkFriend(fr.foaf, depth+1);
                }
              }
            }

            try {
              if ( member_list != null ) {
                log.debug("Process members");
                boolean save_needed = false;

                // After we have processed all friend links lets make any links we need to
                DirectoryEntry.withSession { session ->
                  session.clear();
                  DirectoryEntry.withTransaction { status ->

                    DirectoryEntry owner = DirectoryEntry.findBySlug(json.slug);

                    if ( owner != null ) {
                      member_list.each { mem ->
                        log.debug("Checking member ${mem}");
                        List<GroupMember> gm_list = GroupMember.executeQuery(GROUP_MEMBER_QUERY, [group:json.slug, member: mem.memberOrg])
                        if ( gm_list.size() == 0 ) {
                          DirectoryEntry member_org = DirectoryEntry.findBySlug(mem.memberOrg);
                          if ( member_org != null ) {
                            def gm = new GroupMember( groupOrg: owner, memberOrg: member_org).save(flush:true, failOnError:true);
                          }
                          else {
                            log.error("Cosortium contained a member org : ${mem.memberOrg} for parent ${json.slug} that could not be located in the directory");
                          }
                        }
                      }
                    }
                    else {
                      log.error("Owner slug ${json.slug} not found");
                    }
                  }
                }
              }
            }
            catch ( Exception e ) {
              log.error("Problem processing member list",e);
            }
          }
          else {
            log.error("Failed to validate foaf JSON from ${url}");
          }
        }
        response.failure = { json ->
          log.warn("Problem processing FOAF URL ${url}");
        }
      }
    }
    catch ( Exception e ) {
      log.error("Problem trying to process FOAF URL ${url} : ${e.message}",e)
    }
    finally {
      log.debug("leaving processFriend(${url},${depth}");
    }
  }

  private void upperCaseSymbols(List symbols) {
	  // Uppercase all the symbols
	  if (symbols != null) {
		  symbols.each { symbol ->
			   symbol.authority = symbol.authority.toUpperCase();
			   symbol.symbol = symbol.symbol.toUpperCase();
		  }
	  }
  }

  private void checkUnitsSymbols(List units) {
	  // If we have any symbols uppercase them
	  if (units != null) {
		  units.each { unit ->
			  // Uppercase them
			  upperCaseSymbols(unit.symbols);

			  // Now resurse through the units if we got down through several lwvwla
			  checkUnitsSymbols(unit.units);
		  }
	  }
  }

  private void clearDirectoryEntryRecord(DirectoryEntry directoryEntry) {
      // Could use
      // List<PersistentProperty> properties = Holders.grailsApplication.mappingContext.getPersistentEntity(DirectoryEntry.class.name).persistentProperties;
      // and then process the properties, if we wanted to make this truly dynamic and clear everything, but I do not think this case warrants it

      // Set all the fields to null, apart from id, slug name, type and pubLastUpdate
      directoryEntry.description = null;
      directoryEntry.foafUrl = null;
      directoryEntry.entryUrl = null;
      directoryEntry.phoneNumber = null;
      directoryEntry.emailAddress = null;
      directoryEntry.contactName = null;
      directoryEntry.brandingUrl = null;
      directoryEntry.lmsLocationCode = null;
      directoryEntry.foafTimestamp = null;
      directoryEntry.parent = null;
      directoryEntry.status = null;

      // Now for all the lists
	  // We do not remove units, friends, members, addresses or symbols
	  if (directoryEntry.tags != null) {
		  directoryEntry.tags.collect().each { tag ->
			  directoryEntry.removeFromTags(tag)
		  }
	  }

	  if (directoryEntry.services != null) {
		  directoryEntry.services.collect().each { service ->
			  directoryEntry.removeFromServices(service)
		  }
	  }

	  if (directoryEntry.announcements != null) {
		  directoryEntry.announcements.collect().each { announcement ->
			  directoryEntry.removeFromAnnouncements(announcement)
		  }
	  }
  }

  private boolean validateJson(json) {
    boolean result = false;
    if ( ( json.name != null ) &&
         ( json.name.length() > 0 ) ) {
      result = true;
    }
    return result;
  }

  // return true if record updated
  private boolean updateFromJson(DirectoryEntry de, Map json) {
    boolean result = false

    // do update
    result &= mergeField('name', 'name', de, json);
    // result &= mergeField('url', 'url', de, json);
    result &= mergeField('description', 'description', de, json);

    // Normally immutable
    // result &= mergeField('slug', 'slug', de, json);

    return result;
  }

  /**
   * Check to see if fieldname is present in json, and if so, compare it to the current value of that field in the object
   * if different, set that field on the object and return true to signify that the record was updated.
   */
  private boolean mergeField(String jsonField, String domainModelField, Object obj, Map json) {
    boolean result = false;
    if ( json[jsonField] != null ) {
      obj[domainModelField] = json[jsonField]
      result = true;
    }
    return result;
  }

  private void dumpDE(DirectoryEntry de, depth=0) {
    if ( de != null ) {
      log.debug("${'  '*depth} - DE ${de.id} ${de.version} ${de.slug}");
      log.debug("${'  '*depth} - Symbols array: ${de.symbols?.class?.name} ${de.symbols?.size()}");
      de.symbols.each {
        log.debug("${'  '*depth}   - SYMBOL ${it.id} ${it}");
      }
      de.units.each {
        dumpDE(it, depth+1);
      }
    }
  }


  // Enqueue a freshen request
  public freshen(String tenant) {
    log.debug("FoafService::freshen(${tenant})");
    // Promise p = task {
    executor.submit {
      this.doFreshenWork(tenant);
    } as Runnable

    log.debug("freshen task enqueued");
  }

  private void doFreshenWork(String tenant) {
    log.debug("FoafService::doFreshenWork(${tenant})");
    try {
      Tenants.withId(tenant+'_mod_ill') {
        DirectoryEntry.withNewSession {
          DirectoryEntry.executeQuery('select de.foafUrl, de.foafTimestamp from DirectoryEntry as de where de.foafUrl is not null').each { row ->
            def foaf_url = row[0]
            def foaf_ts = row[1]
            log.debug("freshen() checking ${foaf_url} last:${foaf_ts} remaining:${(MIN_READ_INTERVAL - ( System.currentTimeMillis() - foaf_ts?:0 ))/(60000) }mins");
            checkFriend(foaf_url);
          }
          log.debug("freshen work complete");
        }
      }
    }
    catch ( Exception e ) {
      log.error("Problem in doFreshenWork", e);
    }
  }

  public void announce(String tenant) {
    log.debug("FoafService::announce(${tenant})");
  }

  private void bindCustomProperties(DirectoryEntry de, Map payload) {
    log.debug("Iterate over custom properties sent in directory entry payload ${payload.customProperties}");

    if ( de.customProperties == null ) {
      log.debug("New directory entry - need to initialise custprops container");
    }

    // cleanManagedCustomProperties(de);
    cleanAllCustomProperties(de);

    payload?.customProperties?.each { k, v ->
      // de.custprops is an instance of com.k_int.web.toolkit.custprops.types.CustomPropertyContainer
      // we need to see if we can find
      if ( MANAGED_CUSTPROPS.contains(k) ) {
        log.debug("processing binding for ${k} -> ${v}");
        boolean first = true;

        // Root level custom properties object is a custom properties container
        // We iterate over each custom property to see if it's one we want to process

        boolean existing_custprop_updated = false;

        // replace any existing property
        de.customProperties?.value.each { cp ->
          if ( cp.definition.name == k ) {
            log.debug("updating customProperties.${k}.value to ${v} - custprop type is ${cp.definition.type?.toString()}");
            if ( v instanceof List ) {
              // cp.value = v.get(0);
              mergeCustpropWithList(cp, v)
            }
            else if ( v instanceof String ) {
              // cp.value = v
              mergeCustpropWithString(cp, v)
            }
            existing_custprop_updated = true
          }
        }

        // IF we didn't update an existing property, we need to add a new one
        if ( existing_custprop_updated == false ) {
          log.debug("Need to add new custom property: ${k} -> ${v}");
          CustomPropertyDefinition cpd = CustomPropertyDefinition.findByName(k);
          if ( cpd != null ) {
            if ( ( v instanceof String ) && ( v != null ) && ( v.length() > 0 ) ) {
              // Something goes wrong here when we use the default map constructor to set definition and value
              // explicitly assigning them works fine, but the map constructor seems to leave the value as null
              CustomPropertyText new_text_value = new CustomPropertyText()
              new_text_value.definition = cpd;
              new_text_value.value = v;
              de.customProperties?.addToValue( new_text_value );
            }
            else if ( v instanceof List ) {
              if ( v.size() == 1 ) {
                // Something goes wrong here when we use the default map constructor to set definition and value
                // explicitly assigning them works fine, but the map constructor seems to leave the value as null
                CustomPropertyText new_text_value = new CustomPropertyText()
                new_text_value.definition = cpd;
                new_text_value.value = v[0];
                de.customProperties?.addToValue( new_text_value );
              }
              else {
                log.warn("List props size > 1 are not supported at this time")
              }
            }
          }
          else {
            log.warn("No definition for custprop ${k}. Skipping");
          }
        }
      }
      else {
        log.debug("skip binding for ${k} -> ${v}");
      }
    }
  }

  private void mergeCustpropWithList(Object cp_value, List binding_value) {
    log.debug("mergeCustpropWithList(${cp_value?.class?.name}, ${binding_value})");
    // log.debug("  -> existing cp value is ${cp_value?.value} / ${cp_value?.class?.name}");
    if ( cp_value instanceof com.k_int.web.toolkit.custprops.types.CustomPropertyText ) {
      // We're only concerned with single values for now.
      cp_value.value = binding_value.get(0)?.toString();
    }
    else if ( cp_value instanceof com.k_int.web.toolkit.custprops.types.CustomPropertyRefdata ) {
      String new_refdata_value = binding_value.get(0)
      log.debug("rebinding a custom property ${binding_value} with value ${new_refdata_value}");
      cp_value.value = cp_value.lookupValue(new_refdata_value);
    }
    else {
      log.warn("Re-binding values of type ${cp_value?.class?.name} is not currently supported - value is ${binding_value}");
    }

  }

  private void mergeCustpropWithString(Object cp_value, String binding_value) {
    log.debug("mergeCustpropWithString(${cp_value?.class?.name}, ${binding_value})");
    // log.debug("  -> existing cp value is ${cp_value?.value} / ${cp_value?.class?.name}");
    if ( cp_value instanceof com.k_int.web.toolkit.custprops.types.CustomPropertyText ) {
      cp_value.value = binding_value
    }
    else {
      log.warn("Unhandled rebinding of custprop type ${cp_value?.class?.name}");
    }
  }

  private void cleanManagedCustomProperties(DirectoryEntry de) {

    MANAGED_CUSTPROPS.each { k ->
      boolean first = true;
      boolean updated = false;

      List cps_to_remove = []

      de.customProperties?.value.each { cp ->

        if ( cp.definition.name == k ) {
          if ( first ) {
            first=false;
          }
          else {
            // Extra value - dispose of it.
            // In order to avoid concurrent modification exception, here we just collect together a list of all the custom property instances to be removed.
            cps_to_remove.add(cp);
          }
        }
      }

      cps_to_remove.each { cp ->
        de.customProperties?.removeFromValue(cp)
        updated = true;
      }
    }
  }

  private void cleanAllCustomProperties(DirectoryEntry de) {

    boolean updated = false;
    List props_seen = []
    List cps_to_remove = []

    de.customProperties?.value.each { cp ->
      if ( props_seen.contains(cp.definition.name) ) {
        log.debug("Removing unwanted prop value for ${cp.definition.name}");
        cps_to_remove.add(cp);
      }
      else {
        log.debug("Add ${cp.definition.name} to list of props see - this is the first one so it survives");
        props_seen.add(cp.definition.name);
      }
    }

    cps_to_remove.each { cp ->
      de.customProperties?.removeFromValue(cp)
      updated = true;
    }

  }

  private void bindAddresses(DirectoryEntry directoryEntry, ArrayList addresses) {
	  // Create a copy of the existing addresses
	  def addressesCopy = ((directoryEntry.addresses == null) ? [] : directoryEntry.addresses.collect());

	  if ((addresses != null) && !addresses.isEmpty()) {
		  // Loop through the supplied addresses
		  addresses.each { addressJson ->
			  boolean saveAddress = false;

			  // Does this address exist
			  Address address = directoryEntry.addresses.find { addr -> addr.addressLabel.equals(addressJson.addressLabel) };
			  if (address == null) {
				  // We have a new address record
				  address = new Address();
				  address.addressLabel = addressJson.addressLabel;

				  // Add the address to the directory entry
				  directoryEntry.addToAddresses(address);
			  } else {
				  // Remove from the copy
				  addressesCopy.removeAll{ addr -> addr.addressLabel.equals(addressJson.addressLabel) };
				  saveAddress = true;
			  }

			  // Deal with the other fields as they may have changed
			  address.countryCode = ((addressJson.countryCode == null) ? 'GB-ENG' : addressJson.countryCode);

			  // Now for the lines
			  def linesCopy = ((address.lines == null) ? [] : address.lines.collect());

			  if ((addressJson.lines != null) && !addressJson.lines.isEmpty()) {
				  addressJson.lines.each { lineJson ->
					  boolean saveLine = false;

					  // Does this line exist
					  AddressLine addressLine = address.lines.find {line -> line.seq == lineJson.seq };

					  // Did we find one with this sequence
					  if (addressLine == null) {
						  // Nope so create a new one
						  addressLine = new AddressLine();
						  addressLine.seq = lineJson.seq;

						  // Add it to the address
						  address.addToLines(addressLine);
					  } else {
						  // Yes we did, so remove it from the copy
						  linesCopy.removeAll{ line -> line.seq == lineJson.seq };
						  saveLine = true;
					  }

					  // Set the other fields
					  addressLine.type = RefdataValue.lookupOrCreate('AddressLine.Type', lineJson.type);
					  addressLine.value = lineJson.value;

					  // Do we need to save the address line as it is an edit
					  if (saveLine == true) {
						  addressLine.save(flush:true, failOnError:true);
					  }
				  }
			  }

			  // Remove any sequences that we havn't pulled in
			  linesCopy.each() { line ->
				  address.removeFromLines(line);
			  }

			  // Do we need to save the address as it is an edit
			  if (saveAddress == true) {
				  address.save(flush:true, failOnError:true);
			  }
		  }
	  }

	  // Remove all the addresses that have not been imported
	  addressesCopy.each() { addr ->
		  directoryEntry.removeFromAddresses(addr);
	  }
  }

  /**
   *  Sometimes a symbol can be removed in the record without the local cache copy of that relationship being removed. This function
   *  iterates over all symbols attached to a directory entry and flags up any which are not in the parsed json record. If the parsed
   *  JSON is authoritative, then we should remove the symbol or flag it in some way as being removed.
   *  Current implementation only detects, no action taken presently.
   */
  private void expireRemovedSymbols(DirectoryEntry de, Map payload) {
    log.debug("expireRemovedSymbols....");
    // In the payload root.symbols is an array of maps where each map contains the keys  authority:'ISIL', symbol:'RST1', priority:'a'
    // de.symbols is a List<Symbol>
    try {
      List symbols_to_remove = []

      de.symbols.each { dbsymbol ->
        log.debug("Verify symbol ${dbsymbol}");
        // Look in payload.symbols for a map entry where dbsymbol.symbol == entry.symbol and dbsymbol.authority.symbol == entry.authority
        def located_map_entry = payload.symbols.find { ( it.symbol.equals(dbsymbol.symbol) && it.authority.equals(dbsymbol.authority.symbol) ) }
        if ( located_map_entry ) {
          // DB symbol still present in data, no action needed
        }
        else {
          log.warn("Residual symbol still in db : ${dbsymbol} - should be removed")
          symbols_to_remove.add(dbsymbol);
        }
      }

      symbols_to_remove.each { symbol_to_remove ->
        try {
          log.debug("Remove ${symbol_to_remove}");
          // symbol_to_remove.delete()
          de.removeFromSymbols(symbol_to_remove);
        }
        catch ( Exception e ) {
          log.error("problem deleting symbol",e);
        }
      }
    }
    catch ( Exception e ) {
      log.error("Problem detecting residual symbols",e);
    }
  }
}

package com.k_int.ill.sharedindex;

import com.k_int.ill.AvailabilityStatement;
import com.k_int.ill.SharedIndexActions;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.sharedindex.jiscdiscover.JiscDiscoverApiConnection;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService

/**
 * The interface between mod-ill and the shared index is defined by this service.
 *
 * See also : https://discover.libraryhub.jisc.ac.uk/search?id=6172245&rn=1&format=json - restful API seems to carry more data.
 */
public class JiscDiscoverSharedIndexService implements SharedIndexActions {

    InstitutionSettingsService institutionSettingsService;

  @Autowired
  JiscDiscoverApiConnection jiscDiscoverApiConnection

 /**
   * See: https://discover.libraryhub.jisc.ac.uk/sru-api?operation=searchRetrieve&version=1.1&query=rec.id%3d%2231751908%22&maximumRecords=1
   *
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
      if ( description?.systemInstanceIdentifier != null ) {
        log.debug("Query shared index for holdings of system instance identifier: ${description?.systemInstanceIdentifier}");

        def sru_response = jiscDiscoverApiConnection.getSru(description);

        if ( sru_response?.numberOfRecords?.toString() == '1' ) {
          sru_response.records.record.recordData.mods.extension.modsCollection.mods.each { mr ->


            mr.recordInfo.recordIdentifier.each { loc_specific_record_id ->
              log.debug("    rec [${loc_specific_record_id.'@source'}] : ${loc_specific_record_id.text()}");
            }

            int lendable_copies = 0;
            int total_copies = 0;

            List<String> shelf_locations = []
            mr.location.each { loc ->
              String ukmac_code = loc.physicalLocation.find { it.'@authority'=='UkMaC' }
              log.debug("  UkMac location code: ${ukmac_code}");

              if ( ukmac_code ) {

                loc.holdingSimple.copyInformation.each { ci ->
                  // Have seen (Not Borrowable) in subLocation as an indication of policy
                  log.debug("    subloc: ${ci.subLocation}")
                  log.debug("    shelf: ${ci.shelfLocator}")
                  shelf_locations.add(ci.shelfLocator)
                  lendable_copies++;
                  total_copies++;
                }

                if ( lendable_copies > 0 ) {
                  log.debug("Add ${ukmac_code} to availability / ${shelf_locations.join(', ')}");
                  AvailabilityStatement avls = new AvailabilityStatement()
                  avls.symbol = ukmac_code
                  avls.instanceIdentifier = null;
                  avls.copyIdentifier = null;
                  avls.illPolicy = null;
                  avls.totalCopies = new Long(total_copies);
                  avls.availableCopies = new Long(lendable_copies)
                  result.add(avls);
                }
              }
            }

          }
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

    // Left here as a signpost in case we want to externalise the base URL of the jisc discover service
    // String shared_index_base_url = SettingsData.SETTING_SHARED_INDEX_BASE_URL);

    if ( ( id != null ) && ( id.length() > 0 ) ) {
      log.debug("Attempt to retrieve shared index record ${id} from Jisc LHD");

      def sru_response = jiscDiscoverApiConnection.getSru(description);

      if ( sru_response?.numberOfRecords?.toString() == '1' ) {
        sru_response.records.record.each { r ->
            // Do not know the format of a record from the folio shared index to convert at the moment,
            // when we do add a conversion, for time being just adding a blank record
            result.totalRecords++;
            result.results.add(new SharedIndexBibRecord());
        }
      }

      //
    }
    else {
      log.debug("No record ID provided - cannot lookup SI record at Jisc LHD");
    }

    return result;
  }

    public SharedIndexAvailabilityResult availability(String id) {

        log.debug("availabilty(${id})");
        // Not implemented so we just return an empty result
        SharedIndexAvailabilityResult sharedIndexAvailabilityResult = new SharedIndexAvailabilityResult();

        return(sharedIndexAvailabilityResult);
    }
}

package com.k_int.ill.sharedindex;

import com.k_int.ill.AvailabilityStatement;
import com.k_int.ill.SharedIndexActions;
import com.k_int.ill.settings.SharedIndexSettings;
import com.k_int.ill.sharedindex.openRS.availability.OpenRsAvailability;
import com.k_int.ill.sharedindex.openRS.availability.OpenRsAvailabilityResult;
import com.k_int.ill.sharedindex.openRS.clusterRecord.OpenRsClusterResult;
import com.k_int.ill.sharedindex.openRS.connections.OpenRsAvailabilityApiConnection;
import com.k_int.ill.sharedindex.openRS.connections.OpenRsSharedIndexApiConnection;
import com.k_int.institution.Institution;
import com.k_int.settings.SharedIndexSettingsService;

/**
 * The interface between mod-ill and the OpenRS shared index is defined by this service.
 *
 */
public class OpenrsSharedIndexService implements SharedIndexActions {

    @Autowired
    OpenRsAvailabilityApiConnection openRsAvailabilityApiConnection;

    @Autowired
    OpenRsSharedIndexApiConnection openRsSharedIndexApiConnection;

    @Autowired
    SharedIndexSettingsService sharedIndexSettingsService;

    public List<AvailabilityStatement> findAppropriateCopies(Institution institution, Map description) {

        List<AvailabilityStatement> result = [ ];
        log.debug("findAppropriateCopies(${description})");

        // Use the shared index to try and obtain a list of locations
        try {
            if (description?.systemInstanceIdentifier != null) {
                OpenRsAvailabilityResult openRsAvailabilityResult = openRsAvailabilityApiConnection.get(getSharedIndexSettings(), description.systemInstanceIdentifier);
                String authority = getSharedIndexSettings().getAvailabilityAuthority();

                // Did we find anything
                if ((authority != null) && (openRsAvailabilityResult?.itemList != null)) {
                    // WE did so find out what is available
                    openRsAvailabilityResult.itemList.each { OpenRsAvailability openRsAvailability ->
                        // Is this item requestable
                        if (openRsAvailability.isRequestable &&
                            (openRsAvailability.location?.code != null) &&
                            (openRsAvailability.agency?.code != null) &&
							(openRsAvailability?.status?.code == SharedIndexAvailabilityStatus.AVAILABLE)) {
                            AvailabilityStatement availabilityStatement = new AvailabilityStatement();
                            availabilityStatement.symbol = authority + ':' + openRsAvailability.agency.code.toUpperCase();
                            availabilityStatement.copyIdentifier = openRsAvailability.barcode;

                            // Add it to the list to be returned
                            result.add(availabilityStatement);
                        }
                    }
                }
            } else {
                log.warn("No shared index identifier for record. Cannot use shared index");
            }
        } catch (HttpHostConnectException ) {
			log.error("Connection timed out while trying to connect to OpenRS Shared index");
        } catch (Exception e) {
            log.error("Exception thrown while processing search results from OpenRS shared index", e);
        }
        return(result);
    }

    public SharedIndexResult fetchSharedIndexRecords(Map description) {
        String id = description?.systemInstanceIdentifier;
        OpenRsClusterResult openRsClusterResult = null;

        if (id) {
            log.debug("Attempt to retrieve shared index record ${id} from OpenRS");
            openRsClusterResult = openRsSharedIndexApiConnection.getId(getSharedIndexSettings(), id);

        } else {
            // Are we performing a query
            String query = description?.query;
            if (query) {
                Long size = 100;
                Long from = 0;
                try {
                    if (description.size instanceof String) {
                        size = description.size.toLong();
                    } else if (description.size instanceof Long) {
                        size = description.size;
                    }
                    if (description.from instanceof String) {
                        from = description.from.toLong();
                    } else if (description.from instanceof Long) {
                        from = description.from;
                    }
                } catch (Exception) {
                    // Ignore all conversion exceptions, as we have sensible defaults
                }

                // Now execute the query
                openRsClusterResult = openRsSharedIndexApiConnection.getQuery(getSharedIndexSettings(), query, from, size);
            } else {
                log.debug("No record ID provided - cannot lookup SI record at OpenRS");
            }
        }

        // Convert to a sharedIndexResult
        SharedIndexResult sharedIndexResult;
        if (openRsClusterResult == null) {
            sharedIndexResult = new SharedIndexResult();
        } else {
            sharedIndexResult = openRsClusterResult.toSharedIndexResult();
        }

        // Return the result to the caller
        return(sharedIndexResult);
    }

    public SharedIndexAvailabilityResult availability(String id) {

        log.debug("availabilty(${id})");
        SharedIndexAvailabilityResult sharedIndexAvailabilityResult = null;

        // Use the shared index to try and obtain a list of locations
        try {
            if (id) {
                OpenRsAvailabilityResult openRsAvailabilityResult = openRsAvailabilityApiConnection.get(getSharedIndexSettings(), id);
                sharedIndexAvailabilityResult = openRsAvailabilityResult.toSharedIndexAvailabilityResult();
            } else {
                log.warn("No shared index identifier for record. Cannot search for availability");
            }
        } catch (Exception e) {
            log.error("Exception thrown while processing search results from OpenRS shared index", e);
        }
        return(sharedIndexAvailabilityResult);
    }

    private SharedIndexSettings getSharedIndexSettings() {
        return(new SharedIndexSettings(sharedIndexSettingsService));
    }
}

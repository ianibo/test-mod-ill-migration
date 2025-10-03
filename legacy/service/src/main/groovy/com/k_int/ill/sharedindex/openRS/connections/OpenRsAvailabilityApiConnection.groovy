package com.k_int.ill.sharedindex.openRS.connections;

import com.k_int.ill.settings.SharedIndexSettings;
import com.k_int.ill.sharedindex.openRS.availability.OpenRsAvailabilityResult;

/**
 * This is the interface that allows us to talk to an OpenRS shared index
 */
public interface OpenRsAvailabilityApiConnection {

    /**
     * Retrieves the availability for the supplied cluster id
     * @param sharedIndexSettings The shared index settings that has the settings to connect to the Open RS shared service
     * @param id The cluster id to be searched against
     * @return The availability for the supplied item id
     */
    public OpenRsAvailabilityResult get(SharedIndexSettings sharedIndexSettings, String id);
}

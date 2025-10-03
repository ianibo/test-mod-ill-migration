package com.k_int.ill.sharedindex.openRS.connections;

import com.k_int.ill.settings.SharedIndexSettings;
import com.k_int.ill.sharedindex.openRS.clusterRecord.OpenRsClusterResult;

/**
 * This is the interface that allows us to talk to an OpenRS shared index
 */
public interface OpenRsSharedIndexApiConnection {

    /**
     * Performs a search against the Open RS shared index for the supplied cluster identifier
     * @param sharedIndexSettings The shared index settings
     * @param id The identifier of the cluster record we are interested in
     * @return The returned record or null if no record is found
     */
    public OpenRsClusterResult getId(SharedIndexSettings sharedIndexSettings, String id);

    public OpenRsClusterResult getQuery(SharedIndexSettings sharedIndexSettings, String queryString, long from, long size);

}

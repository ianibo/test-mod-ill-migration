package com.k_int.ill.sharedindex.openRS.connections;

import com.k_int.ill.settings.SharedIndexSettings;

/**
 * This is the interface that allows us to talk to an OpenRS token server
 */
public interface OpenRsTokenApiConnection {

    /**
     * Retrieves an authentication token using the settings for the shared index
     * @param sharedIndexSettings The shared index settings object to get the values from
     * @return The authentication token to use in subsequent calls or null if a token cannot be obtained
     */
    public String get(SharedIndexSettings sharedIndexSettings);

    /**
     * Retrieves an authentication token for the supplied username and password
     * along with the settings for the shared index
     * @param sharedIndexSettings The shared index settings object to get the values from
     * @param username The username to get a token for
     * @param password The password for the supplied username
     * @return The token for the supplied username and password or null if a token cannot be obtained
     */
    public String get(SharedIndexSettings sharedIndexSettings, String username, String password);
}

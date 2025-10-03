package com.k_int.ill.settings;

import com.k_int.settings.SharedIndexSettingsService;

import groovy.transform.CompileStatic;

/**
 * Manages and holds the settings used by the shared index functionality, the functionality has moved into a service
 * this has been kept to minimise the number of changes
 *
 * @author Chas
 *
 */
@CompileStatic
public class SharedIndexSettings {

    /** The service used to obtain the settings */
    private SharedIndexSettingsService sharedIndexSettingsService;

    public SharedIndexSettings(SharedIndexSettingsService sharedIndexSettingsService) {
        this.sharedIndexSettingsService = sharedIndexSettingsService;
    }

    /**
     * Retrieves the base url for the shared index
     * @return The base url to be used for any calls to the shared index
     */
    public String getBaseUrl() {
        return(sharedIndexSettingsService.getBaseUrl());
    }

    /**
     * Retrieves the username for authentication with the shared index
     * @return The username to be used for authentication with the shared index
     */
    public String getUser() {
        return(sharedIndexSettingsService.getUser());
    }

    /**
     * Retrieves the password for authentication with the shared index
     * @return The password to be used for authentication with the shared index
     */
    public String getPassword() {
        return(sharedIndexSettingsService.getPassword());
    }

    /**
     * Retrieves the authority that the agency code belongs returned in the results
     * @return The authority to prefix the agency with
     */
    public String getAvailabilityAuthority() {
        return(sharedIndexSettingsService.getAvailabilityAuthority());
    }

    /**
     * Retrieves the url to obtain the availability if it is not returned with the cluster record
     * @return The availability url
     */
    public String getAvailabilityUrl() {
        return(sharedIndexSettingsService.getAvailabilityUrl());
    }

    /**
     * Retrieves the client id if we need a token to access the availability url
     * @return The client id to obtain a token
     */
    public String getTokenclientId() {
        return(sharedIndexSettingsService.getTokenclientId());
    }

    /**
     * Retrieves the password if we need a token to access the availability url
     * @return The password to obtain a token
     */
    public String getTokenPassword() {
        return(sharedIndexSettingsService.getTokenPassword());
    }

    /**
     * Retrieves the secret if we need a token to access the availability url
     * @return The secret to obtain a token
     */
    public String getTokenSecret() {
        return(sharedIndexSettingsService.getTokenSecret());
    }

    /**
     * Retrieves the url that gives us the availability
     * @return The url to obtain the availability
     */
    public String getTokenUrl() {
        return(sharedIndexSettingsService.getTokenUrl());
    }

    /**
     * Retrieves the username if we need a token to access the availability url
     * @return The username to obtain a token
     */
    public String getTokenUser() {
        return(sharedIndexSettingsService.getTokenUser());
    }
}

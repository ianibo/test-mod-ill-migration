package com.k_int.settings;

import com.k_int.ill.referenceData.SettingsData;

import grails.config.Config;
import grails.core.support.GrailsConfigurationAware;

/**
 * Manages and holds the settings used by the shared index functionality
 *
 * @author Chas
 *
 */
public class SharedIndexSettingsService implements GrailsConfigurationAware {

    /** The service used to obtain the settings */
	@Autowired
    public SystemSettingsService systemSettingsService;

	// The settings we are interested in from the configuration
	private String sharedIndexBaseUrl = null;
	private String sharedIndexUsername = null;
	private String sharedIndexPassword = null;
	private String availabilityAuthority = null;
	private String availabilityUrl = null;
	private String tokenClientId = null;
	private String tokenPassword = null;
	private String tokenSecret = null;
	private String tokenUrl = null;
	private String tokenUser = null;
	
    void setConfiguration(Config config) {
        sharedIndexBaseUrl = config.getProperty('shared.index.url')
        sharedIndexPassword = config.getProperty('shared.index.password')
        sharedIndexUsername = config.getProperty('shared.index.username')
        availabilityAuthority = config.getProperty('shared.availability.authority')
        availabilityUrl = config.getProperty('shared.availability.url')
        tokenClientId = config.getProperty('shared.token.clientid')
        tokenPassword = config.getProperty('shared.token.password')
        tokenSecret = config.getProperty('shared.token.secret')
        tokenUrl = config.getProperty('shared.token.url')
        tokenUser = config.getProperty('shared.token.user')
    }

    /**
     * Retrieves the base url for the shared index
     * @return The base url to be used for any calls to the shared index
     */
    public String getBaseUrl() {
        return(getValue(sharedIndexBaseUrl, SettingsData.SETTING_SHARED_INDEX_BASE_URL));
    }

    /**
     * Retrieves the username for authentication with the shared index
     * @return The username to be used for authentication with the shared index
     */
    public String getUser() {
        return(getValue(sharedIndexUsername, SettingsData.SETTING_SHARED_INDEX_USER));
    }

    /**
     * Retrieves the password for authentication with the shared index
     * @return The password to be used for authentication with the shared index
     */
    public String getPassword() {
        return(getValue(sharedIndexPassword, SettingsData.SETTING_SHARED_INDEX_PASS));
    }

    /**
     * Retrieves the authority that the agency code belongs returned in the results
     * @return The authority to prefix the agency with
     */
    public String getAvailabilityAuthority() {
        return(getValue(availabilityAuthority, SettingsData.SETTING_SHARED_INDEX_AVAILABILITY_AUTHORITY));
    }

    /**
     * Retrieves the url to obtain the availability if it is not returned with the cluster record
     * @return The availability url
     */
    public String getAvailabilityUrl() {
        return(getValue(availabilityUrl, SettingsData.SETTING_SHARED_INDEX_AVAILABILITY_URL));
    }

    /**
     * Retrieves the client id if we need a token to access the availability url
     * @return The client id to obtain a token
     */
    public String getTokenclientId() {
        return(getValue(tokenClientId, SettingsData.SETTING_SHARED_INDEX_TOKEN_CLIENT_ID));
    }

    /**
     * Retrieves the password if we need a token to access the availability url
     * @return The password to obtain a token
     */
    public String getTokenPassword() {
        return(getValue(tokenPassword, SettingsData.SETTING_SHARED_INDEX_TOKEN_PASS));
    }

    /**
     * Retrieves the secret if we need a token to access the availability url
     * @return The secret to obtain a token
     */
    public String getTokenSecret() {
        return(getValue(tokenSecret, SettingsData.SETTING_SHARED_INDEX_TOKEN_SECRET));
    }

    /**
     * Retrieves the url that gives us the availability
     * @return The url to obtain the availability
     */
    public String getTokenUrl() {
        return(getValue(tokenUrl, SettingsData.SETTING_SHARED_INDEX_TOKEN_URL));
    }

    /**
     * Retrieves the username if we need a token to access the availability url
     * @return The username to obtain a token
     */
    public String getTokenUser() {
        return(getValue(tokenUser, SettingsData.SETTING_SHARED_INDEX_TOKEN_USER));
    }

    /**
     * Retrieves the value associated with the supplied setting
     * @param settingId The setting we want the value for
     * @return The value associated with the setting
     */
    private String getValue(String configValue, String settingId) {
		// If we have a config setting then use that, otherwise get the value from the system settings
		if ((configValue == null) || configValue.isBlank()) {
			// We do not have a config setting
			return(systemSettingsService.getSettingValue(settingId));
		} else {
			// We have a config setting
			return(configValue);
		} 
    }
}

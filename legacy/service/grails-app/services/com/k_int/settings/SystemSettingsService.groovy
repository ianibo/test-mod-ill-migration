package com.k_int.settings;

import com.k_int.grails.GrailsUtils;
import com.k_int.web.toolkit.settings.AppSetting;

import groovy.transform.CompileStatic;

@CompileStatic
public class SystemSettingsService extends BaseSettings<AppSetting> {

    public SystemSettingsService() {
        super(AppSetting);
    }

    /**
     * Ensures a setting value exists
     * @param key the key to the app setting
     * @param section the section this value belongs to
     * @param settingType the type of value this setting takes
     * @param vocabulary if it this setting is a lookup into reference data, this is the key to lookup the values (default: null)
     * @param defaultValue the default value if non has been supplied (default: null)
     * @param value the the value that it is given to start off with (default: null)
     * @return the AppSetting that exists or has been created
     */
    public AppSetting ensureSetting(String key, String section, String settingType, String vocabulary = null, String defaultValue = null, String value = null, boolean hidden = false) {
        return(super.ensureSetting(null, key, section, settingType, vocabulary, defaultValue, value, hidden));
    }

	/**
	 * Returns the value for a setting
	 * @param setting the setting you want the value for
	 * @return the value for the setting, if it is not set then the default value will be returned
	 */
	public String getSettingValue(String setting) {
        return(super.getSettingValue(null, setting));
	}

	/**
	 * Checks to see if the setting has the supplied value
	 * @param setting the setting that is to be checked
	 * @param value the value that it is compared against
	 * @return true if they match, otherwise false
	 */
	public boolean hasSettingValue(String setting, String value) {
        return(super.hasSettingValue(null, setting, value));
	}

	/**
	 * Retrieves a settings as an integer
	 * @param setting the setting to be retrieved
	 * @param defaultValue if the value is null, the default value to be returned (default: 0)
	 * @param allowNegative If the value is false and the value is less than 0 then the default value is returned (default: false)
	 * @return The determined value, either from the setting or the default value
	 */
	public int getSettingAsInt(String setting, int defaultValue = 0, boolean allowNegative = false) {
        return(super.getSettingAsInt(null, setting, defaultValue, allowNegative));
	}

    /**
     * See if the setting has the value os the supplied vocabulary and value, we do cache the values, so if they change after yjey have been cached then we will give the wrong result
     * @param setting The setting that needs to be checked
     * @param vocabulary The vocabulary that we need to look up the value for
     * @param vocabularyKey The key within the vocabulary that is to be looked up
     * @return true if the setting matches the vocabulary and key value, false otherwise
     */
    public boolean hasRefDataValue(String setting, String vocabulary, String vocabularyKey) {
        return(super.hasRefDataValue(null, setting, vocabulary, vocabularyKey));
    }

    /**
     * Looks to see if a template container is in use by a setting
     * @param templateId The id of the template
     * @return true if the template is in use otherwise false
     */
    public boolean isTemplateReferenced(String templateContainerId) {
        return(super.isTemplateReferenced(null, templateContainerId));
    }

	/**
	 * Deletes a setting with the supplied key
	 * @param key The key to the setting
	 */
	public void delete(String key) {
		// Just call the parent, with a null institution
		super.delete(null, key);
	}

    /**
     * Retrieves an instance of the settings service
     * @return An instance of the SettingsService
     */
    public static SystemSettingsService getInstance() {
        return((SystemSettingsService)GrailsUtils.getServiceBean("systemSettingsService"));
    }
}

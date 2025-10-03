package com.k_int.settings;

import com.k_int.ill.ReferenceDataService;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;
import com.k_int.web.toolkit.refdata.RefdataValue;

public abstract class BaseSettings<TDomain> implements ISettings {

    private static Map vocabularyCache = [ : ];

    ReferenceDataService referenceDataService;

    /** The domain class that defines the settings */
    private Class<TDomain> domainClass;

    /** Does the domain class have the institution field defined */
    private boolean hasInstitution;

    public BaseSettings(Class<TDomain> domainClass) {
        this.domainClass = domainClass;

        // Determine if this domain has an institution property, we only check the name ...
        hasInstitution =  domainClass.metaClass.properties.find { MetaProperty property ->
            // Return true if this property represents the institution property
            return((property.name == "institution"));
        } != null;
    }

    /**
     * Ensures a setting exists
     * @param institution the institution this setting belongs to
     * @param key the key to the setting
     * @param section the section this value belongs to
     * @param settingType the type of value this setting takes
     * @param vocabulary if it this setting is a lookup into reference data, this is the key to lookup the values (default: null)
     * @param defaultValue the default value if non has been supplied (default: null)
     * @param value the the value that it is given to start off with (default: null)
     * @return the instance of the setting that has been found or newly created
     */
    public TDomain ensureSetting(
        Institution institution,
        String key,
        String section,
        String settingType,
        String vocabulary = null,
        String defaultValue = null,
        String value = null,
        boolean hidden = false
    ) {
        // Does this setting already exist
        TDomain setting = find(institution, key);
        if (setting == null) {
            // It does not so create a new one
            setting = domainClass.getDeclaredConstructor().newInstance();

            // These values do not change as they are the key to the record
            setting.key = key;
            if (hasInstitution) {
                setting.institution = institution;
            }

            // The user could have changed the value, so we do not update it
            setting.value = value;
        }

        // These values may have been modified
        setting.section = section;
        setting.settingType = settingType;
        setting.vocab = vocabulary;
        setting.defValue = defaultValue;
        setting.hidden = hidden;

        // Now update the record
        setting.save(flush:true, failOnError:true);

        // Return the record to the caller
        return(setting);
    }

	/**
	 * Returns the value for a setting
     * @param institution the institution this setting belongs to
	 * @param settingKey the setting you want the value for
	 * @return the value for the setting, if it is not set then the default value will be returned
	 */
	public String getSettingValue(
        Institution institution,
        String settingKey
    ) {
		String result = null;

		// Look up the setting
        TDomain setting = find(institution, settingKey);
		if (setting != null) {
			result = setting.value;
			if (result == null) {
				// Take the default value
				result = setting.defValue;
			}
		}

		// Return the result
		return(result);
	}

	/**
	 * Checks to see if the setting has the supplied value
     * @param institution the institution this setting belongs to
	 * @param settingKey the setting that is to be checked
	 * @param value the value that it is compared against
	 * @return true if they match, otherwise false
	 */
	public boolean hasSettingValue(
        Institution institution,
        String settingKey,
        String value
    ) {
		boolean result = false;

		String settingValue = getSettingValue(institution, settingKey);

		if (settingValue == null) {
			// They must both be null
			result = (value == null);
		} else if (value != null) {
			// They must have the same value
			result = (settingValue == value);
		}

		// Return the result
		return(result);
	}

	/**
	 * Retrieves a settings as an integer
     * @param institution the institution this setting belongs to
	 * @param settingKey the setting to be retrieved
	 * @param defaultValue if the value is null, the default value to be returned (default: 0)
	 * @param allowNegative If the value is false and the value is less than 0 then the default value is returned (default: false)
	 * @return The determined value, either from the setting or the default value
	 */
	public int getSettingAsInt(
        Institution institution,
        String settingKey,
        int defaultValue = 0,
        boolean allowNegative = false
    ) {
		int value = defaultValue;
		String settingString = getSettingValue(institution, settingKey);
		if (settingString != null) {
			try {
				// Now turn the string into an integer
				value = settingString.toInteger();

                // do we allow negative numbers
                if (!allowNegative && (value < 0)) {
                    // The value is negative, so reset to the default
                    value = defaultValue;
                }
			} catch (Exception e) {
				log.error("Unable to convert setting " + settingKey + " with value: " + settingString + " into an integer");
			}
		}

		return(value);
	}

    /**
     * See if the setting has the value os the supplied vocabulary and value, we do cache the values, so if they change after yjey have been cached then we will give the wrong result
     * @param institution the institution this setting belongs to
     * @param settingKey The setting that needs to be checked
     * @param vocabulary The vocabulary that we need to look up the value for
     * @param vocabularyKey The key within the vocabulary that is to be looked up
     * @return true if the setting matches the vocabulary and key value, false otherwise
     */
    public boolean hasRefDataValue(
        Institution institution,
        String settingKey,
        String vocabulary,
        String vocabularyKey
    ) {
        // Generate the key for the cache
        String key = vocabulary + ':' + vocabularyKey;

        // Lookup the cache
        String keyValue = vocabularyCache[key];

        // Did we find it in the cache
        if (keyValue == null) {
            // We did not, so lookup the database for it
            RefdataValue refValue = referenceDataService.lookup(vocabulary, vocabularyKey);
            if (refValue != null) {
                keyValue = refValue.value;
                vocabularyCache[key] = keyValue;
            }
        }

        // Now return true if the setting has the value of this vocabulary item
        return(hasSettingValue(institution, settingKey, keyValue));
    }

    /**
     * Looks to see if a template container is in use by a setting
     * @param institution the institution this setting belongs to
     * @param templateId The id of the template
     * @return true if the template is in use otherwise false
     */
    public boolean isTemplateReferenced(
        Institution institution,
        String templateContainerId
    ) {
        // Check if a Template Container is in use for any Template settings
        TDomain setting;

        // Do we need to take into account the institution
        if (hasInstitution) {
            // We do
            setting = domainClass.findBySettingTypeAndValueAndInstitution(SettingsData.SETTING_TYPE_TEMPLATE, templateContainerId, institution);
        } else {
            // No we do not
            setting = domainClass.findBySettingTypeAndValue(SettingsData.SETTING_TYPE_TEMPLATE, templateContainerId);
        }

        // It is not referenced if we did not find a setting
        return(setting != null);
    }

	/**
	 * Deletes a setting with the supplied key
	 * @param institution The institution the setting belongs to
	 * @param key The key to the setting
	 */
	public void delete(Institution institution, String key) {
		if (key != null) {
			TDomain setting = find(institution, key);
			if (setting != null) {
				setting.delete(fglush: true);
			}
		}
	}

    protected TDomain find(Institution institution, String key) {
        TDomain result;
        if (hasInstitution) {
            // The domain has the institution field, so search on both
            result = domainClass.findByKeyAndInstitution(key, institution);
        } else {
            // The domain only has the key so just search by that
            result = domainClass.findByKey(key);
        }

        // Return the result to the caller
        return(result);
    }
}

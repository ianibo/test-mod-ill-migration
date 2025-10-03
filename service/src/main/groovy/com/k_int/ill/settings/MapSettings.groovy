package com.k_int.ill.settings;

import com.k_int.institution.Institution;

import groovy.transform.CompileStatic

/**
 * Provides a very basic implemention of the ISettings interface
 *
 * @author Chas
 *
 */
@CompileStatic
public class MapSettings implements ISettings {

    private Map values = [ : ];

    /**
     * Adds a key / value pair into our values
     * @param key The key that is to be set
     * @param value The value to the set the key to
     */
    public void add(String key, String value) {
        // Just set the key with the value
        values[key] = value;
    }

    @Override
    public String getSettingValue(Institution institution, String setting) {
        String result = null;
        if (setting != null) {
            result = values[setting];
        }
        return(result);
    }

    @Override
    public boolean hasSettingValue(Institution institution, String setting, String value) {
        boolean result = false;
        if (setting != null) {
            String internalValue = values[setting];
            if (internalValue == null) {
                result = (value == null);
            } else if (value != null) {
                result = internalValue.equals(value);
            }
        }

        // Return the result
        return(result);
    }

    @Override
    public int getSettingAsInt(
        Institution institution,
        String setting,
        int defaultValue = 0,
        boolean allowNegative = false
    ) {
        int result = defaultValue;
        String settingString = getSettingValue(institution, setting);
        if (settingString != null) {
            try {
                // Now turn the string into an integer
                result = settingString.toInteger();

                // do we allow negative numbers
                if (!allowNegative && (result < 0)) {
                    // The value is negative, so reset to the default
                    result = defaultValue;
                }
            } catch (Exception e) {
                // Unable to convert, so just ignore and take the default
            }
        }

        return(result);
    }
}

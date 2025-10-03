package com.k_int.ill.settings;

import com.k_int.institution.Institution;

import groovy.transform.CompileStatic;

/**
 * Provides an interface for obtaining a setting
 *
 * @author Chas
 *
 */
@CompileStatic
public interface ISettings {

    /**
     * Returns the value for the supplied setting
     * @param institution the institution the setting is for
     * @param setting the setting you want the value for
     * @return the value for the setting, if it is not set then the default value will be returned
     */
    public String getSettingValue(Institution institution, String setting);

    /**
     * Checks to see if the supplied setting has the supplied value or not
     * @param institution the institution the setting is for
     * @param setting the setting that is to be checked
     * @param value the value that it is compared against
     * @return true if they match, otherwise false
     */
    public boolean hasSettingValue(Institution institution, String setting, String value);

    /**
     * Retrieves the supplied setting as an integer
     * @param institution the institution the setting is for
     * @param setting the setting to be retrieved
     * @param defaultValue if the value is null, the default value to be returned
     * @param allowNegative If the value is false and the value is less than 0 then the default value is returned
     * @return The determined value, either from the setting or the default value
     */
    public int getSettingAsInt(Institution institution, String setting, int defaultValue, boolean allowNegative);
}

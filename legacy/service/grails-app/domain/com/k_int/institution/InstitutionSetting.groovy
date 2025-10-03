package com.k_int.institution

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * This is eseentially a copy of appsettings from web toolkit
 * I have made this local implementation so we can have settings per institution
 */
@ExcludeFromGeneratedCoverageReport
class InstitutionSetting implements MultiTenant<InstitutionSetting> {

    /** The id of the setting */
    String id;

    /** The institution this setting belongs to */
    Institution institution;

    /** The section this setting belongs to */
    String section;

    /** The settings key which is unique with the institution */
    String key;

    /** The type of setting, probably should be enum, but the current values are Password, Refdata, String and Template, which are defined in SettingsData */
    String settingType;

    /** If settings type is Refdata then this is the vocabulary that defines the valid values */
    String vocab;

    /** The default value  if the value is null */
    String defValue;

    /** The actual value for this setting */
    String value;

    /** If this value is set to true then we do not want it to be found by the filter functionality */
    Boolean hidden;

    static mapping = {
              table 'institution_setting'
                 id column: 'is_id', generator: 'uuid', length:36
            version column: 'is_version'
        institution column: 'is_institution_id'
            section column: 'is_section'
                key column: 'is_key'
        settingType column: 'is_setting_type'
              value column: 'is_value'
              vocab column: 'is_vocab'
           defValue column: 'is_default_value'
             hidden column: 'is_hidden'
    }

    static constraints = {
        institution (nullable: false, unique: 'key')
            section (nullable: false, blank: false)
                key (nullable: false, blank: false)
        settingType (nullable: false, blank: false)
              vocab (nullable: true,  blank: false)
           defValue (nullable: true,  blank: false)
              value (nullable: true,  blank: true)
             hidden (nullable: true)
    }
}


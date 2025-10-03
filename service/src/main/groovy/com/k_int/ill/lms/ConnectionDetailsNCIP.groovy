package com.k_int.ill.lms;

import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

import groovy.transform.CompileStatic;

@CompileStatic
public class ConnectionDetailsNCIP {

    public String ncipServerAddress;
    public String ncipFromAgency;
    public String ncipToAgency;
    public String ncipAppProfile;
    public String registryId;

    ConnectionDetailsNCIP(Institution institution, ISettings settings) {
        // Get hold of the basic ncip connection details
        ncipServerAddress = settings.getSettingValue(
            institution,
            SettingsData.SETTING_NCIP_SERVER_ADDRESS
        );
        ncipFromAgency = settings.getSettingValue(
            institution,
            SettingsData.SETTING_NCIP_FROM_AGENCY
        );
        ncipToAgency = settings.getSettingValue(
            institution,
            SettingsData.SETTING_NCIP_TO_AGENCY
        ) ?: ncipFromAgency;
        ncipAppProfile = settings.getSettingValue(
            institution,
            SettingsData.SETTING_NCIP_APP_PROFILE
        );
        registryId = settings.getSettingValue(
            institution,
            SettingsData.SETTING_WMS_REGISTRY_ID
        );

        // Do we have the basic configuration we can get away with
        if ((ncipServerAddress == null) ||
            (ncipToAgency == null) ||
            (ncipAppProfile == null)) {
            // Throw an exception as it hasn't been configured correctly
            throw new RuntimeException("ncip_server_address, ncip_from_agency and ncip_app_profile must be defined");
        }
    }
}

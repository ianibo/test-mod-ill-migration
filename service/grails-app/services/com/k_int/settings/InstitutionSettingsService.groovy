package com.k_int.settings;

import com.k_int.grails.GrailsUtils;
import com.k_int.institution.InstitutionSetting;

import groovy.transform.CompileStatic;

@CompileStatic
public class InstitutionSettingsService extends BaseSettings<InstitutionSetting> {

    public InstitutionSettingsService() {
        super(InstitutionSetting);
    }

    /**
     * Retrieves an instance of the settings service
     * @return An instance of the SettingsService
     */
    public static InstitutionSettingsService getInstance() {
        return((InstitutionSettingsService)GrailsUtils.getServiceBean("institutionSettingsService"));
    }
}

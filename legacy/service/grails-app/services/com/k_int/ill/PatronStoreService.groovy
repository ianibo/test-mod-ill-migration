package com.k_int.ill;

import com.k_int.ill.patronStore.PatronStoreActions;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService

import grails.core.GrailsApplication;

/**
 * Return the right PatronServiceActions for the tenant config
 *
 */
public class PatronStoreService {

  GrailsApplication grailsApplication;
  InstitutionSettingsService institutionSettingsService;

  public PatronStoreActions getPatronStoreActionsFor(String ps) {
    log.debug("PatronStoreService::getSharedIndexActionsFor(${ps})");

    PatronStoreActions result = null;
    String bean_name;

    if('FOLIO' == ps?.toUpperCase()) {
      bean_name = 'folioPatronStoreService';
    } else {
      bean_name = 'manualPatronStoreService';
    }

    try {
      result = grailsApplication.mainContext.getBean(bean_name);
    } catch(Exception e) {
      log.error("Unable to retrieve bean ${bean_name} from grails application context: ${e}");
    }

    if ( result == null && ps != 'none' ) {
      log.warn("Unable to locate PatronStoreActions for ${ps}. Did you fail to configure the app_setting \"${SettingsData.SETTING_PATRON_STORE}\". Current options are folio|none");
    }

    return result;
  }

  public PatronStoreActions getPatronStoreActions(Institution institution) {
    PatronStoreActions result = null;
    String v = institutionSettingsService.getSettingValue(
        institution,
        SettingsData.SETTING_PATRON_STORE
    );
    log.debug("Return host patron store integrations for : ${v} - query application context for bean named ${v}PatronStoreService");
    result = getPatronStoreActionsFor(v);
    return result;
  }

}

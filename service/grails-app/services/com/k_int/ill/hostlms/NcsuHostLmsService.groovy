package com.k_int.ill.hostlms;

import org.olf.rs.circ.client.CirculationClient;
import org.olf.rs.circ.client.NCIPClientWrapper;

import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

public class NcsuHostLmsService extends SymphonyHostLmsService {

  @Override
  public CirculationClient getCirculationClient(Institution institution, ISettings settings, String address) {
    String password = settings.getSettingValue(
        institution,
        SettingsData.SETTING_NCIP_FROM_AGENCY_AUTHENTICATION
    );
    // This wrapper creates the circulationClient we need
    return new NCIPClientWrapper(address,
     [
      fromAgencyAuthentication: password,
      protocol: "NCIP2"
     ]).circulationClient;
  }

  @Override
  public boolean isNCIP2() {
    return true;
  }
}

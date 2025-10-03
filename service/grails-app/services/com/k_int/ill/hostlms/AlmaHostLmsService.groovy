package com.k_int.ill.hostlms;

import org.olf.rs.circ.client.CirculationClient;
import org.olf.rs.circ.client.NCIPClientWrapper;

import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

/**
 * The interface between mod-ill and any host Library Management Systems
 *
 */
public class AlmaHostLmsService extends BaseHostLmsService {

  public CirculationClient getCirculationClient(Institution institution, ISettings settings, String address) {
    // This wrapper creates the circulationClient we need
    return new NCIPClientWrapper(address, [protocol: "NCIP2"]).circulationClient;
  }

  @Override
  public boolean isNCIP2() {
    return true;
  }

}

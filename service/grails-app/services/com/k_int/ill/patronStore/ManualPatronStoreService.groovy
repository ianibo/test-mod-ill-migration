package com.k_int.ill.patronStore;

import com.k_int.institution.Institution;

import groovy.transform.CompileStatic;

@CompileStatic
public class ManualPatronStoreService implements PatronStoreActions {
  public boolean createPatronStore(Institution institution, Map patronData) {
    return true;
  }

  public Map lookupPatronStore(Institution institution, String systemPatronId) {
    return [:];
  }

  public Map lookupOrCreatePatronStore(Institution institution, String systemPatronId, Map patronData) {
    return patronData;
  }

  public boolean updateOrCreatePatronStore(Institution institution, String systemPatronId, Map patronData) {
    return true;
  }
}
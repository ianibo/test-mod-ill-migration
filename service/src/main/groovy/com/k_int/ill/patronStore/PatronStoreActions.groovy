package com.k_int.ill.patronStore;

import com.k_int.institution.Institution;

import groovy.transform.CompileStatic;

@CompileStatic
public interface PatronStoreActions {

    /*
     * Create a backend store in whatever system we are using to hold patrons
     * to represent a single patron record
     */
    public abstract boolean createPatronStore(Institution institution, Map patronData);

    /*
     * Retrieve a map with information from the backend store for a given
     * external system identifier (if it exists)
     */
    public abstract Map lookupPatronStore(Institution institution, String systemPatronId);

    public abstract Map lookupOrCreatePatronStore(Institution institution, String systemPatronId, Map patronData);

    public abstract boolean updateOrCreatePatronStore(Institution institution, String systemPatronId, Map patronData);
}


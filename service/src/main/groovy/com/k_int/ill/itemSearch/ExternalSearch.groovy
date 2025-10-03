package com.k_int.ill.itemSearch;

import groovy.transform.CompileStatic;

@CompileStatic
public class ExternalSearch {

	// The query to be performed
	public String query;

	// The host lms type id, if the query is restricted to be run against a specific host lms type	
	public String onlyHostLmsTypeId; 
	
	// The host lms type ids that should not be searched
    public List<String> excludedHostLmsTypeIds;

	// Do we require a symbol on the directory entry to perform a search
	public boolean requiresSymbol = true;
	
    public ExternalSearch() {
    }
}

package com.k_int.ill.hostlms.z3950;

import groovy.transform.CompileStatic;

@CompileStatic
public interface Z3950HostLms {

	/**
	 * Retrieves the record syntax to use in the z3950 query
	 * @return The record syntax to request
	 */
	public String recordSyntax();

	/**
	 * Retries the override use attributes to use in the z3950 query	
	 * @return the use attributes that override the default set
	 */
	public Map overrideUseAttributes();
}

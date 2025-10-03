package com.k_int.ill.results;

import com.k_int.Country;

import groovy.transform.CompileStatic;

/**
 * Holds the result details for details required to create or edit a copyright message
 */
@CompileStatic
public class CopyrightMessageCreateEditResult {

	/** The list of countries */
	public List<Country> countries;

	public CopyrightMessageCreateEditResult(List<Country> countries) {
		this.countries = countries;
	}
}

package com.k_int.ill;

import com.k_int.GenericResult;

/**
 * Holds the outcome of fetching the copyright message for a request
 */
public class FetchPatronRequestCopyrightResult extends GenericResult {

	public String copyright;

	public FetchPatronRequestCopyrightResult() {
	}

	public FetchPatronRequestCopyrightResult(String id) {
		super(id);
	}
}

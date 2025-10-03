package com.k_int.ill.results;

import groovy.transform.CompileStatic;

/**
 * Holds the result details for details required to create or edit an ill smtp message
 */
@CompileStatic
public class TemplateContainerCreateEditResult {

	/** The list of valid tokens */
	public Map<String, List<String>> tokens;
	
    public TemplateContainerCreateEditResult(Map<String, List<String>> tokens) {
		// The valid tokens for this type of container
		this.tokens = tokens;
    }
}

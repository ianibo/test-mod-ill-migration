package com.k_int.ill.hostlms.z3950;

import com.k_int.ill.constants.Z3950;

import groovy.transform.CompileStatic;

@CompileStatic
public class BaseZ3950HostLmsService implements Z3950HostLms  {

	public String recordSyntax() {
		return(Z3950.RECORD_SYNTAX_DEFAULT);
	}

	public Map overrideUseAttributes() {
		// We have no overrides by default
		return(null);
	}
}

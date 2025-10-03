package com.k_int.ill.routing;

import groovy.transform.CompileStatic;

@CompileStatic
public class RouterInformation {
	private String name;
	private String description;

	public RouterInformation(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public String toString() {
		return("RouterInformation(" + name + ", " + description + ")");
	}
}

package com.k_int;

import groovy.util.logging.Slf4j;

@Slf4j
public class RestResult {
	boolean success;
	public int statusCode;
	public Object responseBody;

	public RestResult(Object responseBody) {
		success = true;
		this.responseBody = responseBody;
	}

	public RestResult(groovyx.net.http.HttpException e) {
		success = false;
		statusCode = e.getStatusCode();
		responseBody = e.getBody();
	}
};

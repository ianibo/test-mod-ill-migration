package com.k_int.ill.iso18626.complexTypes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestingAgencyAuthentication {

	public String accountId;
	public String securityCode;

	public RequestingAgencyAuthentication() {
	}
}

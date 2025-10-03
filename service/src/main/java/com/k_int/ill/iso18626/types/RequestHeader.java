package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.complexTypes.AgencyId;
import com.k_int.ill.iso18626.complexTypes.RequestingAgencyAuthentication;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestHeader extends Header {

	public String multipleItemRequestId;
	public RequestingAgencyAuthentication requestingAgencyAuthentication;

	// New for 2021
	public AgencyId consortialId;

	public RequestHeader() {
	}

	/**
	 * Constructor for 18626-2017
	 * @param supplyingAgencyId the supplying agency id
	 * @param requestingAgencyId the requesting agency id
	 * @param requestingAgencyRequestId the requesting agency request id
	 * @param multipleItemRequestId multiple request ids
	 * @param requestingAgencyAuthentication the authentication mechanism
	 */
	public RequestHeader(
		AgencyId supplyingAgencyId,
		AgencyId requestingAgencyId,
		String requestingAgencyRequestId,
		String multipleItemRequestId,
		RequestingAgencyAuthentication requestingAgencyAuthentication
	) {
		// Just call the 2021 constructor
		this(
			supplyingAgencyId,
			requestingAgencyId,
			null,
			requestingAgencyRequestId,
			multipleItemRequestId,
			requestingAgencyAuthentication
		);
	}
	
	/**
	 * Constructor for 18626-2021
	 * @param supplyingAgencyId the supplying agency id
	 * @param requestingAgencyId the requesting agency id
	 * @param consortialId the consortial id
	 * @param requestingAgencyRequestId the requesting agency request id
	 * @param multipleItemRequestId multiple request ids
	 * @param requestingAgencyAuthentication the authentication mechanism
	 */
	public RequestHeader(
		AgencyId supplyingAgencyId,
		AgencyId requestingAgencyId,
		AgencyId consortialId,
		String requestingAgencyRequestId,
		String multipleItemRequestId,
		RequestingAgencyAuthentication requestingAgencyAuthentication
	) {
		super(
			supplyingAgencyId,
			requestingAgencyId,
			requestingAgencyRequestId
		);
		this.consortialId = consortialId;
		this.multipleItemRequestId = multipleItemRequestId;
		this.requestingAgencyAuthentication = requestingAgencyAuthentication;
	}
}

package com.k_int.ill.patronRequest;

import com.k_int.GenericResult;
import com.k_int.ill.FetchPatronRequestCopyrightResult;
import com.k_int.ill.PatronRequest;

/**
 * Deals with all things to do with copyright associated with a patron request
 * @author Chas
 */
public class PatronRequestCopyrightService {

	static private final String ERROR_NO_COPYRIGHT = "NO_COPYRIGHT";

	/**
	 * Has the copyright been agreed for a request
	 * It has been agreed under one of the following circumstances
	 * 1. We have not been supplied a patron request
	 * 2. No copyright has been associated with the request
	 * 3. The copyright associayed with the request has been marked as sign
	 * @param patronRequest The request that we wnat to check if the copyright has been agreed
	 * @return true if the copyright has been agreed, false otherwise
	 */
	public boolean isCopyrightAgreed(PatronRequest patronRequest) {
		return((patronRequest == null) ||
			   (patronRequest.copyright == null) ||
			   (patronRequest.copyright.agreedDate != null)
			  );
	}

	/**
	 * Obtain the copyright message for the request, if there is no copyright message, the the error code will be set to NO_COPYRIGHT
	 * @param patronRequest The request the copyright message is required for
	 * @param result the result to be passed back to the caller
	 */
	public void fetchCopyrightMessage(
		PatronRequest patronRequest,
		FetchPatronRequestCopyrightResult result
	) {
		String message = null;

		// Do we have any copyright associated with the request
		if ((patronRequest == null) || (patronRequest.copyright == null)) {
			// No we do not
			result.error("No copyright associated with the request", ERROR_NO_COPYRIGHT);
		} else {
			// Set the return copyright to be the copyright message
			result.copyright = patronRequest.copyright.copyrightText;
		}
	}

	/**
	 * marks the copyright associated with the request as being agreed
	 * @param patronRequest The request the copyright message is marked as reas
	 * @param result the result to be passed back to the caller
	 */
	public void agreeCopyright(
		PatronRequest patronRequest,
		GenericResult result
	) {
		// Do we have any copyright associated with the request
		if ((patronRequest == null) || (patronRequest.copyright == null)) {
			// No we do not
			result.error("No copyright associated with the request", ERROR_NO_COPYRIGHT);
		} else {
			// They do, so set the copyright agreed date to now
			patronRequest.copyright.agreedDate = new Date();
			patronRequest.copyright.save(flush:true, failOnError:true);
		}
	}
}

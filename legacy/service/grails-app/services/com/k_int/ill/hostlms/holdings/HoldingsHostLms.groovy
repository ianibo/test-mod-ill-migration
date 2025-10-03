package com.k_int.ill.hostlms.holdings;

import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;

import groovy.transform.CompileStatic;

@CompileStatic
public interface HoldingsHostLms {

	/**
	 * Extracts the holdings from a z3950 response
	 * @param z3950Response The response from the z3950 server
	 * @param reason The reason that generated this result
	 * @param holdingLogDetails The logger for recording the result
	 * @return The holdings extracted from the z3950 response
	 */
	public List<ItemLocation> extractAvailableItemsFrom(
		Object z3950Response,
		String reason,
		IHoldingLogDetails holdingLogDetails
	);
}

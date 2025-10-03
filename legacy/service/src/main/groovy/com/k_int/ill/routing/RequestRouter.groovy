package com.k_int.ill.routing;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.institution.Institution;

import groovy.transform.CompileStatic;

/**
 * A request router does the job of finding and ranking possible suppliers for a given item.
 * different implementations can have different characteristics and different critera.
 */
@CompileStatic
public interface RequestRouter {

	/**
	 * Locate more suppliers for the request
	 * @param patronRequest the request that we want to find more suppliers for
	 * @return
	 */
    List<RankedSupplier> findMoreSuppliers(
        PatronRequest patronRequest
    );

	/**
	 * Locate more suppliers for the request, but allows the logger to be overridden 
	 * @param patronRequest the request that we want to find more suppliers for
	 * @param overrideHoldingLogDetails the logger for holding details to be used
	 * @return
	 */
    List<RankedSupplier> findMoreSuppliers(
        PatronRequest patronRequest,
		IHoldingLogDetails overrideHoldingLogDetails
    );

	/**
	 * Returns basic information about the router
	 * @return A RouterInformation object containing details about the router
	 */
    RouterInformation getRouterInfo();
}


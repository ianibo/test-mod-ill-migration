package com.k_int.ill.routing;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.ProtocolType;
import com.k_int.ill.logging.IHoldingLogDetails;

public class Sharedindexandz3950RouterService extends BaseRouterService {

	SharedindexRouterService sharedindexRouterService;
	Z3950RouterService z3950RouterService;

	public Sharedindexandz3950RouterService() {
		super('SharedIndexAndZ3950RouterService', 'Shared Index and Z3950 Routing Service', ProtocolType.Z3950_REQUESTER);
	}

	@Override	 
	public List<RankedSupplier> findMoreSuppliers(
		PatronRequest patronRequest,
		IHoldingLogDetails holdingLogDetails
	) {
		log.debug("SharedIndexAndZ3950RouterService::findMoreSuppliers");

		// We first look at the shared index		
		List<RankedSupplier> rankedSuppliers = sharedindexRouterService.findMoreSuppliers(patronRequest, holdingLogDetails);
		
		// Did we find any in the shared index
		if ((rankedSuppliers == null) || rankedSuppliers.isEmpty()) {
			// No we did not, so look externally using Z3950
			rankedSuppliers = z3950RouterService.findMoreSuppliers(patronRequest, holdingLogDetails);
		}
		return(rankedSuppliers);
	}
}

package com.k_int.ill.routing;

import com.k_int.ill.AvailabilityStatement;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestRota;
import com.k_int.ill.SharedIndexService;
import com.k_int.ill.logging.IHoldingLogDetails

public class SharedindexRouterService extends BaseRouterService {

	SharedIndexService sharedIndexService;

	public SharedindexRouterService() {
		super('SharedindexRouterService', 'Shared Index Routing Service');
	}

	@Override	 
	public List<RankedSupplier> findMoreSuppliers(
		PatronRequest patronRequest
	) {
		log.debug("SharedindexRouterService::findMoreSuppliers");
		List<AvailabilityStatement> sia = sharedIndexService.getSharedIndexActions().findAppropriateCopies(
			patronRequest.institution,
			patronRequest.getDescriptiveMetadata()
		);

		if ((sia != null) && !sia.isEmpty()) {
			// Build up te directory entries to ignore
			List<String> directoryEntriesToIgnore = new ArrayList<String>();
			patronRequest.rota.each { PatronRequestRota patronRequestRota ->
				// Why oh why is the directory id not the key to the directory entry, it is in actual fact the symbol
				directoryEntriesToIgnore.add(patronRequestRota.directoryId);
			}
	
			// Do we need to filter the results with the ones we want to exclude
			if (!directoryEntriesToIgnore.isEmpty()) {
				sia.removeIf{ String symbol -> directoryEntriesToIgnore.contains(symbol)};
			}
		}
		return(createRankedRota(sia));
	}
}

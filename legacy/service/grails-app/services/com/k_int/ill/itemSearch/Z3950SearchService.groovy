package com.k_int.ill.itemSearch;

import com.k_int.directory.DirectoryEntry;
import com.k_int.directory.ServiceAccount;
import com.k_int.ill.HostLmsService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.Z3950Service;
import com.k_int.ill.hostlms.holdings.HoldingsHostLms;
import com.k_int.ill.hostlms.z3950.Z3950HostLms;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;

public class Z3950SearchService extends SearchService {

	HostLmsService hostLmsService;
	Z3950SearchTreeService z3950SearchTreeService;
	Z3950Service z3950Service;
	
	@Override
	protected String  buildQuery(
		SearchTree searchTree,
		PatronRequest patronRequest,
		Map useOverrideAttributes
	) {
		return(z3950SearchTreeService.buildQuery(searchTree, patronRequest, useOverrideAttributes));
	}
	
	@Override
	protected ServiceAccount getServiceAccount(DirectoryEntry directoryEntry)
	{
		return(directoryEntryService.getServiceZ3950RtacAccount(directoryEntry));
	}
	
	@Override
	protected Map getOverrideUseAttributes(String hostLms) {
		return(getZ3950HostLms(hostLms).overrideUseAttributes());
	}

	@Override
	protected List<ItemLocation> performSearch(
		ServiceAccount serviceAccount,
		String query,
		int maximumHits,
		IHoldingLogDetails holdingLogDetails
	) {
		log.debug("Executing search: " + query);
		List<ItemLocation> itemLocations = null;
		
		// Can't do anything without an address for the z3950 server
		String z3950Address = serviceAccount.service.address;

		// We need to get hold of the record syn
		if (z3950Address) {
			def z3950Response = z3950Service.query(
				z3950Address,
				query,
				maximumHits,
				getHoldingsQueryRecsyn(serviceAccount.accountHolder),
				holdingLogDetails
			);

			if (z3950Response != null) {			
				// Get hold of the holdings service
				HoldingsHostLms holdingsService = getHoldingsService(serviceAccount.accountHolder); 

				// Now interpret the response
				itemLocations = holdingsService.extractAvailableItemsFrom(
					z3950Response,
					query,
					holdingLogDetails
				);
			}
		}
		return(itemLocations);
	}

	private String getHostLms(DirectoryEntry directoryEntry) {
		return(directoryEntryService.getHostLms(directoryEntry));
	}
	
	private HoldingsHostLms getHoldingsService(DirectoryEntry directoryEntry) {
		return(hostLmsService.getHoldingsHostLms(getHostLms(directoryEntry)));
	}
	
	private Z3950HostLms getZ3950HostLms(DirectoryEntry directoryEntry) {
		return(hostLmsService.getZ3950HostLms(getHostLms(directoryEntry)));
	}
	
	private Z3950HostLms getZ3950HostLms(String hostLms) {
		return(hostLmsService.getZ3950HostLms(hostLms));
	}
	
	private String getHoldingsQueryRecsyn(DirectoryEntry directoryEntry) {
		return(getZ3950HostLms(directoryEntry).recordSyntax());
	}
}

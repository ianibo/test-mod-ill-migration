package com.k_int.ill.routing;

import com.k_int.directory.DirectoryEntry;
import com.k_int.directory.Symbol;
import com.k_int.ill.AvailabilityStatement;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestRota;
import com.k_int.ill.ProtocolType;
import com.k_int.ill.itemSearch.SearchResult;
import com.k_int.ill.itemSearch.Z3950SearchService;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;

public class Z3950RouterService extends BaseRouterService {

	Z3950SearchService z3950SearchService;

	public Z3950RouterService() {
		super('Z3950RouterService', 'Z3950 Routing Service', ProtocolType.Z3950_REQUESTER);
	}

	@Override
	public List<RankedSupplier> findMoreSuppliers(
		PatronRequest patronRequest,
		IHoldingLogDetails holdingLogDetails
	) {
		log.debug("Z3950RouterService::findMoreSuppliers");
		List<RankedSupplier> rankedSuppliers = new ArrayList<RankedSupplier>();
		
		// Build up the list of directory entries to ignore		
		List<String> directoryEntriesToIgnore = new ArrayList<String>();
		patronRequest.rota.each { PatronRequestRota patronRequestRota ->
			// Why oh why is the directory id not the key to the directory entry, it is in actual fact the symbol
			Symbol responderSymbol = directoryEntryService.resolveCombinedSymbol(patronRequestRota.directoryId);
			if (responderSymbol != null) {
				directoryEntriesToIgnore.add(responderSymbol.owner.id);
			}
		}

		// Now we can locate the item		
		SearchResult searchResult = z3950SearchService.locate(
			patronRequest,
			directoryEntriesToIgnore,
			holdingLogDetails
		)
		
		// Now we need to turn the search result into availability statements
		List<AvailabilityStatement> availabilityStatements = toAvailabilityStatements(searchResult); 

		// Now convert it into a ranked rota		
		return(createRankedRota(availabilityStatements));
	}

	/**
	 * Turns a SearchResult into a list of availability statements	
	 * @param searchResult The search result that needs to be turned into availability statements
	 * @return The list of availability statements
	 */
	private List<AvailabilityStatement> toAvailabilityStatements(SearchResult searchResult) {
		List<AvailabilityStatement> availabilityStatements = new ArrayList<AvailabilityStatement>();
		
		// Only have something to do if we found something
		if ((searchResult != null) && searchResult.hasItems()) {
			// Process the found items
			searchResult.foundItems.each { Map.Entry<DirectoryEntry, List<ItemLocation>> item ->
				DirectoryEntry directoryEntry = item.getKey(); 
				// We need to get hold of the first symbol
				Symbol symbol = directoryEntry.symbols.isEmpty() ? null : directoryEntry.symbols.first();

				// Did we find a symbol
				if (symbol == null) {
					log.info("No symbol found for directory: " + directoryEntry.name);
				} else {
					String fullSymbol = symbol.authority.symbol + ":" + symbol.symbol;

					// Loop through each of the item locations					
					item.getValue().each { ItemLocation itemLocation ->
						// Create an availability statement from it
						AvailabilityStatement availabilityStatement = new AvailabilityStatement();
						availabilityStatement.symbol = fullSymbol;
						availabilityStatement.copyIdentifier = itemLocation.itemId;
						availabilityStatement.availableCopies = 1;
						availabilityStatement.totalCopies = 1;

						// Add it to the availability statements
						availabilityStatements.add(availabilityStatement);
					}
				}			
			}
		} else {
			log.info("toAvailabilityStatements no items supplied to convert to availability statements");
		}

		// Return the availability statements		
		return(availabilityStatements);
	}
}

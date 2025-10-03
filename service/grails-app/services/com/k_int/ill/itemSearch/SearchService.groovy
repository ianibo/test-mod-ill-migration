package com.k_int.ill.itemSearch;

import com.k_int.directory.DirectoryEntry;
import com.k_int.directory.DirectoryEntryService;
import com.k_int.directory.DirectoryGroup;
import com.k_int.directory.DirectoryGroupMember;
import com.k_int.directory.DirectoryGroups;
import com.k_int.directory.DirectoryGroupsMember;
import com.k_int.directory.ServiceAccount;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;

public abstract class SearchService {

	DirectoryEntryService directoryEntryService;

	/**
	 * Tries to locate the requested item using Z3950 	
	 * @param patronRequest the item that has been requested by the patron
	 * @param directoryEntriesToIgnore the ids of directory entries we want to ignore
	 * @param holdingLogDetails where to log the details about the holdings 
	 * @return A list of item locations of found and available items
	 */
	public SearchResult locate(
		PatronRequest patronRequest,
		List<String> directoryEntriesToIgnore,
		IHoldingLogDetails holdingLogDetails
	) {
		SearchResult searchResult = new SearchResult();
		
		// The directory entry for the requester can me derived from the resolvedRequester
		if (patronRequest?.resolvedRequester == null) {
			log.error("No resolved requester set on patron request with id: " + patronRequest?.id);
		} else {
			// We have a resolved requester
			DirectoryEntry directoryEntry = patronRequest.resolvedRequester.owner;

			// Get hold of search groups for the directory entry			
			SearchGroup searchGroup = directoryEntryService.getSearches(directoryEntry);

			// Cannot continue if we do not have any searches
			if ((searchGroup == null) ||
				(searchGroup.members == null) ||
				searchGroup.members.isEmpty()) {
				log.error("Failed to find any searches to perform for directory entry: " + directoryEntry.name + "/" + directoryEntry.id);
			} else {
				// Get hold of the directory groups for this directory entry
				DirectoryGroups searchDirectoryGroups = directoryEntryService.getSearchDirectoryGroups(directoryEntry);

				// Cannot continue if we did not find any directory groups
				if ((searchDirectoryGroups == null) ||
					(searchDirectoryGroups.members == null) ||
					searchDirectoryGroups.members.isEmpty()) {
					log.error("Failed to find any search directory groups for directory entry: " + directoryEntry.name + "/" + directoryEntry.id);
				} else {

					// We are in a good place we have searches and directory groups
					// So first of all we will process the directory entry groups
					for (DirectoryGroupsMember directoryGroupsMember in searchDirectoryGroups.members) {
						processDirectoryGroup(
							directoryGroupsMember.directoryGroup,
							searchGroup,
							patronRequest,
							searchResult,
							directoryEntriesToIgnore,
							holdingLogDetails
						);
					}
				}
			}
		}
		
		// return the search result to the caller
		return(searchResult);
	}

	/**
	 * Retrieves the search for that is to be performed
	 * @param searchTree the search tree that will be used to build the search
	 * @param patronRequest the patron request that will be used to feed values to the search
	 * @param useOverrideAttributes use attribute mappings to be used instead
	 * @return The search that will be performed or null if no search is to be performed
	 */
	protected abstract String buildQuery(
		SearchTree searchTree,
		PatronRequest patronRequest,
		Map useOverrideAttributes
	);

	/**
	 * Retrieve the service account that contains the details to perform the search
	 * @param directoryEntry the directory entry that we are going to search
	 * @return the service account if one exists for the directory entry otherwise null if a search does not exist
	 */
	protected abstract ServiceAccount getServiceAccount(DirectoryEntry directoryEntry);

	/**
	 * Performs a search against the service account with the supplied query  
	 * @param serviceAccount the service account to perform the search against
	 * @param query the query to be executed 
	 * @param holdingLogDetails where to log the details about the holdings 
	 * @return A list of item locations of found and available items
	 */
	protected abstract List<ItemLocation> performSearch(
		ServiceAccount serviceAccount,
		String query,
		int maximumHits,
		IHoldingLogDetails holdingLogDetails
	);

	protected Map getOverrideUseAttributes(String hostLms) {
		// By default we have no override use attributes
		return(null);	
	}
	
	private void processDirectoryGroup(
		DirectoryGroup directoryGroup,
		SearchGroup searchGroup,
		PatronRequest patronRequest,
		SearchResult searchResult,
		List<String> directoryEntriesToIgnore,
		IHoldingLogDetails holdingLogDetails
	) {
		// We now loop through the searches in rank order, until we find a potential supplier
		for (SearchGroupEntry searchEntry in searchGroup.members) {
			// We only move onto the next search, if we have not already found any item locations
			if (!searchResult.hasItems()) {
				ExternalSearch externalSearch = new ExternalSearch();
				Map useOverrideAttributes = null;
				
				// Is the search for a specific host lms
				if (searchEntry.search.hostLmsType) {

					// Obtain the use types overrides 
					useOverrideAttributes = getOverrideUseAttributes(searchEntry.search.hostLmsType.label);
					
					// Set the host type id in the external search
					externalSearch.onlyHostLmsTypeId = searchEntry.search.hostLmsType;
				}

				// Do we need to exclude any host lms types from the search
				if (searchEntry.search.excludeHostLmsTypes) {
					externalSearch.excludedHostLmsTypeIds = new ArrayList<String>();
					searchEntry.search.excludeHostLmsTypes.each { SearchExcludeHostLmsType searchExcludeHostLmsType ->
						externalSearch.excludedHostLmsTypeIds.add(searchExcludeHostLmsType.hostLmsType.id);
					}
				}

				// Determine what the query should be
				externalSearch.query = buildQuery(searchEntry.search.searchTree, patronRequest, useOverrideAttributes);
	
				// Did we determine a query
				if (externalSearch.query != null) {

					// Loop through all the entries in the directory group
					for (DirectoryGroupMember directoryGroupMember in directoryGroup.members) {

						// Process this directory entry
						processDirectoryEntry(
							directoryGroupMember.directoryEntry,
							directoryEntriesToIgnore,
							externalSearch,
							searchEntry.search.maximumHits,
							holdingLogDetails,
							searchResult
						);
					}
				}
			}
		}
	}

	/**
	 * Processes the directory entry and carries out the search if the search is applicable to this directory entry 
	 * @param directoryEntry the directory entry to be processed
	 * @param directoryEntriesToIgnore the list of directory entries to be ignored
	 * @param externalSearch the details of te search to be performed
	 * @param maximumHits the maximum number of hits we want to receive for this search
	 * @param holdingLogDetails the logging details
	 * @param searchResult the object that contains the search result
	 */
	private void processDirectoryEntry(
		DirectoryEntry directoryEntry,
		List<String> directoryEntriesToIgnore,
		ExternalSearch externalSearch,
		int maximumHits,
		IHoldingLogDetails holdingLogDetails,
		SearchResult searchResult
	) {
		// Only deal with this directory entry if we are not ignoring it
		if ((directoryEntriesToIgnore == null) ||
			!directoryEntriesToIgnore.contains(directoryEntry.id)) {

			// Is this directory entry for the correct lms host type
			if (isSearchApplicableForDirectoryEntry(externalSearch, directoryEntry)) {
				// Does this directory entry have z3950 details
				ServiceAccount serviceAccount = getServiceAccount(directoryEntry);
				if (serviceAccount == null) {
					log.error("Unable to find Z3950 RTAC service account for Directory entry: " + directoryEntry.name + " / " + directoryEntry.id);
				} else {
					// We have a service account so we can perform the search
					List<ItemLocation> foundItems = performSearch(
						serviceAccount,
						externalSearch.query,
						maximumHits,
						holdingLogDetails
					);
					
					// Add the results of this search to the search results
					searchResult.add(directoryEntry, foundItems);
				}
			}
		}
	}
		
	/**
	 * Checks to see if the directory entry is applicable for the search
	 * @param externalSearch the details of the search that is to be performed
	 * @param directoryEntry the directory entry that we are going to perform the search against
	 * @return true if the search is applicable for the directory entry otherwise false
	 */
	private boolean isSearchApplicableForDirectoryEntry(
		ExternalSearch externalSearch,
		DirectoryEntry directoryEntry
	) {
		// We default to not being applicable
		boolean isApplicable = false;

		// Is this directory entry for the correct lms host type
		if ((externalSearch.onlyHostLmsTypeId == null) ||
			(externalSearch.onlyHostLmsTypeId == directoryEntry.hostLmsType?.id)) {

			// Does this search exclude specific host lms types
			if ((externalSearch.excludedHostLmsTypeIds == null) ||
				!externalSearch.excludedHostLmsTypeIds.contains(directoryEntry.hostLmsType?.id)) {

				// Do we need to check that a symbol exists for the directory entry
				if (!externalSearch.requiresSymbol || !directoryEntry.symbols.isEmpty()) {
					// We have met the conditions for the directory entry being applicable for the search
					isApplicable = true;
				} else {
					log.info("No symbol found for directory: " + directoryEntry.name + ", so not searching");
				}
			}
		}

		// Let the user know if the directory entry is applicable for the search		
		return(isApplicable);
	}
}

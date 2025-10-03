package com.k_int.ill.itemSearch;

import com.k_int.directory.DirectoryEntry;
import com.k_int.ill.lms.ItemLocation;

import groovy.transform.CompileStatic;

@CompileStatic
public class SearchResult {

	// The items that were found for each directory entry 
	public Map<DirectoryEntry, List<ItemLocation>> foundItems = new HashMap<DirectoryEntry, List<ItemLocation>>();

    public SearchResult() {
    }

	public void add(DirectoryEntry directoryEntry, List<ItemLocation> itemLocations) {
		// Only add this directory entry if we did find some item locations
		if ((itemLocations != null) && !itemLocations.isEmpty()) {
			foundItems.put(directoryEntry, itemLocations);
		}
	}

	public boolean hasItems() {
		return(!foundItems.isEmpty());
	}
}

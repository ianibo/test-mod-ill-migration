package com.k_int.ill.itemSearch;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class SearchGroup implements MultiTenant<SearchGroup> {

    String id;
    String code;
    String description;

    static hasMany = [
        members : SearchGroupEntry,
    ];

    static mappedBy = [
         members : 'searchGroup'
    ];

    static constraints = {
               code (nullable: false, blank: false, unique: true)
        description (nullable: false, blank: false)
    }

    static mapping = {
                 id column: 'sg_id', generator: 'uuid2', length:36
            version column: 'sg_version'
               code column: 'sg_code', length:32
        description column: 'sg_description'
            members cascade: 'all-delete-orphan', sort: 'rank', order: 'asc'
    }
	
	public static SearchGroup ensure(String code, String description, List<SearchGroupEntry> entries) {
		// We create ourselves a working list as we want to modify as we process it
		List<SearchGroupEntry> workingEntries = ((entries == null) ? null : entries.collect());
		
		// Lookup to see if the code exists
		SearchGroup searchGroup = findByCode(code);

		// Did we find it
		if (searchGroup == null) {
			// No we did not, so create a new one
			searchGroup = new SearchGroup (
				code: code
			);
		} else {
			// Go through removing the items that no longer need to be there
			if (searchGroup.members.size() > 0) {
				// Process all the current records
				searchGroup.members.collect().each { SearchGroupEntry entry ->
					// now look to see if it is in the working list
					SearchGroupEntry foundEntry = workingEntries.find { SearchGroupEntry workingEntry ->
						workingEntry.search.id.equals(entry.search.id) && (workingEntry.rank == entry.rank)
					};
					if (foundEntry == null) {
						// no longer required so delete it, couldn't get removeFrom to work
						SearchGroupEntry entryToRemove = searchGroup.members.find { SearchGroupEntry member ->
							member.search.id.equals(entry.search.id) && (member.rank == entry.rank)
						};
	                    searchGroup.members.remove(entry);
	                    entry.delete(flush:true, failOnError:true);
					} else {
						// Remove it from the working entries as it is already in the database
						workingEntries.removeIf { SearchGroupEntry workingEntry ->
							workingEntry.search.id.equals(foundEntry.search.id) && (workingEntry.rank == foundEntry.rank)
						};
					}
				}
			}
		}

		// Now update the other fields in case they have changed
		searchGroup.description = description;

		// workingEntries should only have the items in that need adding
		if (workingEntries != null) {
			workingEntries.each{ SearchGroupEntry entry ->
				// Just add the entry as its not already there
				searchGroup.addToMembers(entry);
			}
		}

		// Now save the group
		searchGroup.save(flush:true, failOnError:true);

		// Return the result to the caller
		return(searchGroup);
	}
}

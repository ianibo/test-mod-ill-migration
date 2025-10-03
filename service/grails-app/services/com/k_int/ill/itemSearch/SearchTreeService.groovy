package com.k_int.ill.itemSearch;

import com.k_int.directory.DirectoryEntry;
import com.k_int.directory.DirectoryEntryService;
import com.k_int.directory.DirectoryGroups;
import com.k_int.ill.PatronRequest;

public abstract class SearchTreeService {

	/**
	 * Converts a search tree into a string format appropriate for the protocol 
	 * @param searchTree the search tree that we need to turn into a textual query
	 * @param patronRequest the request that has triggered thequery to be generated
	 * @param useOverrideAttributes use attribute mappings to be used instead
	 * @return The query that is appropriate for the derived class
	 */
	public String buildQuery(
		SearchTree searchTree,
		PatronRequest patronRequest,
		Map useOverrideAttributes
	) {
		String query = null;

		// Have we been supplied a search tree
		if (searchTree != null) {
			// Deal with the left hand side first
			String lhsExpression = processExpression(
				searchTree.lhsSearchAttribute,
				searchTree.lhsSearchTree,
				patronRequest,
				useOverrideAttributes
			);
			
			// Now process the right hand side 
			String rhsExpression = processExpression(
				searchTree.rhsSearchAttribute,
				searchTree.rhsSearchTree,
				patronRequest,
				useOverrideAttributes
			);
			
			// Do we have a left hand side expression
			if (lhsExpression == null) {
				// We do not, so just set the query to what we have on the rhs
				query = rhsExpression;
			} else {
				// Do we have a right hand side expression
				if (rhsExpression == null) {
					// We do not, so just set the query to what we have on the lhs
					query = lhsExpression;
				} else {
					// We do so we need to combine the two expressions
					query = buildSearchOperation(lhsExpression, rhsExpression, searchTree.operator?.label);
				}
			}
		}

		// return the built query to the caller		
		return(query); 
	}
	
	private String processExpression(
		SearchAttribute searchAttribute,
		SearchTree searchTree,
		PatronRequest patronRequest,
		Map useOverrideAttributes
	) {
		String query = null

		// Do we have a search attribute
		if (searchAttribute == null) {
			// No search attribute, so process the search tree
			query = buildQuery(searchTree, patronRequest, useOverrideAttributes); 
		} else {
			// now do we have a value for the request attribute
			String expressionValue = patronRequest[searchAttribute.requestAttribute.label];
			
			// Have we managed to find a value from the patron request
			if (expressionValue != null) {
				// We have, so remove witespaces from it
				expressionValue = expressionValue.trim();
				
				// Have we been left with a value
				if (!expressionValue.isEmpty() ) {
					// Now build the expression
					query = buildSearchExpression(searchAttribute, expressionValue, useOverrideAttributes);
				}
			}
		}
		
		// Return the query to the caller
		return(query);
	}

	/**
	 * Builds the expression for the search attribute
	 * @param searchAttribute the search attribute that needs turning into a query
	 * @param expressionValue the value from the request
	 * @param useOverrideAttributes use attribute mappings to be used instead
	 * @return The query that represents this attribute and value
	 */
	protected abstract String buildSearchExpression(
		SearchAttribute searchAttribute,
		String expressionValue,
		Map useOverrideAttributes
	);

	/**
	 * Builds a query by combining the 2 search expressions with the supplied operator
	 * @param lhsExpression the first search expression
	 * @param rhsExpression the second search expression
	 * @param operator the operator to use to combine the 2 search expressions
	 * @return The query that represents the two expressions
	 */
	protected abstract String buildSearchOperation(
		String lhsExpression,
		String rhsExpression,
		String operator
	);
}

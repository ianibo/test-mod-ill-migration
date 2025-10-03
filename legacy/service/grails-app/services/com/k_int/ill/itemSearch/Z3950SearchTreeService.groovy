package com.k_int.ill.itemSearch;

import com.k_int.ill.constants.Z3950;
import com.k_int.ill.referenceData.RefdataValueData;

public class Z3950SearchTreeService extends SearchTreeService{

	static private final Map operators = [
	    (RefdataValueData.SEARCH_OPERATOR_AND) : Z3950.OPERATOR_AND,
	    (RefdataValueData.SEARCH_OPERATOR_NOT) : Z3950.OPERATOR_NOT,
	    (RefdataValueData.SEARCH_OPERATOR_OR)  : Z3950.OPERATOR_OR
	]

	static private final Map useAttributes = [
	    (RefdataValueData.SEARCH_ATTRIBUTE_AUTHOR)                    : Z3950.USE_AUTHOR,
		(RefdataValueData.SEARCH_ATTRIBUTE_IDENTIFIER)                : Z3950.USE_IDENTIFIER,
		(RefdataValueData.SEARCH_ATTRIBUTE_ISBN)                      : Z3950.USE_ISBN,
		(RefdataValueData.SEARCH_ATTRIBUTE_ISSN)                      : Z3950.USE_ISSN,
		(RefdataValueData.SEARCH_ATTRIBUTE_SUPPLIER_UNIQUE_RECORD_ID) : Z3950.USE_LOCAL_NUMBER,
		(RefdataValueData.SEARCH_ATTRIBUTE_TITLE)                     : Z3950.USE_TITLE
	];
	 
	static private final Map relationAttributes = [
	    (RefdataValueData.SEARCH_RELATION_EQUAL)                 : Z3950.RELATION_EQUAL,
		(RefdataValueData.SEARCH_RELATION_GREATER_THAN)          : Z3950.RELATION_GREATER_THAN,
		(RefdataValueData.SEARCH_RELATION_GREATER_THAN_OR_EQUAL) : Z3950.RELATION_GREATER_THAN_OR_EQUAL,
		(RefdataValueData.SEARCH_RELATION_LESS_THAN)             : Z3950.RELATION_LESS_THAN,
		(RefdataValueData.SEARCH_RELATION_LESS_THAN_OR_EQUAL)    : Z3950.RELATION_LESS_THAN_OR_EQUAL,
		(RefdataValueData.SEARCH_RELATION_NOT_EQUAL)             : Z3950.RELATION_NOT_EQUAL
	];

	static private final Map positionAttributes = [
		(RefdataValueData.SEARCH_POSITION_ANY_POSITION_IN_FIELD) : Z3950.POSITION_ANY_POSITION_IN_FIELD,
		(RefdataValueData.SEARCH_POSITION_FIRST_IN_FIELD)        : Z3950.POSITION_FIRST_IN_FIELD,
		(RefdataValueData.SEARCH_POSITION_FIRST_IN_SUBFIELD)     : Z3950.POSITION_FIRST_IN_SUBFIELD
	];

	static private final Map structureAttributes = [
	    (RefdataValueData.SEARCH_STRUCTURE_DATE)      : Z3950.STRUCTURE_DATE,
		(RefdataValueData.SEARCH_STRUCTURE_KEY)       : Z3950.STRUCTURE_KEY,
		(RefdataValueData.SEARCH_STRUCTURE_PHRASE)    : Z3950.STRUCTURE_PHRASE,
		(RefdataValueData.SEARCH_STRUCTURE_WORD)      : Z3950.STRUCTURE_WORD,
		(RefdataValueData.SEARCH_STRUCTURE_WORD_LIST) : Z3950.STRUCTURE_WORD_LIST,
		(RefdataValueData.SEARCH_STRUCTURE_YEAR)      : Z3950.STRUCTURE_YEAR
	];

	static private final Map truncationAttributes = [
		(RefdataValueData.SEARCH_TRUNCATION_LEFT)           : Z3950.TRUNCATION_LEFT,
		(RefdataValueData.SEARCH_TRUNCATION_LEFT_AND_RIGHT) : Z3950.TRUNCATION_LEFT_AND_RIGHT,
		(RefdataValueData.SEARCH_TRUNCATION_RIGHT)          : Z3950.TRUNCATION_RIGHT
	];
	
	static private final Map completenessAttributes = [
		(RefdataValueData.SEARCH_COMPLETENESS_COMPLETE_FIELD)      : Z3950.COMPLETENESS_COMPLETE_FIELD,
		(RefdataValueData.SEARCH_COMPLETENESS_COMPLETE_SUBFIELD)   : Z3950.COMPLETENESS_COMPLETE_SUBFIELD,
		(RefdataValueData.SEARCH_COMPLETENESS_INCOMPLETE_SUBFIELD) : Z3950.COMPLETENESS_INCOMPLETE_SUBFIELD
	];

	/**
	 * Maps the supplied search attribute value into a z3950 form of the attribute 
	 * @param attributeMap The attribute map to use for the mapping
	 * @param searchAttributeValue The attribute value that needs to be mapped
	 * @param attributeType The type of attribute that is being mapped
	 * @param overrideAttributes use attribute mappings to be used instead
	 * @return The z3950 attribute or null if there is no mapping
	 */
	private String getQueryAttribute(
		Map attributeMap,
		String searchAttributeValue,
		int attributeType,
		Map overrideAttributes
	) {
		// Default to no mapping
		String queryAttribute = null;
		
		// Have we been supplied a search attribute value
		if (searchAttributeValue != null) {
			int attribute = - 1;

			// First look it up in the override attributes if we have been supplied any			
			if (overrideAttributes != null) {
				attribute = overrideAttributes.getOrDefault(searchAttributeValue, -1);
			}
			
			// If there wasn't one in the overrides look it in the attribute map
			if (attribute == -1) {
				attribute = attributeMap.getOrDefault(searchAttributeValue, -1);
			}

			// Did we find the attribute			
			if (attribute != -1) {
				// We did so build the query attribute
				queryAttribute = Z3950.ATTRIBUTE_PREFIX + attributeType + "=" + attribute;
			}
		}	

		// Return the built attribute to the caller
		return(queryAttribute);
	}

	/**
	 * Determines what the value for the attribute should be and appends it to the search expression
	 * @param attribute the type of z3950 attribute we are adding
	 * @param attributeMap the mapping from internal values to z3950 values
	 * @param attributeValue the internal value to be mapped
	 * @param searchExpression the search expression the the determined attribute needs to be mapped to
	 * @param overrideAttributes use attribute mappings to be used instead
	 */
	private void appendAttribute(
		int attribute,
		Map attributeMap,
		String attributeValue,
		StringBuilder searchExpression,
		Map overrideAttributes = null
	) {
		// Get hold of the query attribute
		String queryAttribute = getQueryAttribute(
			attributeMap,
			attributeValue,
			attribute,
			overrideAttributes
		);

		// if the query attribute is not null append it to the searchExpression
		if (queryAttribute != null) {
			// Add a space if the search expression already has something
			if (!searchExpression.isEmpty()) {
				searchExpression.append(" ");
			}
			
			// Now add the query attribute
			searchExpression.append(queryAttribute);
		}
	}

	@Override
	protected String buildSearchExpression(
		SearchAttribute searchAttribute,
		String expressionValue,
		Map useOverrideAttributes
	) {
		// Create ourselves a StringBuffer that will contain the search expression 
		StringBuilder searchExpression = new StringBuilder();
		
		// For each type of attribute add the appropriate attribute to the search expression
		appendAttribute(Z3950.ATTRIBUTE_USE,          useAttributes,          searchAttribute.attribute?.label, searchExpression, useOverrideAttributes);
		appendAttribute(Z3950.ATTRIBUTE_RELATION,     relationAttributes,     searchAttribute.relation?.label, searchExpression);
		appendAttribute(Z3950.ATTRIBUTE_POSITION,     positionAttributes,     searchAttribute.position?.label, searchExpression);
		appendAttribute(Z3950.ATTRIBUTE_STRUCTURE,    structureAttributes,    searchAttribute.structure?.label, searchExpression);
		appendAttribute(Z3950.ATTRIBUTE_TRUNCATION,   truncationAttributes,   searchAttribute.truncation?.label, searchExpression);
		appendAttribute(Z3950.ATTRIBUTE_COMPLETENESS, completenessAttributes, searchAttribute.completeness?.label, searchExpression);
		
		// Finally add the request value to the search term
		searchExpression.append(' "').append(expressionValue).append('"');

		// Return the search expression to the caller		
		return(searchExpression.toString());
	}

	@Override
	protected String buildSearchOperation(
		String lhsExpression,
		String rhsExpression,
		String operator
	) {
		String searchExpression = null;
		
		// Combine the expressions with the operator defaulting to AND, if we cannot determine the operator
		if (lhsExpression) {
			if (rhsExpression) {
				// We have both sides
				searchExpression = operators.getOrDefault(operator, Z3950.OPERATOR_AND) + " " + lhsExpression + " " + rhsExpression;
			} else {
				// Only have the lhs
				searchExpression = lhsExpression;
			}
		} else {
			if (rhsExpression) {
				// Only have the rhs
				searchExpression = rhsExpression;
			}
		}

		// Return the search expression to the caller		
		return(searchExpression);
	}
}

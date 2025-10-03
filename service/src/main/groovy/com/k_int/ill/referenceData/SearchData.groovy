package com.k_int.ill.referenceData;

import com.k_int.ill.PredefinedId;
import com.k_int.ill.itemSearch.Search;
import com.k_int.ill.itemSearch.SearchAttribute;
import com.k_int.ill.itemSearch.SearchExcludeHostLmsType;
import com.k_int.ill.itemSearch.SearchGroup;
import com.k_int.ill.itemSearch.SearchGroupEntry;
import com.k_int.ill.itemSearch.SearchTree;
import com.k_int.web.toolkit.refdata.RefdataValue;

import groovy.util.logging.Slf4j;

/**
 * Class that reads and creates searches, search attributes, search trees and search groups
 * @author Chas
 *
 */
@Slf4j
public class SearchData {

	private static final String NAMESPACE_SEARCH           = "search";
	private static final String NAMESPACE_SEARCH_ATTRIBUTE = "search_attribute";
	private static final String NAMESPACE_SEARCH_GROUP     = "search_group";
	private static final String NAMESPACE_SEARCH_TREE      = "search_tree";
	
    public static void loadAll() {
		(new SearchData()).load();
    }

    private void load() {
        log.info('Adding predefined search attributes to the database');
		SearchAttribute authorSearchAttribute = loadSearchAttribute(
			"author_author",
			"Attribute: Author, Request attribute: author",
			RefdataValueData.SEARCH_ATTRIBUTE_AUTHOR,
			RefdataValueData.SEARCH_ATTRIBUTE_REQUEST_AUTHOR
		);
		SearchAttribute identifierOclcNumberSearchAttribute = loadSearchAttribute(
			"identifier_oclcNumber",
			"Attribute: identifier, Request attribute: oclc number",
			RefdataValueData.SEARCH_ATTRIBUTE_IDENTIFIER,
			RefdataValueData.SEARCH_ATTRIBUTE_REQUEST_OCLC_NUMBER
		);
		SearchAttribute isbnSearchAttribute = loadSearchAttribute(
			"isbn_isbn",
			"Attribute: isbn, Request attribute: isbn",
			RefdataValueData.SEARCH_ATTRIBUTE_ISBN,
			RefdataValueData.SEARCH_ATTRIBUTE_REQUEST_ISBN
		);
		SearchAttribute issnSearchAttribute = loadSearchAttribute(
			"issn_issn",
			"Attribute: issn, Request attribute: issn",
			RefdataValueData.SEARCH_ATTRIBUTE_ISSN,
			RefdataValueData.SEARCH_ATTRIBUTE_REQUEST_ISSN
		);
		SearchAttribute titleSearchAttribute = loadSearchAttribute(
			"title_title",
			"Attribute: title, Request attribute: title",
			RefdataValueData.SEARCH_ATTRIBUTE_TITLE,
			RefdataValueData.SEARCH_ATTRIBUTE_REQUEST_TITLE
		);
		SearchAttribute localSystemIdSearchAttribute = loadSearchAttribute(
			"localNumber_supplierUniqueId",
			"Attribute: local number, Request attribute: supplier unique id",
			RefdataValueData.SEARCH_ATTRIBUTE_SUPPLIER_UNIQUE_RECORD_ID,
			RefdataValueData.SEARCH_ATTRIBUTE_REQUEST_SUPPLIER_UNIQUE_RECORD_ID
		);

		log.info('Adding predefined search trees to the database');
		SearchTree identifierOclcNumberSearchTree = loadSearchTree(
			"identifierOclcNumber",
			"Search on identifier using oclc number",
			identifierOclcNumberSearchAttribute
		);
		SearchTree isbnSearchTree = loadSearchTree(
			"isbn",
			"Search on ISBN",
			isbnSearchAttribute
		);
		SearchTree issnSearchTree = loadSearchTree(
			"issn",
			"Search on ISSN",
			issnSearchAttribute
		);
		SearchTree titleAuthorSearchTree = loadSearchTree(
			"titleAuthor",
			"Search on Title and Author",
			titleSearchAttribute,
			null,
			RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_SEARCH_OPERATOR, RefdataValueData.SEARCH_OPERATOR_AND),
			authorSearchAttribute
		);
		SearchTree localSystemNumberSearchTree = loadSearchTree(
			"localSystemNumber",
			"Search on Local system number",
			localSystemIdSearchAttribute
		);
		
		log.info('Adding predefined searches to the database');
		RefdataValue horizonHostLmsType = RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_HOST_LMS_INTEGRATION_ADAPTER, RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_HORIZON);
		RefdataValue symphonyHostLmsType = RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_HOST_LMS_INTEGRATION_ADAPTER, RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_SYMPHONY);
		
		Search localSystemNumberSearch = loadSearch(
			"localSystemNumber",
			"Search by local system number",
			localSystemNumberSearchTree,
			1
		);
		loadSearchExcludeHostLmsType(
			localSystemNumberSearch,
			horizonHostLmsType
		);
		loadSearchExcludeHostLmsType(
			localSystemNumberSearch,
			symphonyHostLmsType
		);
		Search horizonLocalSystemNumberSearch = loadSearch(
			"horizonLocalSystemNumber",
			"Search by horizon local system number",
			localSystemNumberSearchTree,
			1,
			horizonHostLmsType
		);
		Search symphonyLocalSystemNumberSearch = loadSearch(
			"symphonyLocalSystemNumber",
			"Search by symphony local system number",
			localSystemNumberSearchTree,
			1,
			symphonyHostLmsType
		);
		Search identifierOclcNumberSearch = loadSearch(
			"identifierOclc",
			"Search by isentifier using oclc number",
			identifierOclcNumberSearchTree,
			3
		);
		Search isbnSearch = loadSearch(
			"isbn",
			"Search by isbn",
			isbnSearchTree,
			3
		);
		Search issnSearch = loadSearch(
			"issn",
			"Search by issn",
			issnSearchTree,
			3
		);
		Search titleAuthorSearch = loadSearch(
			"titleAuthor",
			"Search by title and author",
			titleAuthorSearchTree,
			3
		);

		log.info('Adding predefined search groups to the database');
		SearchGroup.ensure(
			"main searches",
			"The main searches that are performed", 
			[
				new SearchGroupEntry (search: isbnSearch, rank: 1),
				new SearchGroupEntry (search: issnSearch, rank: 2),
				new SearchGroupEntry (search: identifierOclcNumberSearch, rank: 3),
				new SearchGroupEntry (search: titleAuthorSearch, rank: 4)
			]
		);		
    }

	private SearchExcludeHostLmsType loadSearchExcludeHostLmsType(
		Search search,
		RefdataValue hostLmsType
	) {
		SearchExcludeHostLmsType searchExcludeHostLmsType = SearchExcludeHostLmsType.findBySearchAndHostLmsType(search, hostLmsType);
		if (searchExcludeHostLmsType == null) {
			searchExcludeHostLmsType = new SearchExcludeHostLmsType();
			searchExcludeHostLmsType.search = search;
			searchExcludeHostLmsType.hostLmsType = hostLmsType;
			
			searchExcludeHostLmsType.save(flush:true, failOnError:true);
		}

		return(searchExcludeHostLmsType);
	}
	
	private Search loadSearch(
		String code,
		String description,
		SearchTree searchTree,
		int maximumHits,
		RefdataValue hostLmsType = null
	) {
		String id = getReferencedId(NAMESPACE_SEARCH, code);
		Search search = (id == null) ? null : Search.get(id);
		if (search == null) {
			search = new Search();
			search.code = code;
		}

	    search.description = description;
		search.searchTree = searchTree;
		search.maximumHits = maximumHits;
		search.hostLmsType = hostLmsType;

		search.save(flush:true, failOnError:true);
		ensurePredefinedIdExists(NAMESPACE_SEARCH, code, search.id);
		return(search);
	}
	
	private SearchTree loadSearchTree(
		String code,
		String description,
		SearchAttribute lhsSearchAttribute,
		SearchTree lhsSearchTree = null,
		RefdataValue operator = null,
		SearchAttribute rhsSearchAttribute = null,
		SearchTree rhsSearchTree = null
	) {
		String id = getReferencedId(NAMESPACE_SEARCH_TREE, code);
		SearchTree searchTree = (id == null) ? null : SearchTree.get(id);
		if (searchTree == null) {
			searchTree = new SearchTree();
			searchTree.code = code;
		}

		searchTree.description = description;
		searchTree.lhsSearchAttribute = lhsSearchAttribute;
		searchTree.lhsSearchTree = lhsSearchTree;
		searchTree.operator = operator;
		searchTree.rhsSearchAttribute = rhsSearchAttribute;
		searchTree.rhsSearchTree = rhsSearchTree;
	
		searchTree.save(flush:true, failOnError:true);
		ensurePredefinedIdExists(NAMESPACE_SEARCH_TREE, code, searchTree.id);
		return(searchTree);
	}
	
	private SearchAttribute loadSearchAttribute(
	    String code,
	    String description,
	    String attribute,
	    String requestAttribute,
		String completeness = null,
		String position = null,
		String relation = null,
		String structure = null,
		String truncation = null
	) {
		String id = getReferencedId(NAMESPACE_SEARCH_ATTRIBUTE, code);
		SearchAttribute searchAttribute = (id == null) ? null : SearchAttribute.get(id);
		if (searchAttribute == null) {
			searchAttribute = new SearchAttribute();
			searchAttribute.code = code;
		}
		
		searchAttribute.description = description;
    	searchAttribute.attribute = RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_SEARCH_ATTRIBUTE, attribute);
		searchAttribute.completeness = completeness ? RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_SEARCH_COMPLETENESS, completeness) : null;
		searchAttribute.position = position ? RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_SEARCH_POSITION, position) : null;
		searchAttribute.relation = relation ? RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_SEARCH_RELATION, relation) : null;
		searchAttribute.structure = structure ? RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_SEARCH_STRUCTURE, structure) : null;
		searchAttribute.truncation = truncation ? RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_SEARCH_TRUNCATION, truncation) : null;
		searchAttribute.requestAttribute = RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_SEARCH_ATTRIBUTE_REQUEST, requestAttribute);

		searchAttribute.save(flush:true, failOnError:true);
		ensurePredefinedIdExists(NAMESPACE_SEARCH_ATTRIBUTE, code, searchAttribute.id);
		return(searchAttribute);
	}
	
    private String getReferencedId(String namespace, String predefinedId) {
        return(PredefinedId.lookupReferenceId(namespace, predefinedId));
    }

	private void ensurePredefinedIdExists(String namespace, String predefinedId, String id) {	
		PredefinedId.ensureExists(namespace, predefinedId, id);
	}
}

package com.k_int.ill.protocols.illEmail;

import java.time.LocalDate

import com.k_int.directory.Address;
import com.k_int.directory.DirectoryEntry;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.RemoteAction;
import com.k_int.ill.RemoteActionData;
import com.k_int.ill.RemoteActionService;
import com.k_int.ill.statemodel.Actions;

public class IllEmailMessageTokensService {

	// The top level part of the token
	private static final String TOKEN_ACTION    = "action";
	private static final String TOKEN_PICKUP    = "pickup";
	private static final String TOKEN_REQUEST   = "request";
	private static final String TOKEN_REQUESTER = "requester";
	private static final String TOKEN_RESPONDER = "responder";
	
	// Generic ones for pickup, requester and responder
	private static final String TOKEN_CONTACT_NAME = "contactName";
	private static final String TOKEN_EMAIL        = "email";
	private static final String TOKEN_NAME         = "name";
	private static final String TOKEN_PHONE        = "phone";
  
	// The address types will be dynamically generated from the reference data
	private static final String TOKEN_ADDRESS_PREFIX  = "address_";
	
	// The tokens that apply to the action
	private static final String TOKEN_ACTION_RECEIVED         = "actionReceived";
	private static final String TOKEN_ACTION_SHIPPED          = "actionShipped";
	private static final String TOKEN_ACTION_UNABLE_TO_SUPPLY = "actionUnableToSupply";
	private static final String TOKEN_ACTION_WILL_SUPPLY      = "actionWillSupply";

	private static final List<RemoteActionData> REMOTE_ACTION_DATA = new ArrayList<RemoteActionData>() {{	
		add(new RemoteActionData(TOKEN_ACTION_RECEIVED, Actions.ACTION_REQUESTER_INFORMED_RETURNED, null, 90));
		add(new RemoteActionData(TOKEN_ACTION_SHIPPED, Actions.ACTION_REQUESTER_INFORMED_SHIPPED, null, 20));
		add(new RemoteActionData(TOKEN_ACTION_UNABLE_TO_SUPPLY, Actions.ACTION_REQUESTER_INFORMED_NOT_SUPPLY, null, 10));
		add(new RemoteActionData(TOKEN_ACTION_WILL_SUPPLY, Actions.ACTION_REQUESTER_INFORMED_WILL_SUPPLY, null, 10));
	}};

	// The tokens that apply to a request
	private static final String TOKEN_REQUEST_ART_NUM                       = "artNum";
	private static final String TOKEN_REQUEST_AUTHOR                        = "author";
	private static final String TOKEN_REQUEST_AUTHOR_OF_COMPONENT           = "authorOfComponent";
	private static final String TOKEN_REQUEST_BIBLIOGRAPHIC_RECORD_ID       = "bibliographicRecordId";
	private static final String TOKEN_REQUEST_BICI                          = "bici";
	private static final String TOKEN_REQUEST_CANCELLATION_REASON           = "cancellationReason";
	private static final String TOKEN_REQUEST_CODEN                         = "coden";
	private static final String TOKEN_REQUEST_DOI                           = "doi";
	private static final String TOKEN_REQUEST_EDITION                       = "edition";
	private static final String TOKEN_REQUEST_EISSN                         = "eissn";
	private static final String TOKEN_REQUEST_IDENTIFIER                    = "identifier";
	private static final String TOKEN_REQUEST_INFORMATION_SOURCE            = "informationsource";
	private static final String TOKEN_REQUEST_ISBN                          = "isbn";
	private static final String TOKEN_REQUEST_ISSN                          = "issn";
	private static final String TOKEN_REQUEST_ISSUE                         = "issue";
	private static final String TOKEN_REQUEST_NEEDED_BY                     = "neededBy";
	private static final String TOKEN_REQUEST_NUMBER_OF_PAGES               = "numberOfPages";
	private static final String TOKEN_REQUEST_OCLC_NUMBER                   = "oclcNumber";
	private static final String TOKEN_REQUEST_PART                          = "part";
	private static final String TOKEN_REQUEST_PATRON_NOTE                   = "patronNote";
	private static final String TOKEN_REQUEST_PLACE_OF_PUBLICATION          = "placeOfPublication";
	private static final String TOKEN_REQUEST_PUBLICATION_DATE              = "publicationDate";
	private static final String TOKEN_REQUEST_PUBLICATION_DATE_OF_COMPONENT = "publicationDateOfCompnent";
	private static final String TOKEN_REQUEST_PUBLICATION_TYPE              = "publicationType";
	private static final String TOKEN_REQUEST_PUBLISHER                     = "publisher";
	private static final String TOKEN_REQUEST_QUARTER                       = "quarter";
	private static final String TOKEN_REQUEST_SERVICE_TYPE                  = "serviceType";
	private static final String TOKEN_REQUEST_SICI                          = "sici";
	private static final String TOKEN_REQUEST_SPONSOR                       = "sponsor";
	private static final String TOKEN_REQUEST_SPONSORING_BODY               = "sponsoringBody";
	private static final String TOKEN_REQUEST_SSN                           = "ssn";
	private static final String TOKEN_REQUEST_START_PAGE                    = "startPage";
	private static final String TOKEN_REQUEST_STITLE                        = "stitle";
	private static final String TOKEN_REQUEST_SUBTITLE                      = "subtitle";
	private static final String TOKEN_REQUEST_TITLE                         = "title";
	private static final String TOKEN_REQUEST_TITLE_OF_COMPONENT            = "titleOfComponent";
	private static final String TOKEN_REQUEST_VOLUME                        = "volume";

	private static final List<String> REQUEST_TOKENS = new ArrayList<String>() {{
		add(TOKEN_REQUEST_ART_NUM);
		add(TOKEN_REQUEST_AUTHOR);
		add(TOKEN_REQUEST_AUTHOR_OF_COMPONENT);
		add(TOKEN_REQUEST_BIBLIOGRAPHIC_RECORD_ID);
		add(TOKEN_REQUEST_BICI);
		add(TOKEN_REQUEST_CANCELLATION_REASON);
		add(TOKEN_REQUEST_CODEN);
		add(TOKEN_REQUEST_DOI);
		add(TOKEN_REQUEST_EDITION);
		add(TOKEN_REQUEST_EISSN);
		add(TOKEN_REQUEST_IDENTIFIER);
		add(TOKEN_REQUEST_INFORMATION_SOURCE);
		add(TOKEN_REQUEST_ISBN);
		add(TOKEN_REQUEST_ISSN);
		add(TOKEN_REQUEST_ISSUE);
		add(TOKEN_REQUEST_NEEDED_BY);
		add(TOKEN_REQUEST_NUMBER_OF_PAGES);
		add(TOKEN_REQUEST_OCLC_NUMBER);
		add(TOKEN_REQUEST_PART);
		add(TOKEN_REQUEST_PATRON_NOTE);
		add(TOKEN_REQUEST_PLACE_OF_PUBLICATION);
		add(TOKEN_REQUEST_PUBLICATION_DATE);
		add(TOKEN_REQUEST_PUBLICATION_DATE_OF_COMPONENT);
		add(TOKEN_REQUEST_PUBLICATION_TYPE);
		add(TOKEN_REQUEST_PUBLISHER);
		add(TOKEN_REQUEST_QUARTER);
		add(TOKEN_REQUEST_SERVICE_TYPE);
		add(TOKEN_REQUEST_SICI);
		add(TOKEN_REQUEST_SPONSOR);
		add(TOKEN_REQUEST_SPONSORING_BODY);
		add(TOKEN_REQUEST_SSN);
		add(TOKEN_REQUEST_START_PAGE);
		add(TOKEN_REQUEST_STITLE);
		add(TOKEN_REQUEST_SUBTITLE);
		add(TOKEN_REQUEST_TITLE);
		add(TOKEN_REQUEST_TITLE_OF_COMPONENT);
		add(TOKEN_REQUEST_VOLUME);
	}};

	private static final String ADDRESS_SEPARATOR = ", ";

	private static final String SQL_ADDRESS_LABEL = '''
select distinct addressLabel
from Address
order by addressLabel
''';

	private static final String SQL_ADDRESS_LINES = '''
select ad.addressLabel, al.value
from Address ad, AddressLine al
where al.owner = ad.id and
	  ad.owner = :directoryEntry
order by ad.id, al.seq
''';

	RemoteActionService remoteActionService;

	/**
	 * Retrieves the tokens that can be used within an ill email template 
	 * @return The map of tokens that can can be used in an email template
 	*/
	public Map<String, List<String>> tokens() {
		Map<String, List<String>> tokens = new HashMap<String, List<String>>() ;
		tokens.put(TOKEN_ACTION, actionTokens());
		tokens.put(TOKEN_PICKUP, directoryEntryTokens());
		tokens.put(TOKEN_REQUEST, REQUEST_TOKENS);
		tokens.put(TOKEN_REQUESTER, directoryEntryTokens());
		tokens.put(TOKEN_RESPONDER, directoryEntryTokens());
		return(tokens);
	}

	/**
	 * Retrieves all the possible action tokens
	 * @return The tokens that represent actions
	 */
	private List<String> actionTokens() {
		List<String> tokens = new ArrayList<String>();
		REMOTE_ACTION_DATA.each { RemoteActionData remoteActionData ->
			tokens.add(remoteActionData.token);
		}
		return(tokens);
	}

	/**
	 * Retrieves all the tokens that are associated with a directory entry
	 * @return The tokens associated with a directory entry
	 */
	private List<String> directoryEntryTokens() {
		List<String> tokens = new ArrayList<String>();
		tokens.add(TOKEN_CONTACT_NAME);
		tokens.add(TOKEN_EMAIL);
		tokens.add(TOKEN_NAME);
		tokens.add(TOKEN_PHONE);

		// Now We need to add all the address labels
		Address.executeQuery(SQL_ADDRESS_LABEL).each { String addressType ->
			tokens.add(TOKEN_ADDRESS_PREFIX + addressType);
		}
		return(tokens);
	}

	/**
	 * Creates a map of tokens to values from the request
	 * @param patronRequest the request that we want to extract the values from
	 * @return The map of tokens to values
	 */
    public Map<String, Map<String, String>> tokenValues(PatronRequest patronRequest) {
		Map<String, Map<String, String>> tokenValues = new HashMap<String, Map<String, String>>();
		tokenValues.put(TOKEN_ACTION, actionTokenValues(patronRequest));
		tokenValues.put(TOKEN_PICKUP, directoryEntryTokenValues(patronRequest.resolvedPickupLocation));
		tokenValues.put(TOKEN_REQUEST, requestTokenValues(patronRequest));
		tokenValues.put(TOKEN_REQUESTER, directoryEntryTokenValues(patronRequest.resolvedRequester?.owner));
		tokenValues.put(TOKEN_RESPONDER, directoryEntryTokenValues(patronRequest.resolvedSupplier?.owner));
		return(tokenValues);
    }

	/**
	 * Maps the request section of the tokens to values
	 * @param patronRequest The patron request to take the values from
	 * @return The map of tokens to values for the request section
	 */
	private Map<String, String> requestTokenValues(PatronRequest patronRequest) {
		Map<String, String> tokenValues = new HashMap<String, String>();
		
		addToken(tokenValues, TOKEN_REQUEST_ART_NUM, patronRequest.artnum);
		addToken(tokenValues, TOKEN_REQUEST_AUTHOR, patronRequest.author);
		addToken(tokenValues, TOKEN_REQUEST_AUTHOR_OF_COMPONENT, patronRequest.authorOfComponent);
		addToken(tokenValues, TOKEN_REQUEST_BIBLIOGRAPHIC_RECORD_ID, patronRequest.bibliographicRecordId);
		addToken(tokenValues, TOKEN_REQUEST_BICI, patronRequest.bici);
		addToken(tokenValues, TOKEN_REQUEST_CANCELLATION_REASON, patronRequest.cancellationReason?.label);
		addToken(tokenValues, TOKEN_REQUEST_CODEN, patronRequest.coden);
		addToken(tokenValues, TOKEN_REQUEST_DOI, patronRequest.doi);
		addToken(tokenValues, TOKEN_REQUEST_EDITION, patronRequest.edition);
		addToken(tokenValues, TOKEN_REQUEST_EISSN, patronRequest.eissn);
		addToken(tokenValues, TOKEN_REQUEST_IDENTIFIER, patronRequest.hrid);
		addToken(tokenValues, TOKEN_REQUEST_INFORMATION_SOURCE, patronRequest.informationSource);
		addToken(tokenValues, TOKEN_REQUEST_ISBN, patronRequest.isbn);
		addToken(tokenValues, TOKEN_REQUEST_ISSN, patronRequest.issn);
		addToken(tokenValues, TOKEN_REQUEST_ISSUE, patronRequest.issue);
		addTokenDate(tokenValues, TOKEN_REQUEST_NEEDED_BY, patronRequest.neededBy);
		addToken(tokenValues, TOKEN_REQUEST_NUMBER_OF_PAGES, patronRequest.numberOfPages);
		addToken(tokenValues, TOKEN_REQUEST_OCLC_NUMBER, patronRequest.oclcNumber);
		addToken(tokenValues, TOKEN_REQUEST_PART, patronRequest.part);
		addToken(tokenValues, TOKEN_REQUEST_PATRON_NOTE, patronRequest.patronNote);
		addToken(tokenValues, TOKEN_REQUEST_PLACE_OF_PUBLICATION, patronRequest.placeOfPublication);
		addToken(tokenValues, TOKEN_REQUEST_PUBLICATION_DATE, patronRequest.publicationDate);
		addToken(tokenValues, TOKEN_REQUEST_PUBLICATION_DATE_OF_COMPONENT, patronRequest.publicationDateOfComponent);
		addToken(tokenValues, TOKEN_REQUEST_PUBLICATION_TYPE, patronRequest.publicationType?.label);
		addToken(tokenValues, TOKEN_REQUEST_PUBLISHER, patronRequest.publisher);
		addToken(tokenValues, TOKEN_REQUEST_QUARTER, patronRequest.quarter);
		addToken(tokenValues, TOKEN_REQUEST_SERVICE_TYPE, patronRequest.serviceType?.label);
		addToken(tokenValues, TOKEN_REQUEST_SICI, patronRequest.sici);
		addToken(tokenValues, TOKEN_REQUEST_SPONSOR, patronRequest.sponsor);
		addToken(tokenValues, TOKEN_REQUEST_SPONSORING_BODY, patronRequest.sponsoringBody);
		addToken(tokenValues, TOKEN_REQUEST_SSN, patronRequest.ssn);
		addToken(tokenValues, TOKEN_REQUEST_START_PAGE, patronRequest.startPage);
		addToken(tokenValues, TOKEN_REQUEST_STITLE, patronRequest.stitle);
		addToken(tokenValues, TOKEN_REQUEST_SUBTITLE, patronRequest.subtitle);
		addToken(tokenValues, TOKEN_REQUEST_TITLE, patronRequest.title);
		addToken(tokenValues, TOKEN_REQUEST_TITLE_OF_COMPONENT, patronRequest.titleOfComponent);
		addToken(tokenValues, TOKEN_REQUEST_VOLUME, patronRequest.volume);
			
		return(tokenValues);
	}

	/**
	 * Maps the directory to token values
	 * @param directoryEntry
	 * @return
	 */
	private Map<String, String> directoryEntryTokenValues(DirectoryEntry directoryEntry) {
		Map<String, String> tokenValues = new HashMap<String, String>();
		if (directoryEntry != null) {
			addToken(tokenValues, TOKEN_CONTACT_NAME, directoryEntry.contactName);
			addToken(tokenValues, TOKEN_EMAIL, directoryEntry.emailAddress);
			addToken(tokenValues, TOKEN_NAME, directoryEntry.name);
			addToken(tokenValues, TOKEN_PHONE, directoryEntry.phoneNumber);
	
			// Now We need to add all the addresses
			Map params = [ "directoryEntry" : directoryEntry ];
			String label = null;
			StringBuilder fullAddress = new StringBuilder(); 
			Address.executeQuery(SQL_ADDRESS_LINES, params).each { Object[] addressDetail ->
				// Rip out the fields from the list, so it is more meaningful
				String addressLabel = addressDetail[0];
				String addressLine = ((String)addressDetail[1]).trim();

				// Not interested if the address line is empty
				if (!addressLine.isEmpty()) {
					// Is it the first time through
					if (label == null) {
						// First time through
						label = addressLabel;
					} else {
						// Is this for the current address or for a new address
						if (label == addressLabel) {
							if (fullAddress.length() > 0) {
								// Add a separator
								fullAddress.append(ADDRESS_SEPARATOR);
							}
						} else {
							// We have come to the end of the current address
							if (fullAddress.length() > 0) {
								// We have come to the end of this address, so set the token and reset the address
								addToken(tokenValues, TOKEN_ADDRESS_PREFIX + label, fullAddress.toString());
								fullAddress.setLength(0);
							}
							
							// Move onto the new label
							label = addressLabel;
						}
					}
	
					// Just append this line of the address
					fullAddress.append(addressLine);
				}
			}

			// Do we have to take care of the last label
			if (fullAddress.length() > 0) {
				addToken(tokenValues, TOKEN_ADDRESS_PREFIX + label, fullAddress.toString());
			}
		}
		
		// Finally return the token values
		return(tokenValues);
	}

	/**
	 * Maps the action section of the tokens to values
	 * @param patronRequest The patron request to generate the remote actions from
	 * @return The map of tokens to values for the action section
	 */
	private Map<String, String> actionTokenValues(PatronRequest patronRequest) {
		Map<String, String> tokenValues = new HashMap<String, String>();
		REMOTE_ACTION_DATA.each { RemoteActionData remoteActionData ->
			// We need to create the potential remote action
			RemoteAction remoteAction = remoteActionService.create(
				patronRequest,
				remoteActionData.action,
				remoteActionData.parameters,
				remoteActionData.expiryDays
			);
				
			// Did we successfully create a remote action
			String url = null
			if (remoteAction != null) {
				// Obtain the url for this action
				url = remoteActionService.getUrl(remoteAction);
			}

			// Add the url to the token map			
			addToken(tokenValues, remoteActionData.token, url);
		}

		// Return the values to the caller
		return(tokenValues);
	}

	/**
	 * Adds a string value token to the map
	 * @param tokens The map of tokens that this token is to be added to
	 * @param token The token to be added
	 * @param value The value that is to be associated with the token
	 */
	private void addToken(Map<String, String> tokens, String token, String value) {
		String tokenValue = value == null ? "" : value.trim();
		tokens.put(token, tokenValue);
	}

	/**
	 * Adds a local date value token to the map by formatting it to a string first
	 * @param tokens The map of tokens that this token is to be added to
	 * @param token The token to be added
	 * @param date The date to be associated with the token
	 */
	private void addTokenDate(Map<String, String> tokens, String token, LocalDate date) {
		String formatted = null;
		if (date != null) {
			// Decide how we are going to format the date
			formatted = date.toString();
		}

		// Now it has been formatted pass a string, add the token
		addToken(tokens, token, formatted);
	}
}

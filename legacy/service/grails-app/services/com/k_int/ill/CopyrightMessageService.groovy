package com.k_int.ill;

import com.k_int.Country;
import com.k_int.ill.results.CopyrightMessageCreateEditResult;

/**
 * This service handles everything to do with copyright messages
 */
public class CopyrightMessageService {

	/**
	 * Ensures a copyright message exists it does not update it if it already exists
	 * @param code the ISO18626 code to be used for this copyright
	 * @param description Te description for this copyright
	 * @param country the country this copyright is associated with
	 * @param message the message that will be presented to the user
	 * @return A CopyrightMessage object or null if it already exists
	 */
	public CopyrightMessage ensure(
		String code,
		String description,
		String country,
		String message
	) {
		CopyrightMessage copyrightMessage = null;
		if (code) {
			// Good start in that we have a code, can we find the CopyrightMessage record
			copyrightMessage = CopyrightMessage.findByCode(code);
			if (copyrightMessage == null)  {
				// If dosn't already exist so, if we have been supplied the data create the record
				if (description && country && message) {
					log.info("Creating new copyright message " + code);

					// All the fields have been specified, so we will create a new record
					copyrightMessage = new CopyrightMessage();
					copyrightMessage.code = code;
					copyrightMessage.description = description;
					copyrightMessage.country = country;
					copyrightMessage.message = message;

					// Save it
					copyrightMessage.save(flush:true, failOnError:true);
				}
			}
		}

		// Return the result to the caller
		return(copyrightMessage);
	}

	/**
	 * Obtains the details required to create or edit a copyright message 
	 * @return a CopyrightMessageCreateEditResult object containing the details required
	 */
	public CopyrightMessageCreateEditResult detailsForCreateEdit() {
		return(new CopyrightMessageCreateEditResult(Country.getAllCountries()));
	}

	/**
	 * Fetches all the copyright messages that are selectable
	 * @return A list of the selectable copyright messages
	 */
	public List<CopyrightMessage> getSelectable() {
		return(CopyrightMessage.findAllByHide(false));
	}
}

package com.k_int.ill;

import com.k_int.ill.constants.Category;
import com.k_int.web.toolkit.refdata.RefdataCategory;
import com.k_int.web.toolkit.refdata.RefdataValue;

/**
 * Useful methods for accessing RefDataValue 
 *
 */
public class RefdataValueService {
	/**
	 * Looks to see if the supplied value is a valid delivery method and if so returns the RefdataValue for it
	 * @param value The delivery method to lookup
	 * @return The RefdataValue that represents the publication type being looked up, which may be null if it is not found
	 */
	public RefdataValue lookupDeliveryMethod(String value) {
		return(lookup(Category.DELIVERY_METHOD, value));
	}

	/**
	 * Looks to see if the supplied value is a valid publication type and if so returns the RefdataValue for it
	 * @param value The publication type to lookup
	 * @return The RefdataValue that represents the publication type being looked up, which may be null if it is not found
	 */
	public RefdataValue lookupPublicationType(String value) {
		return(lookup(Category.PUBLICATION_TYPE, value));
	}

	/**
	 * Looks to see if the supplied value is a valid service type and if so returns the RefdataValue for it
	 * @param value The service type to lookup
	 * @return The RefdataValue that represents the service type being looked up, which may be null if it is not found
	 */
	public RefdataValue lookupServiceType(String value) {
		return(lookup(Category.SERVICE_TYPE, value));
	}

	/**
	 * Fetches all the service types
	 * @return A list of the service types
	 */
	public List<RefdataValue> getServiceTypes() {
		return(getAll(Category.SERVICE_TYPE));
	}

	/**
	 * Fetches all the reference data for the specified category
	 * @param category The category the reference data is required for
	 * @return A list of RefdataValue records if the category is valid otherwise null
	 */
	public List<RefdataValue> getAll(String category) {
		List<RefdataValue> refdataValues = null;
		RefdataCategory refdataCategory = getCategory(category);
		if (refdataCategory != null) {
			refdataValues = RefdataValue.findAllByOwner(refdataCategory);
		}
		return(refdataValues);
	}

	/**
	 * Looks to see if the supplied value is a valid value for the supplied category type and if so returns the RefdataValue for it
	 * @param categoryTpe The reference type to lookup
	 * @param value The value to lookup
	 * @return The RefdataValue that represents the value being looked up, which may be null if it is not found
	 */
	public RefdataValue lookup(String categoryName, String value) {
		RefdataValue reference = null;

		// Cannot do anything without a  value
		if (value != null) {
			// First of all, look for the category
			RefdataCategory category = getCategory(categoryName);

			// Did we find a category
			if (category != null) {
				// Good start we have a category, now normalise the value
				final String normalisedValue = RefdataValue.normValue(value);

				// We can now lookup the refereence object for this category
				reference = RefdataValue.findByOwnerAndValue(category, normalisedValue);
			}
		}

		// Return the found reference to the caller
		return(reference);
	}

	/**
	 * Fetches the reference data category
	 * @param category The category name
	 * @return The found category otherwise null
	 */
	public RefdataCategory getCategory(String category) {
		RefdataCategory refdataCategory = null;
		
		// Cannot do anything without a category
		if (category != null) {
			// First of all, look for the category
			refdataCategory = RefdataCategory.findByDesc(category);
		}
		return(refdataCategory);
	}
}

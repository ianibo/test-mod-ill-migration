package com.k_int;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.jknack.handlebars.internal.lang3.StringUtils;

public class Country {

	static private final List<Country> allCountries = new ArrayList<Country>();
	static {
		// Loop through the list of locales, adding them to allCountries		
		for (Locale locale : Locale.getAvailableLocales()) {
			String code = locale.getCountry();
			String displayValue = locale.getDisplayCountry();

			// We want to ignore anything that is null or blank
			if (!StringUtils.isAllBlank(code) && !StringUtils.isAllBlank(displayValue)) {
				// We only want it appearing once in our list of countries
				Country country = new Country(code, displayValue);
				if (!allCountries.contains(country)) {
					// We should have a way of filtering this list, so they do not get offered all countries, as that seems a bit overkill
					allCountries.add(country);
				}
			}
		}

		// Sort them by the display value
		allCountries.sort((Country country1, Country country2) -> { return(country1.displayValue.compareTo(country2.displayValue)); } );
	}
	
	public String code;
	public String displayValue;

	public Country(String code, String displayValue) {
		this.code = code;
		this.displayValue = displayValue;
	}

	public boolean equals(Object other) {
		boolean matches = false;
		if ((other != null) && (other instanceof Country) && (code != null)) {
			matches = code.equals(((Country)other).code);
		}
		return(matches);
	}

	public String toString() {
		return("Code: " + code + ", Display value: " + displayValue);
	}
	
	static public List<Country> getAllCountries() {
		return(allCountries);
	}
}

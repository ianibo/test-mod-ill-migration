package com.k_int.ill.referenceData;

import com.k_int.directory.NamingAuthority;

import groovy.util.logging.Slf4j;

@Slf4j
public class NamingAuthorityData {

	/**
	 * Ensures that the naming authority exists in the database
	 * @param symbol The symbol that represents this naming authority
	 * @return the found or created naming authority
	 */
	public NamingAuthority ensureNamingAuthority(String symbol) {
		NamingAuthority result = NamingAuthority.findBySymbol(symbol);
		if (result == null) {
			result = new NamingAuthority(symbol: symbol);
			result.save(flush:true, failOnError:true);
		}
		return result;
	}

	/**
	 * Loads the naming authorities into the database
	 */
	public void load() {
		try {
			log.info("Adding naming authorities to the database");

			// ensureNamingAuthority("CARDINAL");
			// ensureNamingAuthority("RESHARE");
			ensureNamingAuthority("EXL");
			ensureNamingAuthority("ILL");
			ensureNamingAuthority("ISIL");
            ensureNamingAuthority("MOBIUS");
			ensureNamingAuthority("OCLC");
			ensureNamingAuthority("PALCI");
			ensureNamingAuthority("OPENRS");
			ensureNamingAuthority("ORSTEST");

		} catch ( Exception e ) {
			log.error("Exception thrown while loading naming authorities", e);
		}
	}

	public static void loadAll() {
		(new NamingAuthorityData()).load();
	}
}

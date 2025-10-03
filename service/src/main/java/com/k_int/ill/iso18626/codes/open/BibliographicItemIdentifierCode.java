package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class BibliographicItemIdentifierCode extends Code {
	// The initial valid bibliographic item identifier codes
	public static final String ISBN = "ISBN";
	public static final String ISMN = "ISMN";
	public static final String ISSN = "ISSN";

	static {
		add(ISBN);
		add(ISMN);
		add(ISSN);
	}

	public BibliographicItemIdentifierCode() {
	}

	public BibliographicItemIdentifierCode(String bibliographicItemIdentifierCode) {
		super(bibliographicItemIdentifierCode);
	}
}

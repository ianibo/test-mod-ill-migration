package com.k_int.ill.iso18626.complexTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.open.BibliographicItemIdentifierCode;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BibliographicItemId {

	public BibliographicItemIdentifierCode bibliographicItemIdentifierCode;
	public String bibliographicItemIdentifier;

	public BibliographicItemId() {
	}

	public BibliographicItemId(String bibliographicItemIdentifierCode, String bibliographicItemIdentifier) {
		// Allow for a null code bing supplied
		if (bibliographicItemIdentifierCode != null) {
			this.bibliographicItemIdentifierCode = new BibliographicItemIdentifierCode(bibliographicItemIdentifierCode);
		}
		this.bibliographicItemIdentifier = bibliographicItemIdentifier;
	}
}

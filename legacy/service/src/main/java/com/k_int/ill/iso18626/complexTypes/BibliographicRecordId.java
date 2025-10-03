package com.k_int.ill.iso18626.complexTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.open.BibliographicRecordIdentifierCode;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BibliographicRecordId {

	public BibliographicRecordIdentifierCode bibliographicRecordIdentifierCode;
	public String bibliographicRecordIdentifier;

	public BibliographicRecordId() {
	}

	public BibliographicRecordId(
		String bibliographicRecordIdentifierCode,
		String bibliographicRecordIdentifier
	) {
		// Allow for a null code bing supplied
		if (bibliographicRecordIdentifierCode != null) {
			this.bibliographicRecordIdentifierCode = new BibliographicRecordIdentifierCode(bibliographicRecordIdentifierCode);
		}
		this.bibliographicRecordIdentifier = bibliographicRecordIdentifier;
	}
}

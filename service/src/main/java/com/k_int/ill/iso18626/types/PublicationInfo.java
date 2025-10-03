package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.open.PublicationType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PublicationInfo {

	public String publisher;
	public PublicationType publicationType;
	public String publicationDate;
	public String placeOfPublication;

	// New for 2021
	public String publisherId;

	public PublicationInfo() {
	}

	public PublicationInfo(
		String publisher,
		String publicationType,
		String publicationDate,
		String placeOfPublication
	) {
		// Constructor for ISO-18626 version 2017
		this(
			publisher,
			null,
			publicationType,
			publicationDate,
			placeOfPublication
		);
	}

	public PublicationInfo(
		String publisher,
		String publisherId,
		String publicationType,
		String publicationDate,
		String placeOfPublication
	) {
		// Constructor for ISO-18626 version 2021
		this.publisher = publisher;
		this.publisherId = publisherId;
		if (publicationType != null) {
			this.publicationType = new PublicationType(publicationType);
		}
		this.publicationDate = publicationDate;
		this.placeOfPublication = placeOfPublication;
	}
}

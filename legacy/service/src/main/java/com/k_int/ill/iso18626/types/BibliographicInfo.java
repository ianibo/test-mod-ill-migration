package com.k_int.ill.iso18626.types;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.k_int.ill.iso18626.complexTypes.BibliographicItemId;
import com.k_int.ill.iso18626.complexTypes.BibliographicRecordId;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BibliographicInfo {

	public String supplierUniqueRecordId;
	public String title;
	public String author;
	public String subtitle;
	public String seriesTitle;
	public String edition;
	public String titleOfComponent;
	public String authorOfComponent;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<String> volume;
	public String issue;
	public String pagesRequested;
	public Integer estimatedNoPages;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<BibliographicItemId> bibliographicItemId;
	public String sponsor;
	public String informationSource;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<BibliographicRecordId> bibliographicRecordId;

	// New for 2021
	public String authorId;

	public BibliographicInfo() {
	}

	public BibliographicInfo(
		String supplierUniqueRecordId,
		String title,
		String author,
		String subtitle,
		String seriesTitle,
		String edition,
		String titleOfComponent,
		String authorOfComponent,
		String volume,
		String issue,
		String pagesRequested,
		Integer estimatedNoPages,
		String sponsor,
		String informationSource
	) {
		// Constructor for ISO-18626 version 2021
		this(
			supplierUniqueRecordId,
			title,
			author,
			null,
			subtitle,
			seriesTitle,
			edition,
			titleOfComponent,
			authorOfComponent,
			volume,
			issue,
			pagesRequested,
			estimatedNoPages,
			sponsor,
			informationSource
		);
	}

	public BibliographicInfo(
		String supplierUniqueRecordId,
		String title,
		String author,
		String authorId,
		String subtitle,
		String seriesTitle,
		String edition,
		String titleOfComponent,
		String authorOfComponent,
		String volume,
		String issue,
		String pagesRequested,
		Integer estimatedNoPages,
		String sponsor,
		String informationSource
	) {
		// Constructor for ISO-18626 version 2021
		this.supplierUniqueRecordId = supplierUniqueRecordId;
		this.title = title;
		this.author = author;
		this.authorId = authorId;
		this.subtitle = subtitle;
		this.seriesTitle = seriesTitle;
		this.edition = edition;
		this.titleOfComponent = titleOfComponent;
		this.authorOfComponent = authorOfComponent;
		this.issue = issue;
		this.pagesRequested = pagesRequested;
		this.estimatedNoPages = estimatedNoPages;
		this.sponsor = sponsor;
		this.informationSource = informationSource;
		addVolume(volume);
	}

	public void addVolume(String volume) {
		// Only add it if it is not null or blank
		if ((volume != null) && !volume.isBlank()) {
			if (this.volume == null) {
				this.volume = new ArrayList<String>();
			}
			this.volume.add(volume);
		}
	}

	public void addBibliographicItemIdentifier(
		String bibliographicItemIdentifierCode,
		String value
	) {
		if (value != null) {
			if (bibliographicItemId == null) {
				bibliographicItemId = new ArrayList<BibliographicItemId>();
			}
			bibliographicItemId.add(new BibliographicItemId(bibliographicItemIdentifierCode, value));
		}
	}

	public void addBibliographicRecordIdentifier(
		String bibliographicRecordIdentifierCode,
		String value
	) {
		if (value != null) {
			if (bibliographicRecordId == null) {
				bibliographicRecordId = new ArrayList<BibliographicRecordId>();
			}
			bibliographicRecordId.add(new BibliographicRecordId(bibliographicRecordIdentifierCode, value));
		}
	}
}

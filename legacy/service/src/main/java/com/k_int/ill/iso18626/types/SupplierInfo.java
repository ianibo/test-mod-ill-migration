package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.complexTypes.AgencyId;
import com.k_int.ill.iso18626.complexTypes.BibliographicRecordId;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SupplierInfo {

	public Integer sortOrder;
	public AgencyId supplierCode;
	public String supplierDescription;
	public BibliographicRecordId bibliographicRecordId;
	public String callNumber;
	public String summaryHoldings;
	public String availabilityNote;

	public SupplierInfo() {
	}

	public SupplierInfo(
		Integer sortOrder,
		AgencyId supplierCode,
		String supplierDescription,
		BibliographicRecordId bibliographicRecordId,
		String callNumber,
		String summaryHoldings,
		String availabilityNote
	) {
		this.sortOrder = sortOrder;
		this.supplierCode = supplierCode;
		this.supplierDescription = supplierDescription;
		this.bibliographicRecordId = bibliographicRecordId;
		this.callNumber = callNumber;
		this.summaryHoldings = summaryHoldings;
		this.availabilityNote = availabilityNote;
	}

	public boolean isSet() {
		return(
			(sortOrder != null) ||
			(supplierCode != null) ||
			(supplierDescription != null) ||
			(bibliographicRecordId != null) ||
			(callNumber != null) ||
			(summaryHoldings != null) ||
			(availabilityNote != null)
		);
	}
}

package com.k_int.ill.iso18626.types;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.k_int.ill.iso18626.codes.closed.PreferredEdition;
import com.k_int.ill.iso18626.codes.closed.RequestSubType;
import com.k_int.ill.iso18626.codes.closed.RequestType;
import com.k_int.ill.iso18626.codes.closed.ServiceType;
import com.k_int.ill.iso18626.codes.closed.YesNo;
import com.k_int.ill.iso18626.codes.open.CopyrightCompliance;
import com.k_int.ill.iso18626.codes.open.ItemFormat;
import com.k_int.ill.iso18626.codes.open.LoanCondition;
import com.k_int.ill.iso18626.codes.open.PreferredFormat;
import com.k_int.ill.iso18626.codes.open.ServiceLevel;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceInfo {

	public RequestType requestType;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<RequestSubType> requestSubType;
	public String requestingAgencyPreviousRequestId;
	public ServiceType serviceType;
	public ServiceLevel serviceLevel;
	public String needBeforeDate;
	public CopyrightCompliance copyrightCompliance;
	public YesNo anyEdition;
	public String startDate;
	public String endDate;
	public String note;

	// Redundant for 2021
	public PreferredFormat preferredFormat;

	// New for 2021
	public ItemFormat itemFormat;
	public PreferredEdition preferredEdition;
	public LoanCondition loanCondition;

	public ServiceInfo() {
	}

	public ServiceInfo(
		String requestTypeCode,
		String requestingAgencyPreviousRequestId,
		String serviceTypeCode,
		String serviceLevelCode,
		String preferredFormatCode,
		String needBeforeDate,
		String copyrightComplianceCode,
		boolean anyEdition,
		String startDate,
		String endDate,
		String note
	) {
		// Constructor for ISO-18626 version 2017
		// Call the 2021 constructor
		this(
			requestTypeCode,
			requestingAgencyPreviousRequestId,
			serviceTypeCode,
			serviceLevelCode,
			null,
			needBeforeDate,
			copyrightComplianceCode,
			anyEdition,
			null,
			null,
			startDate,
			endDate,
			note
		);

		// Assign the fields that are specific to 2017
		if (preferredFormatCode != null) {
			this.preferredFormat = new PreferredFormat(preferredFormatCode);
		}
	}

	public ServiceInfo(
		String requestTypeCode,
		String requestingAgencyPreviousRequestId,
		String serviceTypeCode,
		String serviceLevelCode,
		String itemFormatCode,
		String needBeforeDate,
		String copyrightComplianceCode,
		boolean anyEdition,
		String preferredEditionCode,
		String loanConditionCode,
		String startDate,
		String endDate,
		String note
	) {
		// Constructor for ISO-18626 version 2021
		if (requestTypeCode != null) {
			this.requestType = new RequestType(requestTypeCode);
		}
		this.requestingAgencyPreviousRequestId = requestingAgencyPreviousRequestId;
		if (serviceTypeCode != null) {
			this.serviceType = new ServiceType(serviceTypeCode);
		}
		if (serviceLevelCode != null) {
			this.serviceLevel = new ServiceLevel(serviceLevelCode);
		}
		if (itemFormatCode != null) {
			this.itemFormat = new ItemFormat(itemFormatCode);
		}
		this.needBeforeDate = needBeforeDate;
		if (copyrightComplianceCode != null) {
			this.copyrightCompliance = new CopyrightCompliance(copyrightComplianceCode);
		}
		this.anyEdition = new YesNo(anyEdition ? YesNo.YES : YesNo.NO);
		if (preferredEditionCode != null) {
			this.preferredEdition = new PreferredEdition(preferredEditionCode);
		}
		if (copyrightComplianceCode != null) {
			this.loanCondition = new LoanCondition(loanConditionCode);
		}
		this.startDate = startDate;
		this.endDate = endDate;
		this.note = note;
	}

	public void addRequestSubType(String requestSubTypeCode) {
		if (requestSubTypeCode != null) {
			if (requestSubType == null) {
				requestSubType = new ArrayList<RequestSubType>();
			}
			requestSubType.add(new RequestSubType(requestSubTypeCode));
		}
	}
}

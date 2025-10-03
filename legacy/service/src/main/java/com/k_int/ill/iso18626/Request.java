package com.k_int.ill.iso18626;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.k_int.ill.iso18626.types.BibliographicInfo;
import com.k_int.ill.iso18626.types.BillingInfo;
import com.k_int.ill.iso18626.types.PatronInfo;
import com.k_int.ill.iso18626.types.PublicationInfo;
import com.k_int.ill.iso18626.types.RequestHeader;
import com.k_int.ill.iso18626.types.RequestedDeliveryInfo;
import com.k_int.ill.iso18626.types.RequestingAgencyInfo;
import com.k_int.ill.iso18626.types.ServiceInfo;
import com.k_int.ill.iso18626.types.SupplierInfo;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request {

	public RequestHeader header;
	public BibliographicInfo bibliographicInfo;
	public PublicationInfo publicationInfo;
	public ServiceInfo serviceInfo;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<SupplierInfo> supplierInfo;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<RequestedDeliveryInfo> requestedDeliveryInfo;
	public RequestingAgencyInfo requestingAgencyInfo;
	public PatronInfo patronInfo;
	public BillingInfo billingInfo;

	public Request() {
	}

	public Request(
		RequestHeader header,
		BibliographicInfo bibliographicInfo,
		PublicationInfo publicationInfo,
		ServiceInfo serviceInfo,
		RequestingAgencyInfo requestingAgencyInfo,
		PatronInfo patronInfo,
		BillingInfo billingInfo
	) {
		this.header = header;
		this.bibliographicInfo = bibliographicInfo;
		this.publicationInfo = publicationInfo;
		this.serviceInfo = serviceInfo;
		this.requestingAgencyInfo = (requestingAgencyInfo == null ? null : (requestingAgencyInfo.isSet() ? requestingAgencyInfo : null));
		this.patronInfo = patronInfo;
		this.billingInfo = (billingInfo == null ? null : (billingInfo.isSet() ? billingInfo : null));
	}

	public void addSupplierInfo(SupplierInfo supplierInfo) {
		if ((supplierInfo != null) && supplierInfo.isSet()) {
			if (this.supplierInfo == null) {
				this.supplierInfo = new ArrayList<SupplierInfo>();
			}
			this.supplierInfo.add(supplierInfo);
		}
	}

	public void addRequestedDeliveryInfo(RequestedDeliveryInfo requestedDeliveryInfo) {
		if (requestedDeliveryInfo != null) {
			if (this.requestedDeliveryInfo == null) {
				this.requestedDeliveryInfo = new ArrayList<RequestedDeliveryInfo>();
			}
			this.requestedDeliveryInfo.add(requestedDeliveryInfo);
		}
	}
}

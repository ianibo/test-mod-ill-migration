package com.k_int.ill.iso18626.types;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.k_int.ill.iso18626.codes.closed.ServiceType;
import com.k_int.ill.iso18626.codes.closed.YesNo;
import com.k_int.ill.iso18626.codes.open.DeliveredFormat;
import com.k_int.ill.iso18626.codes.open.DeliveryMethod;
import com.k_int.ill.iso18626.codes.open.ItemFormat;
import com.k_int.ill.iso18626.codes.open.LoanCondition;
import com.k_int.ill.iso18626.codes.open.PaymentMethod;
import com.k_int.ill.iso18626.codes.open.SentVia;
import com.k_int.ill.iso18626.complexTypes.Address;
import com.k_int.ill.iso18626.complexTypes.Costs;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeliveryInfo {

	public String dateSent;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<String> itemId;
	public YesNo sentToPatron;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<LoanCondition> loanCondition;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<Costs> deliveryCosts;

	// Redundant for 2021
	public SentVia sentVia;
	public DeliveredFormat deliveredFormat;

	// New for 2021
	@JsonProperty("URL")
	public String url;
	public DeliveryMethod deliveryMethod;
	public Address address;
	public ItemFormat itemFormat;
	public ServiceType serviceType;
	public PaymentMethod paymentMethod;

	public DeliveryInfo() {
	}

	public DeliveryInfo(
			String dateSent,
			String itemId,
			String sentViaCode,
			boolean sentToPatron,
			String loanConditionCode,
			String deliveredFormatCode,
			Costs deliveryCosts
	) {
		// Constructor for ISO-18626 version 2017
		this(
			dateSent,
			itemId,
			null,
			null,
			null,
			sentToPatron,
			loanConditionCode,
			null,
			null,
			deliveryCosts,
			null
		);

		// The fields specific to 2017
		if (sentViaCode != null) {
			this.sentVia = new SentVia(sentViaCode);
		}
		if (deliveredFormatCode != null) {
			this.deliveredFormat = new DeliveredFormat(deliveredFormatCode);
		}
	}
	
	public DeliveryInfo(
		String dateSent,
		String itemId,
		String url,
		String deliveryMethodCode,
		Address address,
		boolean sentToPatron,
		String loanConditionCode,
		String itemFormatCode,
		String serviceTypeCode,
		Costs deliveryCosts,
		String paymentMethodCode
	) {
		// Constructor for ISO-18626 version 2021
		this.dateSent = dateSent;
		this.url = url;
		if (deliveryMethodCode != null) {
			this.deliveryMethod = new DeliveryMethod(deliveryMethodCode);
		}
		this.address = address;
		this.sentToPatron = new YesNo(sentToPatron ? YesNo.YES : YesNo.NO);
		if (itemFormatCode != null) {
			this.itemFormat = new ItemFormat(itemFormatCode);
		}
		if (serviceTypeCode != null) {
			this.serviceType = new ServiceType(serviceTypeCode);
		}
		if (paymentMethodCode != null) {
			this.paymentMethod = new PaymentMethod(paymentMethodCode);
		}
		addItemId(itemId);
		addLoanCondition(loanConditionCode);
		addDeliveryCosts(deliveryCosts);
	}

	public void addItemId(String itemId) {
		// Only add it if it is not null or blank
		if ((itemId != null) && !itemId.isBlank()) {
			if (this.itemId == null) {
				this.itemId = new ArrayList<String>();
			}
			this.itemId.add(itemId);
		}
	}

	public void addLoanCondition(String loanConditioncode) {
		// Only add it if it is not null or blank
		if ((loanConditioncode != null) && !loanConditioncode.isBlank()) {
			if (this.loanCondition == null) {
				this.loanCondition = new ArrayList<LoanCondition>();
			}
			this.loanCondition.add(new LoanCondition(loanConditioncode));
		}
	}

	public void addDeliveryCosts(Costs deliveryCosts) {
		// Only add it if it is not null
		if (deliveryCosts != null) {
			if (this.deliveryCosts == null) {
				this.deliveryCosts = new ArrayList<Costs>();
			}
			this.deliveryCosts.add(deliveryCosts);
		}
	}
}

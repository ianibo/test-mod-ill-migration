package com.k_int.ill.iso18626.types;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.k_int.ill.iso18626.codes.closed.ServiceType;
import com.k_int.ill.iso18626.codes.open.DeliveryMethod;
import com.k_int.ill.iso18626.codes.open.ItemFormat;
import com.k_int.ill.iso18626.codes.open.LoanCondition;
import com.k_int.ill.iso18626.codes.open.PaymentMethod;
import com.k_int.ill.iso18626.codes.open.ServiceLevel;
import com.k_int.ill.iso18626.complexTypes.Costs;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RetryInfo {

	@JacksonXmlElementWrapper(useWrapping=false)
	public List<LoanCondition> loanCondition;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<String> edition;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<ItemFormat> itemFormat;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<String> volume;
	public ServiceType serviceType;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<ServiceLevel> serviceLevel;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<DeliveryMethod> deliveryMethod;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<String> courierName;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<Costs> offeredCosts;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<PaymentMethod> paymentMethod;
	public String retryAfter;
	public String retryBefore;

	public RetryInfo() {
	}

	public RetryInfo(
		String serviceTypeCode,
		String retryAfter,
		String retryBefore
	) {
		if (serviceTypeCode != null) {
			this.serviceType = new ServiceType(serviceTypeCode);
		}
		this.retryAfter = retryAfter;
		this.retryBefore = retryBefore;
	}

	public void addLoanCondition(String loanConditionCode) {
		if (loanConditionCode != null) {
			if (loanCondition == null) {
				loanCondition = new ArrayList<LoanCondition>();
			}
			loanCondition.add(new LoanCondition(loanConditionCode));
		}
	}

	public void addEdition(String edition) {
		if (edition != null) {
			if (this.edition == null) {
				this.edition = new ArrayList<String>();
			}
			this.edition.add(edition);
		}
	}

	public void addItemFormat(String itemFormatCode) {
		if (itemFormatCode != null) {
			if (itemFormat == null) {
				itemFormat = new ArrayList<ItemFormat>();
			}
			itemFormat.add(new ItemFormat(itemFormatCode));
		}
	}

	public void addVolume(String volume) {
		if (volume != null) {
			if (this.volume == null) {
				this.volume = new ArrayList<String>();
			}
			this.volume.add(volume);
		}
	}

	public void addServiceLevel(String serviceLevelCode) {
		if (serviceLevelCode != null) {
			if (serviceLevel == null) {
				serviceLevel = new ArrayList<ServiceLevel>();
			}
			serviceLevel.add(new ServiceLevel(serviceLevelCode));
		}
	}

	public void addDeliveryMethod(String deliveryMethodCode) {
		if (deliveryMethodCode != null) {
			if (deliveryMethod == null) {
				deliveryMethod = new ArrayList<DeliveryMethod>();
			}
			deliveryMethod.add(new DeliveryMethod(deliveryMethodCode));
		}
	}

	public void addCourierName(String courierName) {
		if (courierName != null) {
			if (this.courierName == null) {
				this.courierName = new ArrayList<String>();
			}
			this.courierName.add(courierName);
		}
	}

	public void addOfferedCosts(Costs offeredCosts) {
		if (offeredCosts != null) {
			if (this.offeredCosts == null) {
				this.offeredCosts = new ArrayList<Costs>();
			}
			this.offeredCosts.add(offeredCosts);
		}
	}

	public void addPaymentMethod(PaymentMethod paymentMethodCode) {
		if (paymentMethodCode != null) {
			if (paymentMethod == null) {
				paymentMethod = new ArrayList<PaymentMethod>();
			}
			paymentMethod.add(paymentMethodCode);
		}
	}
}

package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.open.BillingMethod;
import com.k_int.ill.iso18626.codes.open.PaymentMethod;
import com.k_int.ill.iso18626.complexTypes.Address;
import com.k_int.ill.iso18626.complexTypes.Costs;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BillingInfo {

	public PaymentMethod paymentMethod;
	public Costs maximumCosts;
	public BillingMethod billingMethod;
	public String billingName;
	public Address address;

	public BillingInfo() {
	}

	public BillingInfo(
		String paymentMethodCode,
		Costs maximumCosts,
		String billingMethodCode,
		String billingName,
		Address address
	) {
		if (paymentMethodCode != null) {
			this.paymentMethod = new PaymentMethod(paymentMethodCode);
		}
		this.maximumCosts = maximumCosts;
		if (billingMethodCode != null) {
			this.billingMethod = new BillingMethod(billingMethodCode);
		}
		this.billingName = billingName;
		this.address = address;
	}

	public boolean isSet() {
		return(
			(paymentMethod != null) &&
			(maximumCosts != null) &&
			(billingMethod != null) &&
			(billingName != null) &&
			(address != null)
		);
	}
}

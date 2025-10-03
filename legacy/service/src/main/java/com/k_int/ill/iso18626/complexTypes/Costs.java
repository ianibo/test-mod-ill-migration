package com.k_int.ill.iso18626.complexTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.open.CostType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Costs {

	public String currencyCode;
	public float monetaryValue;

	// New for 2021
	public CostType costType;

	public Costs() {
	}

	public Costs(
		String currencyCode,
		float monetaryValue
	) {
		// Constructor for ISO-18626 version 2017
		this(
			currencyCode,
			monetaryValue,
			null
		);
	}

	public Costs(
		String currencyCode,
		float monetaryValue,
		String costTypeCode
	) {
		// Constructor for ISO-18626 version 2021
		this.currencyCode = currencyCode;
		this.monetaryValue = monetaryValue;
		if (costTypeCode != null) {
			costType = new CostType(costTypeCode);
		}
	}
}

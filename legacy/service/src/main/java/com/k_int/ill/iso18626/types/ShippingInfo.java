package com.k_int.ill.iso18626.types;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.k_int.ill.iso18626.codes.closed.YesNo;
import com.k_int.ill.iso18626.complexTypes.Costs;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ShippingInfo {

	public String courierName;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<String> trackingId;
	public YesNo insurance;
	public YesNo insuranceThirdParty;
	public String thirdPartyName;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<Costs> insuranceCosts;

	public ShippingInfo() {
	}

	public ShippingInfo(
		String courierName,
		String insuranceCode,
		String insuranceThirdPartyCode,
		String thirdPartyName
	) {
		this.courierName = courierName;
		if (insuranceCode != null) {
			insurance = new YesNo(insuranceCode);
		}
		if (insuranceThirdPartyCode != null) {
			insuranceThirdParty = new YesNo(insuranceThirdPartyCode);
		}
		this.thirdPartyName = thirdPartyName;
		
	}

	public void addTrackingId(String trackingId) {
		if (trackingId != null) {
			if (this.trackingId == null) {
				this.trackingId = new ArrayList<String>();
			}
			this.trackingId.add(trackingId);
		}
	}

	public void addInsuranceCost(Costs insuranceCost) {
		if (insuranceCost != null) {
			if (this.insuranceCosts == null) {
				this.insuranceCosts = new ArrayList<Costs>();
			}
			this.insuranceCosts.add(insuranceCost);
		}
	}
}

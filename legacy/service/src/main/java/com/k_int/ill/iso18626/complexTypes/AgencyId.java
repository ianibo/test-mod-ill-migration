package com.k_int.ill.iso18626.complexTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.open.AgencyIdType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AgencyId {

	public AgencyIdType agencyIdType;
	public String agencyIdValue;

	public AgencyId() {
	}

	public AgencyId(String agencyIdType, String agencyIdValue) {
		this.agencyIdType = new AgencyIdType(agencyIdType);
		this.agencyIdValue = agencyIdValue;
	}

    public String toSymbol() {
        StringBuffer symbolBuffer = new StringBuffer();
        if (agencyIdType != null) {
            symbolBuffer.append(agencyIdType.code).append(":");
        }
        symbolBuffer.append(agencyIdValue);
        return(symbolBuffer.toString());
    }
}

package com.k_int.ill.iso18626.types;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.k_int.ill.iso18626.complexTypes.Address;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestingAgencyInfo {

	public String name;
	public String contactName;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<Address> address;

	public RequestingAgencyInfo() {
	}

	public RequestingAgencyInfo(
		String name,
		String contactName
	) {
		this.name = name;
		this.contactName = contactName;
	}

	public void addAddress(Address address) {
		if (address != null) {
			if (this.address != null) {
				this.address = new ArrayList<Address>();
			}
			this.address.add(address);
		}
	}

	public boolean isSet() {
		return(
			(name != null) ||
			(contactName != null) ||
			(address != null)
		);
	}
}

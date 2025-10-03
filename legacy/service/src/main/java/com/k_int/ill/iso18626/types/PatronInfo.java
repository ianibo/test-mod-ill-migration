package com.k_int.ill.iso18626.types;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.k_int.ill.iso18626.codes.closed.YesNo;
import com.k_int.ill.iso18626.codes.open.PatronType;
import com.k_int.ill.iso18626.complexTypes.Address;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PatronInfo {

	public String patronId;
	public String surname;
	public String givenName;
	public PatronType patronType;
	public YesNo sendToPatron;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<Address> address;

	public PatronInfo() {
	}

	public PatronInfo(
		String patronId,
		String surname,
		String givenName,
		String patronTypeCode,
		boolean sendToPatron
	) {
		this.patronId = patronId;
		this.surname = surname;
		this.givenName = givenName;
		if (patronTypeCode != null) {
			this.patronType = new PatronType(patronTypeCode);
		}
		this.sendToPatron = new YesNo(sendToPatron ? YesNo.YES : YesNo.NO);
	}

	public void addAddress(Address address) {
		if (address != null) {
			if (this.address != null) {
				this.address = new ArrayList<Address>();
			}
			this.address.add(address);
		}
	}
}

package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class ElectronicAddressType extends Code {
	// The initial valid elecronic address  type codes
	public static final String CHAT  = "Chat";
	public static final String EMAIL = "Email";
	public static final String FTP   = "FTP";
	public static final String SKYPE = "Skype";
	public static final String URL   = "url";

	static {
		add(CHAT);
		add(EMAIL);
		add(FTP);
		add(SKYPE);
	}

	public ElectronicAddressType() {
	}

	public ElectronicAddressType(String electronicAddressType) {
		super(electronicAddressType);
	}
}

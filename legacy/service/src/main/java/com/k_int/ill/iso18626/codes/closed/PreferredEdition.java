package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class PreferredEdition extends Code {
	// The valid preferred edition codes
	public static final String ANY_EDITON          = "AnyEdition";
	public static final String MOST_RECENT_EDITION = "MostRecentEdition";
	public static final String THIS_EDITION        = "ThisEdition";

	static {
		add(ANY_EDITON);
		add(MOST_RECENT_EDITION);
		add(THIS_EDITION);
	}

	public PreferredEdition() {
	}

	public PreferredEdition(String preferredEdition) {
		super(preferredEdition);
	}
}

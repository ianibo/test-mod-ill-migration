package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class LoanCondition extends Code {
	// The initial valid loan condition codes
	public static final String LIBRARY_USE_ONLY       = "LibraryUseOnly";
	public static final String NO_REPRODUCTION        = "NoReproduction";
	public static final String SIGNATURE_REQUIRED     = "SignatureRequired";
	public static final String SPEC_COLL_SUPERV_REQ   = "SpecCollSupervReq";
	public static final String WATCH_LIBRARY_USE_ONLY = "WatchLibraryUseOnly";

	static {
		add(LIBRARY_USE_ONLY);
		add(NO_REPRODUCTION);
		add(SIGNATURE_REQUIRED);
		add(SPEC_COLL_SUPERV_REQ);
		add(WATCH_LIBRARY_USE_ONLY);
	}

	public LoanCondition() {
	}

	public LoanCondition(String loanCondition) {
		super(loanCondition);
	}
}

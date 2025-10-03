package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class ReasonRetry extends Code {
	// The initial valid reason retry codes
	public static final String AT_BINDERY                    = "AtBindery";
	public static final String COST_EXCEEDS_MAX_COST         = "CostExceedsMaxCost";
	public static final String LOAN_POSSIBLE                 = "LoanPossible";
	public static final String NOT_CURRENT_AVAILABLE_FOR_ILL = "NotCurrentAvailableForILL";
	public static final String NOT_FOUND_AS_CITED            = "NotFoundAsCited";
	public static final String ON_LOAN                       = "OnLoan";
	public static final String ON_ORDER                      = "OnOrder";
	public static final String REQ_DEL_DATE_NOT_POSSIBLE     = "ReqDelDateNotPossible";
	public static final String REQ_DEL_METHOD_NOT_SUPP       = "ReqDelMethodNotSupp";

	static {
		add(AT_BINDERY);
		add(COST_EXCEEDS_MAX_COST);
		add(LOAN_POSSIBLE);
		add(NOT_CURRENT_AVAILABLE_FOR_ILL);
		add(NOT_FOUND_AS_CITED);
		add(ON_LOAN);
		add(ON_ORDER);
		add(REQ_DEL_DATE_NOT_POSSIBLE);
		add(REQ_DEL_METHOD_NOT_SUPP);
	}

	public ReasonRetry() {
	};

	public ReasonRetry(String reasonRetry) {
		super(reasonRetry);
	};
}

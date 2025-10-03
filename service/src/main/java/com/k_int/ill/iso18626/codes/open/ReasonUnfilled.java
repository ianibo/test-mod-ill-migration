package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class ReasonUnfilled extends Code {
	// The initial valid reason unfilled codes
	public static final String NON_CIRCULATING = "NonCirculating";
	public static final String NOT_AVAILABLE_FOR_ILL = "NotAvailableForILL";
	public static final String NOT_HELD = "NotHeld";
	public static final String NOT_ON_SHELF = "NotOnShelf";
	public static final String POLICY_PROBLEM = "PolicyProblem";
	public static final String POOR_CONDITION = "PoorCondition";

	static {
		add(NON_CIRCULATING);
		add(NOT_AVAILABLE_FOR_ILL);
		add(NOT_HELD);
		add(NOT_ON_SHELF);
		add(POLICY_PROBLEM);
		add(POOR_CONDITION);
	}

	public ReasonUnfilled() {
	};

	public ReasonUnfilled(String reasonUnfilled) {
		super(reasonUnfilled);
	};
}

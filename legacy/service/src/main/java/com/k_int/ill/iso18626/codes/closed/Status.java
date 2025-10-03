package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class Status extends Code {
	// The valid status codes
	public static final String CANCELLED                = "Cancelled";
	public static final String COMPLETED_WITHOUT_RETURN = "CompletedWithoutReturn";
	public static final String COPY_COMPLETED           = "CopyCompleted";
	public static final String EXPECT_TO_SUPPLY         = "ExpectToSupply";
	public static final String LOAN_COMPLETED           = "LoanCompleted";
	public static final String LOANED                   = "Loaned";
	public static final String OVERDUE                  = "Overdue";
	public static final String RECALLED                 = "Recalled";
	public static final String REQUEST_RECEIVED         = "RequestReceived";
	public static final String RETRY_POSSIBLE           = "RetryPossible";
	public static final String UNFILLED                 = "Unfilled";
	public static final String WILL_SUPPLY              = "WillSupply";

	static {
		add(CANCELLED);
		add(COMPLETED_WITHOUT_RETURN);
		add(COPY_COMPLETED);
		add(EXPECT_TO_SUPPLY);
		add(LOAN_COMPLETED);
		add(LOANED);
		add(OVERDUE);
		add(RECALLED);
		add(REQUEST_RECEIVED);
		add(RETRY_POSSIBLE);
		add(UNFILLED);
		add(WILL_SUPPLY);
	}

	public Status() {
	}

	public Status(String status) {
		super(status);
	}
}

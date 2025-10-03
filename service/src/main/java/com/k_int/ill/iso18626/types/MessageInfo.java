package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.closed.ReasonForMessage;
import com.k_int.ill.iso18626.codes.closed.YesNo;
import com.k_int.ill.iso18626.codes.open.ReasonRetry;
import com.k_int.ill.iso18626.codes.open.ReasonUnfilled;
import com.k_int.ill.iso18626.complexTypes.Costs;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageInfo {

	public ReasonForMessage reasonForMessage;
	public YesNo answerYesNo;
	public String note;
	public ReasonUnfilled reasonUnfilled;
	public ReasonRetry reasonRetry;

	// Redundant for 2021
	public Costs offeredCosts;
	public String retryAfter;
	public String retryBefore;

	public MessageInfo() {
	}


	public MessageInfo(
		String reasonForMessageCode,
		boolean answerYesNo,
		String note,
		String reasonUnfilledCode,
		String reasonRetryCode,
		Costs offeredCosts,
		String retryAfter,
		String retryBefore
	) {
		// Constructor for ISO-18626 version 2017
		this(
			reasonForMessageCode,
			answerYesNo,
			note,
			reasonUnfilledCode,
			reasonRetryCode
		);

		// Now the properties that are specific for 2017
		this.offeredCosts = offeredCosts;
		this.retryAfter = retryAfter;
		this.retryBefore = retryBefore;
	}

	public MessageInfo(
		String reasonForMessageCode,
		boolean answerYesNo,
		String note,
		String reasonUnfilledCode,
		String reasonRetryCode
	) {
		// Constructor for ISO-18626 version 2021
		if (reasonForMessageCode != null) {
			this.reasonForMessage = new ReasonForMessage(reasonForMessageCode);
		}
		this.answerYesNo = new YesNo(answerYesNo ? YesNo.YES : YesNo.NO);
		this.note = note;
		if (reasonUnfilledCode != null) {
			this.reasonUnfilled = new ReasonUnfilled(reasonUnfilledCode);
		}
		if (reasonRetryCode != null) {
			this.reasonRetry = new ReasonRetry(reasonRetryCode);
		}
	}
}

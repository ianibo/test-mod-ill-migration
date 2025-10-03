package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.closed.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorData extends Header {

	public ErrorCode errorType;
	public String errorValue;

	public ErrorData() {
	}

	public ErrorData(
		String errorType,
		String errorValue
	) {
		if (errorType != null) {
			this.errorType = new ErrorCode(errorType);
		}
		this.errorValue = errorValue;
	}
}

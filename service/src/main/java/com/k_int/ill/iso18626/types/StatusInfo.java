package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.closed.Status;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StatusInfo {

	public Status status;
	public String expectedDeliveryDate;
	public String dueDate;
	public String lastChange;

	public StatusInfo() {
	}

	public StatusInfo(
		String statusCode,
		String expectedDeliveryDate,
		String dueDate,
		String lastChange
	) {
		if (statusCode != null) {
			this.status = new Status(statusCode);
		}
		this.expectedDeliveryDate = expectedDeliveryDate;
		this.dueDate = dueDate;
		this.lastChange = lastChange;
	}
}

package com.k_int.ill.iso18626.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.closed.Action;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActiveSection extends Header {

	public Action action;
	public String note;

	public ActiveSection() {
	}

	public ActiveSection(
		String actionCode,
		String note
	) {
		if (actionCode != null) {
			this.action = new Action(actionCode);
		}
		this.note = note;
	}
}

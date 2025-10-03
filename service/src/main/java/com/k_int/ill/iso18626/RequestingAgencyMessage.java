package com.k_int.ill.iso18626;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.k_int.ill.iso18626.codes.closed.Action;
import com.k_int.ill.iso18626.types.ActiveSection;
import com.k_int.ill.iso18626.types.RequestingAgencyHeader;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestingAgencyMessage {

	public RequestingAgencyHeader header;
	public ActiveSection activeSection;

    // Strictly speaking these 2 fields should not live at this level, but that is where they are in the xsd
    // and apparently the xsd trumps the standard
    // Remove these, when the standard rules ... which will probably be never as it appears it is not looked at
    public Action action;
    public String note;

	public RequestingAgencyMessage() {
	}

	public RequestingAgencyMessage(
		RequestingAgencyHeader header,
		ActiveSection activeSection
	) {
		this.header = header;

        // This breaks the standard but conforms to the xsd, but the xsd trumps the standard apparently
		//this.activeSection = activeSection;
        if (activeSection != null) {
            action = activeSection.action;
            note = activeSection.note;
        }
	}

    /**
     * We need to take into account the standard and the xsd as we may have data coming in both ways
     * @return The action associated with this message
     */
    public Action findAction() {
        Action action = this.action;
        if (activeSection != null) {
            action = activeSection.action;
        }
        return(action);
    }

    /**
     * We need to take into account the standard and the xsd as we may have data coming in both ways
     * @return The action code associated with this message
     */
    public String findActionCode() {
        // The non standard way for those where that accepts the xsd trumps the standard
        String actionCode = (action == null) ? null : action.code;
        if (activeSection != null) {
            // The standard way
            actionCode = activeSection.action == null ? null : activeSection.action.code;
        }
        return(actionCode);
    }

    /**
     * We need to take into account the standard and the xsd as we may have data coming in both ways
     * @return The note associated with this message
     */
    public String findNote() {
        String note = this.note;
        if (activeSection != null) {
            note = activeSection.note;
        }
        return(note);
    }
}

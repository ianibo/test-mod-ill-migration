package com.k_int.ill.referenceData.protocolActionEvent;

import com.k_int.ill.Protocol;
import com.k_int.ill.ProtocolActionEvent;

/**
 * The base class for creating action event protocol records
 * @author Chas
 *
 */
public abstract class BaseProtocolActionEvent {

    public abstract void load(Protocol protocol);

	/**
	 * Loads the action events that trigger a protocol message for a protocol
	 * @param protocol the protocol the action events are for
	 * @param actionEvents the action events that trigger a protocol message
	 */
    protected void loadActions(
		Protocol protocol,
		List<String> actionEvents
	) {
		actionEvents.each { String actionEvent -> 		 		
			ProtocolActionEvent.ensure(
				protocol,
				actionEvent
			);
		}
	}
}

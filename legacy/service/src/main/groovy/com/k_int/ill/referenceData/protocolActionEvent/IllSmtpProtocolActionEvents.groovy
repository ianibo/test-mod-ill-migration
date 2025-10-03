package com.k_int.ill.referenceData.protocolActionEvent;

import com.k_int.ill.Protocol;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.Events;

import groovy.util.logging.Slf4j;

/**
 * The class for creating action event protocol records for ILL SMTP
 * @author Chas
 *
 */
@Slf4j
public class IllSmtpProtocolActionEvents extends BaseProtocolActionEvent {

	@Override
    public void load(Protocol protocol) {
        log.info('Adding action events for the ILL SMTP protocol');

		List<String> actionEvents = [
			Actions.ACTION_MANUAL_CLOSE,
//			Actions.ACTION_MESSAGE,
//			Actions.ACTION_REQUESTER_INFORMED_NOT_SUPPLY,
//			Actions.ACTION_REQUESTER_INFORMED_RETURNED,
//			Actions.ACTION_REQUESTER_INFORMED_SHIPPED,
//			Actions.ACTION_REQUESTER_INFORMED_WILL_SUPPLY,
		
//			Actions.ACTION_REQUESTER_EDIT,
			Actions.ACTION_REQUESTER_PATRON_RETURNED_ITEM_AND_SHIPPED,
			Actions.ACTION_REQUESTER_REQUESTER_CANCEL,
			Actions.ACTION_REQUESTER_REQUESTER_RECEIVED,
			Actions.ACTION_REQUESTER_SHIPPED_RETURN,
			
			Events.EVENT_REQUESTER_NEW_PATRON_REQUEST_INDICATION
		];

		// Load the action events
		loadActions(protocol, actionEvents);
	}
}

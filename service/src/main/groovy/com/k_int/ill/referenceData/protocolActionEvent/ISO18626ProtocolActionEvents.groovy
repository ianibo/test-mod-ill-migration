package com.k_int.ill.referenceData.protocolActionEvent;

import com.k_int.ill.Protocol;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.Events;

import groovy.util.logging.Slf4j;

/**
 * The class for creating action event protocol records for ISO18626
 * @author Chas
 *
 */
@Slf4j
public class ISO18626ProtocolActionEvents extends BaseProtocolActionEvent {

	@Override
    public void load(Protocol protocol) {
        log.info('Adding action events for the ISO18626 protocol');

		List<String> actionEvents = [
			Actions.ACTION_MANUAL_CLOSE,
			Actions.ACTION_MESSAGE,

			Actions.ACTION_REQUESTER_EDIT,
			Actions.ACTION_REQUESTER_PATRON_RETURNED_ITEM_AND_SHIPPED,
			Actions.ACTION_REQUESTER_REQUESTER_AGREE_CONDITIONS,
			Actions.ACTION_REQUESTER_REQUESTER_CANCEL,
			Actions.ACTION_REQUESTER_REQUESTER_RECEIVED,
			Actions.ACTION_REQUESTER_REQUESTER_REJECT_CONDITIONS,
			Actions.ACTION_REQUESTER_SHIPPED_RETURN,

			Actions.ACTION_RESPONDER_RESPOND_YES,
			Actions.ACTION_RESPONDER_SUPPLIER_ADD_CONDITION,
			Actions.ACTION_RESPONDER_SUPPLIER_CANNOT_SUPPLY,
			Actions.ACTION_RESPONDER_SUPPLIER_CHECK_INTO_ILL_AND_MARK_SHIPPED,
			Actions.ACTION_RESPONDER_SUPPLIER_CHECKOUT_OF_ILL,
			Actions.ACTION_RESPONDER_SUPPLIER_CONDITIONAL_SUPPLY,
			Actions.ACTION_RESPONDER_SUPPLIER_FILL_DIGITAL_LOAN,
			Actions.ACTION_RESPONDER_SUPPLIER_MARK_SHIPPED,
			Actions.ACTION_RESPONDER_SUPPLIER_RESPOND_TO_CANCEL,

			Events.EVENT_RESPONDER_NEW_PATRON_REQUEST_INDICATION,
			Events.EVENT_STATUS_RES_AWAIT_DESEQUESTRATION_INDICATION,
			Events.EVENT_STATUS_RES_CANCEL_REQUEST_RECEIVED_INDICATION,
			Events.EVENT_STATUS_RES_OVERDUE_INDICATION
		];
		
		// Load the action events
		loadActions(protocol, actionEvents);
	}
}

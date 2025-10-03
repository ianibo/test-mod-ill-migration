package com.k_int.ill.referenceData.availableActions;

import com.k_int.ill.statemodel.ActionEventResultList;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.AvailableAction;
import com.k_int.ill.statemodel.StateModel;
import com.k_int.ill.statemodel.Status;

import groovy.util.logging.Slf4j;

/**
 * Creates the available actions for the requester copy state model
 * @author Chas
 *
 */
@Slf4j
public class RequesterCopyAvailableActions extends BaseAvailableActions {

    public void load(StateModel stateModel) {
        log.info('Adding available actions for the RequesterCopy state model');

        // REQ_REQUEST_SENT_TO_SUPPLIER OR "Request sent"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER, Actions.ACTION_REQUESTER_REQUESTER_CANCEL, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SENT_TO_SUPPLIER_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SENT_TO_SUPPLIER_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SENT_TO_SUPPLIER_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SENT_TO_SUPPLIER_ISO18626);
		AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER, Actions.ACTION_REQUESTER_INFORMED_NOT_SUPPLY, AvailableAction.TRIGGER_TYPE_MANUAL, ActionEventResultList.REQUESTER_INFORMED_NOT_SUPPLY);
		AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER, Actions.ACTION_REQUESTER_INFORMED_SHIPPED, AvailableAction.TRIGGER_TYPE_MANUAL, ActionEventResultList.REQUESTER_INFORMED_SHIPPED);
		AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER, Actions.ACTION_REQUESTER_INFORMED_WILL_SUPPLY, AvailableAction.TRIGGER_TYPE_MANUAL, ActionEventResultList.REQUESTER_INFORMED_WILL_SUPPLY);

		// Needed for when the supplier does not inform you it has been shipped		
		AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER, Actions.ACTION_REQUESTER_REQUESTER_RECEIVED, AvailableAction.TRIGGER_TYPE_MANUAL);
		
        // REQ_CONDITIONAL_ANSWER_RECEIVED OR "Loan conditions received"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED, Actions.ACTION_REQUESTER_REQUESTER_AGREE_CONDITIONS, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED, Actions.ACTION_REQUESTER_REQUESTER_REJECT_CONDITIONS, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED, Actions.ACTION_REQUESTER_REQUESTER_CANCEL, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_CONDITION_ANSWER_RECEIVED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_CONDITION_ANSWER_RECEIVED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_CONDITION_ANSWER_RECEIVED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_CONDITION_ANSWER_RECEIVED_ISO18626);

        // REQ_IDLE OR "New"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_IDLE, Actions.ACTION_REQUESTER_REQUESTER_CANCEL, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_IDLE, Actions.ACTION_REQUESTER_BORROWER_CHECK, AvailableAction.TRIGGER_TYPE_MANUAL)

        // REQ_INVALID_PATRON OR "Invalid patron"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_INVALID_PATRON, Actions.ACTION_REQUESTER_REQUESTER_CANCEL, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_INVALID_PATRON, Actions.ACTION_REQUESTER_BORROWER_CHECK, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_INVALID_PATRON, Actions.ACTION_REQUESTER_BORROWER_CHECK_OVERRIDE, AvailableAction.TRIGGER_TYPE_MANUAL)

        // REQ_CANCEL_PENDING OR "Cancel pending"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CANCEL_PENDING, Actions.ACTION_REQUESTER_ISO18626_CANCEL_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_CANCEL_PENDING_ISO18626);

        // REQ_VALIDATED OR "Validated"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_VALIDATED, Actions.ACTION_REQUESTER_REQUESTER_CANCEL, AvailableAction.TRIGGER_TYPE_MANUAL)

        // REQ_SOURCING_ITEM OR "Sourcing"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SOURCING_ITEM, Actions.ACTION_REQUESTER_REQUESTER_CANCEL, AvailableAction.TRIGGER_TYPE_MANUAL)

        // REQ_SUPPLIER_IDENTIFIED OR "Supplier identified"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SUPPLIER_IDENTIFIED, Actions.ACTION_REQUESTER_REQUESTER_CANCEL, AvailableAction.TRIGGER_TYPE_MANUAL)

		// 
        // REQ_EXPECTS_TO_SUPPLY OR "Expects to supply"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_REQUESTER_REQUESTER_CANCEL, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_EXPECTS_TO_SUPPLY_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_EXPECTS_TO_SUPPLY_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_EXPECTS_TO_SUPPLY_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_EXPECTS_TO_SUPPLY_ISO18626);
		AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_REQUESTER_INFORMED_SHIPPED, AvailableAction.TRIGGER_TYPE_MANUAL, ActionEventResultList.REQUESTER_INFORMED_NOT_SUPPLY);
		AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_REQUESTER_INFORMED_SHIPPED, AvailableAction.TRIGGER_TYPE_MANUAL, ActionEventResultList.REQUESTER_INFORMED_SHIPPED);
		
        // REQ_SHIPPED OR "Shipped"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SHIPPED, Actions.ACTION_REQUESTER_REQUESTER_RECEIVED, AvailableAction.TRIGGER_TYPE_MANUAL);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SHIPPED, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SHIPPED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SHIPPED, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SHIPPED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SHIPPED, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SHIPPED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SHIPPED, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SHIPPED_ISO18626);

        // REQ_BORROWING_LIBRARY_RECEIVED OR "Awaiting local item creation"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWING_LIBRARY_RECEIVED, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_BORROWING_LIBRARY_RECEIVED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWING_LIBRARY_RECEIVED, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_BORROWING_LIBRARY_RECEIVED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWING_LIBRARY_RECEIVED, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_BORROWING_LIBRARY_RECEIVED_ISO18626);

        // REQ_REQUEST_COMPLETE OR "Complete"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_COMPLETE, Actions.ACTION_MESSAGE, AvailableAction.TRIGGER_TYPE_SYSTEM, ActionEventResultList.REQUESTER_NO_STATUS_CHANGE)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_REQUEST_COMPLETE, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_NO_STATUS_CHANGE)

        // REQ_LOCAL_REVIEW OR "Requires review - locally available"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_LOCAL_REVIEW, Actions.ACTION_REQUESTER_FILL_LOCALLY, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_LOCAL_REVIEW, Actions.ACTION_REQUESTER_CANCEL_LOCAL, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_LOCAL_REVIEW, Actions.ACTION_REQUESTER_LOCAL_SUPPLIER_CANNOT_SUPPLY, AvailableAction.TRIGGER_TYPE_MANUAL)

        // The messageAllSeen action can be applied to all states
        assignToAllStates(stateModel, Actions.ACTION_MESSAGES_ALL_SEEN, AvailableAction.TRIGGER_TYPE_SYSTEM, ActionEventResultList.REQUESTER_NO_STATUS_CHANGE);

        // The messageSeen action can be applied to all states
        assignToAllStates(stateModel, Actions.ACTION_MESSAGE_SEEN, AvailableAction.TRIGGER_TYPE_SYSTEM, ActionEventResultList.REQUESTER_NO_STATUS_CHANGE);

        // The message action can be applied to all active states
        assignToActiveStates(stateModel, Actions.ACTION_MESSAGE, AvailableAction.TRIGGER_TYPE_MANUAL, ActionEventResultList.REQUESTER_NO_STATUS_CHANGE);

        // The manualClose action can be applied to all non terminal states
        assignToActiveStates(stateModel, Actions.ACTION_MANUAL_CLOSE, AvailableAction.TRIGGER_TYPE_MANUAL, ActionEventResultList.REQUESTER_CLOSE_MANUAL);

        // The ISO18626Notification action can be applied to all active actions
        assignToActiveStates(stateModel, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_NOTIFICATION_RECEIVED_ISO18626);

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_BORROWING_LIBRARY_RECEIVED,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_BORROWING_LIBRARY_RECEIVED_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_CANCEL_PENDING,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_CANCEL_PENDING_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_CONDITIONAL_ANSWER_RECEIVED,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_CONDITION_ANSWER_RECEIVED_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_EXPECTS_TO_SUPPLY_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_REQUEST_SENT_TO_SUPPLIER,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_SENT_TO_SUPPLIER_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_SHIPPED,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_SHIPPED_ISO18626
        );
    }
}

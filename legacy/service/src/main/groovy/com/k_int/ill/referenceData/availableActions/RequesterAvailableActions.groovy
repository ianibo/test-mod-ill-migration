package com.k_int.ill.referenceData.availableActions;

import com.k_int.ill.statemodel.ActionEventResultList;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.AvailableAction;
import com.k_int.ill.statemodel.StateModel;
import com.k_int.ill.statemodel.Status;

import groovy.util.logging.Slf4j;

/**
 * Creates the available actions for the requester state model
 * @author Chas
 *
 */
@Slf4j
public class RequesterAvailableActions extends BaseAvailableActions {

    public void load(StateModel stateModel) {
        log.info('Adding available actions for the Requester state model');

        // REQ_BORROWING_LIBRARY_RECEIVED OR "Awaiting local item creation"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWING_LIBRARY_RECEIVED, Actions.ACTION_REQUESTER_REQUESTER_MANUAL_CHECKIN, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWING_LIBRARY_RECEIVED, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_BORROWING_LIBRARY_RECEIVED_ISO18626);

        // REQ_CHECKED_IN OR "In local circulation process"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CHECKED_IN, Actions.ACTION_REQUESTER_PATRON_RETURNED_ITEM, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CHECKED_IN, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_CHECKED_IN_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CHECKED_IN, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_CHECKED_IN_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CHECKED_IN, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_CHECKED_IN_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CHECKED_IN, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_CHECKED_IN_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_CHECKED_IN, Actions.ACTION_REQUESTER_PATRON_RETURNED_ITEM_AND_SHIPPED, AvailableAction.TRIGGER_TYPE_MANUAL)

        // REQ_AWAITING_RETURN_SHIPPING OR "Awaiting return shipping"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_AWAITING_RETURN_SHIPPING, Actions.ACTION_REQUESTER_SHIPPED_RETURN, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_AWAITING_RETURN_SHIPPING, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_AWAITING_RETURN_SHIPPING_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_AWAITING_RETURN_SHIPPING, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_AWAITING_RETURN_SHIPPING_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_AWAITING_RETURN_SHIPPING, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_AWAITING_RETURN_SHIPPING_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_AWAITING_RETURN_SHIPPING, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_AWAITING_RETURN_SHIPPING_ISO18626);

        // REQ_SHIPPED_TO_SUPPLIER OR "Return shipped"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SHIPPED_TO_SUPPLIER, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SHIPPED_TO_SUPPLIER_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SHIPPED_TO_SUPPLIER, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SHIPPED_TO_SUPPLIER_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SHIPPED_TO_SUPPLIER, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SHIPPED_TO_SUPPLIER_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_SHIPPED_TO_SUPPLIER, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_SHIPPED_TO_SUPPLIER_ISO18626);

        // REQ_OVERDUE OR "Overdue"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_OVERDUE, Actions.ACTION_REQUESTER_PATRON_RETURNED_ITEM, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_OVERDUE, Actions.ACTION_REQUESTER_REQUESTER_RECEIVED, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_OVERDUE, Actions.ACTION_REQUESTER_SHIPPED_RETURN, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_OVERDUE, Actions.ACTION_REQUESTER_PATRON_RETURNED_ITEM_AND_SHIPPED, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_OVERDUE, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_OVERDUE_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_OVERDUE, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_OVERDUE_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_OVERDUE, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_OVERDUE_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_OVERDUE, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_OVERDUE_ISO18626);

        // REQ_BORROWER_RETURNED
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWER_RETURNED, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_BORROWER_RETURNED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWER_RETURNED, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_BORROWER_RETURNED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWER_RETURNED, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_BORROWER_RETURNED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWER_RETURNED, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_BORROWER_RETURNED_ISO18626);
		AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_BORROWER_RETURNED, Actions.ACTION_REQUESTER_INFORMED_RETURNED, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_INFORMED_RETURNED);
		
        // REQ_RECALLED
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_RECALLED, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_RECALLED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_RECALLED, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_RECALLED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_RECALLED, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_RECALLED_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_RECALLED, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.REQUESTER_RECALLED_ISO18626);

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

        // These ones are for when the state is specified in the message from the responder for ISO-18626, hence trigger is system
        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_AWAITING_RETURN_SHIPPING,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_AWAITING_RETURN_SHIPPING_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_BORROWER_RETURNED,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_BORROWER_RETURNED_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_CHECKED_IN,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_CHECKED_IN_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_OVERDUE,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_OVERDUE_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_RECALLED,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_RECALLED_ISO18626
        );

        AvailableAction.ensure(
            stateModel,
            Status.PATRON_REQUEST_SHIPPED_TO_SUPPLIER,
            Actions.ACTION_INCOMING_ISO18626,
            AvailableAction.TRIGGER_TYPE_SYSTEM,
            ActionEventResultList.REQUESTER_SHIPPED_TO_SUPPLIER_ISO18626
        );
    }
}

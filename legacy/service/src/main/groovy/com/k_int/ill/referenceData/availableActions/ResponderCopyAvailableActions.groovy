package com.k_int.ill.referenceData.availableActions;

import com.k_int.ill.statemodel.ActionEventResultList;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.AvailableAction;
import com.k_int.ill.statemodel.StateModel;
import com.k_int.ill.statemodel.Status;

import groovy.util.logging.Slf4j;

/**
 * Creates the available actions for the responder copy state model
 * @author Chas
 *
 */
@Slf4j
public class ResponderCopyAvailableActions extends BaseAvailableActions {

    public void load(StateModel stateModel) {
        log.info('Adding available actions for the ResponderCopy state model');

        // RES_AWAIT_SHIP OR "Awaiting shipping"
        AvailableAction.ensure(stateModel, Status.RESPONDER_AWAIT_SHIP, Actions.ACTION_RESPONDER_SUPPLIER_MARK_SHIPPED, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_AWAIT_SHIP, Actions.ACTION_RESPONDER_SUPPLIER_ADD_CONDITION, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_AWAIT_SHIP, Actions.ACTION_RESPONDER_SUPPLIER_CHECK_INTO_ILL, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_AWAIT_SHIP, Actions.ACTION_RESPONDER_ISO18626_CANCEL, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_CANCEL_RECEIVED_ISO18626);

        // RES_IDLE OR "New"
        AvailableAction.ensure(stateModel, Status.RESPONDER_IDLE, Actions.ACTION_RESPONDER_RESPOND_YES, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_IDLE, Actions.ACTION_RESPONDER_SUPPLIER_CANNOT_SUPPLY, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_IDLE, Actions.ACTION_RESPONDER_SUPPLIER_CONDITIONAL_SUPPLY, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_IDLE, Actions.ACTION_RESPONDER_ISO18626_CANCEL, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_CANCEL_RECEIVED_ISO18626);

        // RES_PENDING_CONDITIONAL_ANSWER OR "Loan conditions sent"
        AvailableAction.ensure(stateModel, Status.RESPONDER_PENDING_CONDITIONAL_ANSWER, Actions.ACTION_RESPONDER_SUPPLIER_MARK_CONDITIONS_AGREED, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_PENDING_CONDITIONAL_ANSWER, Actions.ACTION_RESPONDER_SUPPLIER_CANNOT_SUPPLY, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_PENDING_CONDITIONAL_ANSWER, Actions.ACTION_RESPONDER_ISO18626_CANCEL, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_CANCEL_RECEIVED_ISO18626);

        // RES_CANCEL_REQUEST_RECEIVED OR "Cancel request received"
        AvailableAction.ensure(stateModel, Status.RESPONDER_CANCEL_REQUEST_RECEIVED, Actions.ACTION_RESPONDER_SUPPLIER_RESPOND_TO_CANCEL, AvailableAction.TRIGGER_TYPE_MANUAL)

        // RES_NEW_AWAIT_PULL_SLIP OR "Awaiting pull slip printing"
        AvailableAction.ensure(stateModel, Status.RESPONDER_NEW_AWAIT_PULL_SLIP, Actions.ACTION_RESPONDER_SUPPLIER_PRINT_PULL_SLIP, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_NEW_AWAIT_PULL_SLIP, Actions.ACTION_RESPONDER_SUPPLIER_ADD_CONDITION, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_NEW_AWAIT_PULL_SLIP, Actions.ACTION_RESPONDER_SUPPLIER_CANNOT_SUPPLY, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_NEW_AWAIT_PULL_SLIP, Actions.ACTION_RESPONDER_ISO18626_CANCEL, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_CANCEL_RECEIVED_ISO18626);

        // RES_AWAIT_PICKING OR "Searching"
        AvailableAction.ensure(stateModel, Status.RESPONDER_AWAIT_PICKING, Actions.ACTION_RESPONDER_SUPPLIER_CHECK_INTO_ILL, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_AWAIT_PICKING, Actions.ACTION_RESPONDER_SUPPLIER_CANNOT_SUPPLY, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_AWAIT_PICKING, Actions.ACTION_RESPONDER_SUPPLIER_ADD_CONDITION, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_AWAIT_PICKING, Actions.ACTION_RESPONDER_SUPPLIER_CHECK_INTO_ILL_AND_MARK_SHIPPED, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_AWAIT_PICKING, Actions.ACTION_RESPONDER_ISO18626_CANCEL, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_CANCEL_RECEIVED_ISO18626);

        // RES_ITEM_SHIPPED OR "Shipped"
        AvailableAction.ensure(stateModel, Status.RESPONDER_ITEM_SHIPPED, Actions.ACTION_RESPONDER_ISO18626_RECEIVED, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_RECEIVED_ISO18626);

        // RES_COMPLETE OR "Complete"
        AvailableAction.ensure(stateModel, Status.RESPONDER_COMPLETE, Actions.ACTION_MESSAGE, AvailableAction.TRIGGER_TYPE_SYSTEM, ActionEventResultList.RESPONDER_NO_STATUS_CHANGE)
        AvailableAction.ensure(stateModel, Status.RESPONDER_COMPLETE, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_NO_STATUS_CHANGE)

        // The messageAllSeen action can be applied to all states
        assignToAllStates(stateModel, Actions.ACTION_MESSAGES_ALL_SEEN, AvailableAction.TRIGGER_TYPE_SYSTEM, ActionEventResultList.RESPONDER_NO_STATUS_CHANGE);

        // The messageSeen action can be applied to all states
        assignToAllStates(stateModel, Actions.ACTION_MESSAGE_SEEN, AvailableAction.TRIGGER_TYPE_SYSTEM, ActionEventResultList.RESPONDER_NO_STATUS_CHANGE);

        // The message action can be applied to all active states
        assignToActiveStates(stateModel, Actions.ACTION_MESSAGE, AvailableAction.TRIGGER_TYPE_MANUAL, ActionEventResultList.RESPONDER_NO_STATUS_CHANGE);

        // The manualClose action can be applied to all non terminal states
        assignToActiveStates(stateModel, Actions.ACTION_MANUAL_CLOSE, AvailableAction.TRIGGER_TYPE_MANUAL, ActionEventResultList.RESPONDER_CLOSE_MANUAL);

        // The ISO18626Notification action can be applied to all active actions
        assignToActiveStates(stateModel, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_NOTIFICATION_RECEIVED_ISO18626);

        // The ISO18626StatusRequest action can be applied to all active responder actions
        assignToActiveStates(stateModel, Actions.ACTION_RESPONDER_ISO18626_STATUS_REQUEST, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_NO_STATUS_CHANGE);
    }
}

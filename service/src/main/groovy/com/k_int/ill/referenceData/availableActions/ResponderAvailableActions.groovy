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
public class ResponderAvailableActions extends BaseAvailableActions {

    public void load(StateModel stateModel) {
        log.info('Adding available actions for the Responder state model');

        // RES_ITEM_SHIPPED OR "Shipped"
        AvailableAction.ensure(stateModel, Status.RESPONDER_ITEM_SHIPPED, Actions.ACTION_RESPONDER_ISO18626_SHIPPED_RETURN, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_SHIPPED_RETURN_ISO18626);

        // RES_ITEM_RETURNED OR "Return shipped"
        AvailableAction.ensure(stateModel, Status.RESPONDER_ITEM_RETURNED, Actions.ACTION_RESPONDER_SUPPLIER_CHECKOUT_OF_ILL, AvailableAction.TRIGGER_TYPE_MANUAL)

        // RES_OVERDUE OR "Overdue"
        AvailableAction.ensure(stateModel, Status.RESPONDER_OVERDUE, Actions.ACTION_RESPONDER_SUPPLIER_CHECKOUT_OF_ILL, AvailableAction.TRIGGER_TYPE_MANUAL)
        AvailableAction.ensure(stateModel, Status.RESPONDER_OVERDUE, Actions.ACTION_RESPONDER_ISO18626_SHIPPED_RETURN, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.RESPONDER_SHIPPED_RETURN_ISO18626);

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

package com.k_int.ill.referenceData.availableActions;

import com.k_int.ill.statemodel.ActionEventResultList;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.AvailableAction;
import com.k_int.ill.statemodel.StateModel;
import com.k_int.ill.statemodel.Status;

import groovy.util.logging.Slf4j;

/**
 * Creates the available actions for the Digital Returnable Requester state model
 * @author Chas
 *
 */
@Slf4j
public class DigitalReturnableRequesterAvailableActions extends BaseAvailableActions {

    public void load(StateModel stateModel) {
        log.info('Adding available actions for the Digital Returnable Requester state model');

        // REQ_EXPECTS_TO_SUPPLY OR "Expects to supply"
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.DIGITAL_RETURNABLE_REQUESTER_EXPECTS_TO_SUPPLY_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.DIGITAL_RETURNABLE_REQUESTER_EXPECTS_TO_SUPPLY_ISO18626);
        AvailableAction.ensure(stateModel, Status.PATRON_REQUEST_EXPECTS_TO_SUPPLY, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.DIGITAL_RETURNABLE_REQUESTER_EXPECTS_TO_SUPPLY_ISO18626);

        // REQ_LOANED_DIGITALLY
        AvailableAction.ensure(stateModel, Status.REQUESTER_LOANED_DIGITALLY, Actions.ACTION_ISO18626_NOTIFICATION, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.DIGITAL_RETURNABLE_REQUESTER_LOANED_DIGITALLY_ISO18626);
        AvailableAction.ensure(stateModel, Status.REQUESTER_LOANED_DIGITALLY, Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.DIGITAL_RETURNABLE_REQUESTER_LOANED_DIGITALLY_ISO18626);
        AvailableAction.ensure(stateModel, Status.REQUESTER_LOANED_DIGITALLY, Actions.ACTION_REQUESTER_ISO18626_STATUS_CHANGE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.DIGITAL_RETURNABLE_REQUESTER_LOANED_DIGITALLY_ISO18626);
        AvailableAction.ensure(stateModel, Status.REQUESTER_LOANED_DIGITALLY, Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE, AvailableAction.TRIGGER_TYPE_PROTOCOL, ActionEventResultList.DIGITAL_RETURNABLE_REQUESTER_LOANED_DIGITALLY_ISO18626);

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
    }
}

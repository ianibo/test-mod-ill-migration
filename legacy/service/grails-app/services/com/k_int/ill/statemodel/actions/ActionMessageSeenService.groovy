package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestNotification;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Abstract action that marks a message as seen
 * @author Chas
 *
 */
public class ActionMessageSeenService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_MESSAGE_SEEN);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // We must have an id
        if (parameters.id == null) {
            actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
            actionResultDetails.auditMessage = 'No message id supplied to mark as seen';
        } else if (parameters.seenStatus == null) {
            actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
            actionResultDetails.auditMessage = 'No seenStatus supplied to mark as seen';
        } else {
            PatronRequestNotification message = PatronRequestNotification.findById(parameters.id)
            if (message == null) {
                actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
                actionResultDetails.auditMessage = 'Message with id: ' + parameters.id + ' does not exist';
            } else {
                message.seen = parameters.seenStatus;
                message.save(flush:true, failOnError:true);
            }
        }

        actionResultDetails.responseResult.status = (actionResultDetails.result == ActionResult.SUCCESS);
        return(actionResultDetails);
    }
}

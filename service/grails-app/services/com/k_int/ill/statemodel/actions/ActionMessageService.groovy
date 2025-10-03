package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Abstract action that sends a message to the other side of the transaction
 * @author Chas
 *
 */
public class ActionMessageService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_MESSAGE);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // We must have a note
        if (parameters.note == null) {
            actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
            actionResultDetails.auditMessage = 'No note supplied to send';
        } else {
            // Send the message
			actionResultDetails.sendProtocolMessage = true;
            actionResultDetails.auditMessage = 'Message sent: ' + parameters.note;
        }
        return(actionResultDetails);
    }
}

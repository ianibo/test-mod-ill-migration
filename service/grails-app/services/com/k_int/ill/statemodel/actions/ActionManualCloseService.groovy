package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.Status;
import com.k_int.ill.statemodel.StatusService;

/**
 * This action is performed when the user actions the request with Manual Close
 * @author Chas
 *
 */
public class ActionManualCloseService extends AbstractAction {

    StatusService statusService;

    @Override
    String name() {
        return(Actions.ACTION_MANUAL_CLOSE);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        if ((parameters?.terminalState != null) && (parameters.terminalState ==~ /[A-Z_]+/)) {

            // Need to validate the terminal state is legitimate, this way is no longer valid
            Status closeStatus = Status.lookup(parameters.terminalState);

            // Have we been supplied a valid close status
            if (closeStatus && closeStatus.terminal) {
				parameters.note = "The ${request.isRequester ? 'requester' : 'reponder'} has manually closed this request.";
				actionResultDetails.sendProtocolMessage = true;
                actionResultDetails.auditMessage = 'Manually closed';
                actionResultDetails.qualifier = parameters.terminalState;
            } else {
                actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
                actionResultDetails.auditMessage = "Attemped manualClose action with non-terminal state: ${s} ${parameters?.terminalState}";
            }
        } else {
            actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
            actionResultDetails.auditMessage = "Attemped manualClose action with state containing invalid character: ${parameters?.terminalState}";
        }

        // Set the response status
        actionResultDetails.responseResult.status = (actionResultDetails.result == ActionResult.SUCCESS);

        return(actionResultDetails);
    }
}

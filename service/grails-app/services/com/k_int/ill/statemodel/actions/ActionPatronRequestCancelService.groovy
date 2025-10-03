package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Abstract action class that deals with a cancel being requested
 * @author Chas
 *
 */
public abstract class ActionPatronRequestCancelService extends AbstractAction {

    public void sendCancel(PatronRequest request, String action, Map<String, Object> parameters, ActionResultDetails resultDetails) {
        switch (action) {
            case Actions.ACTION_REQUESTER_REQUESTER_REJECT_CONDITIONS:
                request.requestToContinue = true;
                break;

            case Actions.ACTION_REQUESTER_REQUESTER_CANCEL:
                request.requestToContinue = false;
                break;

            default:
                log.error("Action ${action} should not be able to send a cancel message");
                break;
        }

		resultDetails.sendProtocolMessage = true;
//        illActionService.sendRequestingAgencyMessage(request, 'Cancel', parameters, resultDetails);
    }
}

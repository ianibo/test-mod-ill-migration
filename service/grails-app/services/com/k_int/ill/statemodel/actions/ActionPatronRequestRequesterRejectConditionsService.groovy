package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * This action is performed when the requester rejects the conditions
 * @author Chas
 *
 */
public class ActionPatronRequestRequesterRejectConditionsService extends ActionPatronRequestCancelService {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_REQUESTER_REJECT_CONDITIONS);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        sendCancel(request, Actions.ACTION_REQUESTER_REQUESTER_REJECT_CONDITIONS, parameters, actionResultDetails);
        actionResultDetails.auditMessage = 'Rejected loan conditions';

        return(actionResultDetails);
    }
}

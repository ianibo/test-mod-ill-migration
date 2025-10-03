package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * This action performs the Manual Check In action for the requester
 * @author Chas
 *
 */
public class ActionPatronRequestRequesterManualCheckInService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_REQUESTER_MANUAL_CHECKIN);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Just set the status
        actionResultDetails.responseResult.status = true;

        return(actionResultDetails);
    }
}

package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Action that deals with filling the request locally
 * @author Chas
 *
 */
public class ActionPatronRequestFillLocallyService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_FILL_LOCALLY);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Just set the status
        actionResultDetails.responseResult.status = true;

        return(actionResultDetails);
    }
}

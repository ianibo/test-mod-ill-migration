package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Action that means the supplier has received the item back from the requester
 * @author Chas
 *
 */
public class ActionResponderItemReturnedService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_ITEM_RETURNED);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Just mark it as successful
        actionResultDetails.responseResult.status = true;

        return(actionResultDetails);
    }
}

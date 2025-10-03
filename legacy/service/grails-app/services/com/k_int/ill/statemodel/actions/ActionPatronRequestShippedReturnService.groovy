package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.constants.Counter;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * This action is performed when the requester ships the item back to the supplier
 * @author Chas
 *
 */
public class ActionPatronRequestShippedReturnService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_SHIPPED_RETURN);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Decrement the active borrowing counter - we are returning the item
        counterService.decrementCounter(request.institution, Counter.COUNTER_ACTIVE_BORROWING);

		actionResultDetails.sendProtocolMessage = true;
//        illActionService.sendRequestingAgencyMessage(request, 'ShippedReturn', parameters, actionResultDetails);

        actionResultDetails.responseResult.status = true;

        return(actionResultDetails);
    }
}

package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Responder is performing a manual Check Out
 * @author Chas
 *
 */
public class ActionResponderSupplierManualCheckoutService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_MANUAL_CHECKOUT);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Nowt to do
        actionResultDetails.responseResult.status = true;

        return(actionResultDetails);
    }
}

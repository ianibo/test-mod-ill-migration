package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Action that deals with a cannot supply locally
 * @author Chas
 *
 */
public class ActionPatronRequestLocalSupplierCannotSupplyService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_LOCAL_SUPPLIER_CANNOT_SUPPLY);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {

        actionResultDetails.auditMessage = 'Request locally flagged as unable to supply';

        return(actionResultDetails);
    }
}

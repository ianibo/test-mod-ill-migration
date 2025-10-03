package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Supplier has printed the pull slip
 * @author Chas
 *
 */
public class ActionResponderSupplierPrintPullSlipService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_PRINT_PULL_SLIP);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Just set the audit message
        actionResultDetails.auditMessage = 'Pull slip printed';
        actionResultDetails.responseResult.status = true;

        return(actionResultDetails);
    }
}

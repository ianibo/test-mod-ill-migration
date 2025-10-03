package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.codes.closed.Status;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Performed when the responder has said he cannot supply
 * @author Chas
 *
 */
public class ActionResponderSupplierCannotSupplyService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_CANNOT_SUPPLY);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Just send the message of unfilled
		actionResultDetails.sendProtocolMessage = true;
//        illActionService.sendResponse(request, Status.UNFILLED, parameters, actionResultDetails);

        // Now set the  audit message
        actionResultDetails.auditMessage = 'Request manually flagged unable to supply';

        return(actionResultDetails);
    }
}

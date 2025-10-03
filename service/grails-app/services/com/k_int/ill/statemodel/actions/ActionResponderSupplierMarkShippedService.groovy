package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.codes.closed.Status;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Responder has sent the item on its way to the requester
 * @author Chas
 *
 */
public class ActionResponderSupplierMarkShippedService extends ActionResponderService {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_MARK_SHIPPED);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Send the message that it is on its way
		actionResultDetails.sendProtocolMessage = true;
//        illActionService.sendResponse(request, Status.LOANED, parameters, actionResultDetails);
        actionResultDetails.auditMessage = 'Shipped';

        return(actionResultDetails);
    }
}

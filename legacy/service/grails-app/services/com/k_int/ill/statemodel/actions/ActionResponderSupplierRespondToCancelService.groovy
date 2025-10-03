package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Responder is replying to a cancel request from the requester
 * @author Chas
 *
 */
public class ActionResponderSupplierRespondToCancelService extends ActionResponderService {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_RESPOND_TO_CANCEL);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Send the response to the requester
        illActionService.sendSupplierCancelResponse(request, parameters, actionResultDetails);

        // If the cancellation is denied, switch the cancel flag back to false, otherwise send request to complete
        if (parameters?.cancelResponse == 'no') {
            // Set the audit message and qualifier
            actionResultDetails.auditMessage = 'Cancellation denied';
            actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_NO;
        } else {
            actionResultDetails.auditMessage = 'Cancellation accepted';
        }

        return(actionResultDetails);
    }
}

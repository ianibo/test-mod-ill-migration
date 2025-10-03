package com.k_int.ill.statemodel.actions.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.SupplyingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.ReasonForMessage;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that deals with the ISO18626 RequestResponse message
 * @author Chas
 *
 */
public class ActionPatronRequestISO18626RequestResponseService extends ActionISO18626RequesterService {

    @Override
    String name() {
        return(ReasonForMessage.MESSAGE_REASON_REQUEST_RESPONSE);
    }

    @Override
    ActionResultDetails performAction(
        PatronRequest request,
        SupplyingAgencyMessage supplyingAgencyMessage,
        ActionResultDetails actionResultDetails
    ) {
        // Call the base class first
        actionResultDetails = super.performAction(request, supplyingAgencyMessage, actionResultDetails);

        // Only continue if successful
        if (actionResultDetails.result == ActionResult.SUCCESS) {
            // Add an audit entry
            actionResultDetails.auditMessage = 'Request Response message received';
        }

        // Now return the results to the caller
        return(actionResultDetails);
    }
}

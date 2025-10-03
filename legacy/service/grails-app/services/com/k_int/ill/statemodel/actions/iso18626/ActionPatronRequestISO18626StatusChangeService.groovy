package com.k_int.ill.statemodel.actions.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.SupplyingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.ReasonForMessage;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that deals with the ISO18626 StatusChange message
 * @author Chas
 *
 */
public class ActionPatronRequestISO18626StatusChangeService extends ActionISO18626RequesterService {

    @Override
    String name() {
        return(ReasonForMessage.MESSAGE_REASON_STATUS_CHANGE);
    }

    @Override
    ActionResultDetails performAction(
        PatronRequest request,
        SupplyingAgencyMessage supplyingAgencyMessage,
        ActionResultDetails actionResultDetails
    ) {
        // We have a hack where we use this  message to verify that the last one sent was actually received or not
        if (!checkForLastSequence(request, supplyingAgencyMessage.messageInfo?.note, actionResultDetails)) {
            // A normal message
            // Call the base class first
            actionResultDetails = super.performAction(request, supplyingAgencyMessage, actionResultDetails);

            // Only continue if successful
            if (actionResultDetails.result == ActionResult.SUCCESS) {
                // Add an audit entry
                actionResultDetails.auditMessage = 'Status Change message received';
            }
        }

        // Now return the results to the caller
        return(actionResultDetails);
    }
}

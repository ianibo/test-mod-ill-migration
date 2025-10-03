package com.k_int.ill.statemodel.actions.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.RequestingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.Action;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that deals with the ISO18626 Shipped Return message
 * @author Chas
 *
 */
public class ActionResponderISO18626ShippedReturnService extends ActionISO18626ResponderService {

    @Override
    String name() {
        return(Action.SHIPPED_RETURN);
    }

    ActionResultDetails performAction(
        PatronRequest request,
        RequestingAgencyMessage requestingAgencyMessage,
        ActionResultDetails actionResultDetails
    ) {
        // Process the note
        processNote(request, requestingAgencyMessage, requestingAgencyMessage.findNote(), actionResultDetails);

        // Call the base class
        if (actionResultDetails.result == ActionResult.SUCCESS) {
            // Set the audit message
            actionResultDetails.auditMessage = 'Item(s) Returned by requester';

            // Set the items waiting to be checked back in
            request.volumes?.each { vol ->
                vol.status = vol.lookupStatus('awaiting_lms_check_in');
            }
        }

        // Now return the result to the caller
        return(actionResultDetails);
    }
}

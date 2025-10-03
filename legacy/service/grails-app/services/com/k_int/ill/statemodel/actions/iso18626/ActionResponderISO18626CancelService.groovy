package com.k_int.ill.statemodel.actions.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.RequestingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.Action;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that deals with the ISO18626 Received message
 * @author Chas
 *
 */
public class ActionResponderISO18626CancelService extends ActionISO18626ResponderService {

    @Override
    String name() {
        return(Action.CANCEL);
    }

    ActionResultDetails performAction(
        PatronRequest request,
        RequestingAgencyMessage requestingAgencyMessage,
        ActionResultDetails actionResultDetails
    ) {
        // Process the note
        processNote(request, requestingAgencyMessage, requestingAgencyMessage.findNote(), actionResultDetails);

        // Were we successful
        if (actionResultDetails.result == ActionResult.SUCCESS) {
            // Just set the audit message
            actionResultDetails.auditMessage = 'Requester requested cancellation of the request';
        }

        // Now return the result to the caller
        return(actionResultDetails);
    }
}

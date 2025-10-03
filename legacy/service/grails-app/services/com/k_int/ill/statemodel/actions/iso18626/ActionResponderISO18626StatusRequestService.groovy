package com.k_int.ill.statemodel.actions.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.RequestingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.Action;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that deals with the ISO18626 Received message
 * @author Chas
 *
 */
public class ActionResponderISO18626StatusRequestService extends ActionISO18626ResponderService {

    @Override
    String name() {
        return(Action.STATUS_REQUEST);
    }

    ActionResultDetails performAction(
        PatronRequest request,
        RequestingAgencyMessage requestingAgencyMessage,
        ActionResultDetails actionResultDetails
    ) {
        // We have a hack where we use this  message to verify that the last one sent was actually received or not
        if (!checkForLastSequence(request, requestingAgencyMessage.findNote(), actionResultDetails)) {
            // Not implemented yet, placeholder for when it is, rather oddly we need to send another message, which shouldn't be a problem
        }

        // Now return the result to the caller
        return(actionResultDetails);
    }
}

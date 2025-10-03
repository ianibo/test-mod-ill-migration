package com.k_int.ill.statemodel.actions.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestService;
import com.k_int.ill.iso18626.RequestingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.Action;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that deals with the ISO18626 Received message
 * @author Chas
 *
 */
public class ActionResponderISO18626ReceivedService extends ActionISO18626ResponderService {

	PatronRequestService patronRequestService;
	
    @Override
    String name() {
        return(Action.RECEIVED);
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
            actionResultDetails.auditMessage = 'Shipment received by requester';

			// If it is a copy request then we want to go to a slightly different action			
			if (patronRequestService.isCopy(request)) {
				// It is a copy request, so set the qualifier
				actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_COPY;
			}
        }

        // Now return the result to the caller
        return(actionResultDetails);
    }
}

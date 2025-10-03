package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.codes.closed.Status;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Performs an answer will supply action for the responder
 * @author Chas
 *
 */
public class ActionResponderRespondYesService extends ActionResponderService {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_RESPOND_YES);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Check the pickup location and route
        if (validatePickupLocationAndRoute(request, parameters, actionResultDetails).result == ActionResult.SUCCESS) {
            // Status is set to Status.RESPONDER_NEW_AWAIT_PULL_SLIP in validatePickupLocationAndRoute
			actionResultDetails.sendProtocolMessage = true;
//            illActionService.sendResponse(request, Status.EXPECT_TO_SUPPLY, parameters, actionResultDetails);
        }

        return(actionResultDetails);
    }
}

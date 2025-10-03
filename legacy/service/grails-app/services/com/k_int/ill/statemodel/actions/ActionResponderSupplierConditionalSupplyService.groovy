package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.codes.closed.Status;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * This action is when the responder is applying conditions before they will supply
 * @author Chas
 *
 */
public class ActionResponderSupplierConditionalSupplyService extends ActionResponderConditionService {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_CONDITIONAL_SUPPLY);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Check the pickup location and route
        if (validatePickupLocationAndRoute(request, parameters, actionResultDetails).result == ActionResult.SUCCESS) {
			actionResultDetails.sendProtocolMessage = true;
//            illActionService.sendResponse(request, Status.EXPECT_TO_SUPPLY, parameters, actionResultDetails);
            sendSupplierConditionalWarning(request, parameters, actionResultDetails);

            if (!parameters.containsKey('holdingState') || parameters.holdingState == 'no') {
                // The supplying agency wants to continue with the request
                actionResultDetails.auditMessage = 'Request responded to conditionally, request continuing';
            // Status is set to Status.RESPONDER_NEW_AWAIT_PULL_SLIP in validatePickupLocationAndRoute
            } else {
                // The supplying agency wants to go into a holding state
                // In this case we want to "pretend" the previous state was actually the next one, for later when it looks up the previous state
                actionResultDetails.auditMessage = 'Request responded to conditionally, placed in hold state';
                actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_HOLDING;
            }
        }

        return(actionResultDetails);
    }
}

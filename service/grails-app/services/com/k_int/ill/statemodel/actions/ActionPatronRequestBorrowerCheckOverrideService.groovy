package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Performs a check against the host LMS to validate the patron, overriding to be a valid patron ...
 * @author Chas
 *
 */
public class ActionPatronRequestBorrowerCheckOverrideService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_BORROWER_CHECK_OVERRIDE);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        Map localParameters = parameters;
        localParameters.override = true;

        // Perform the borrower check
        Map borrowerCheck = illActionService.lookupPatron(request, localParameters);

        // borrowerCheck.patronValid should ALWAYS be true in this action
        actionResultDetails.responseResult.status = borrowerCheck?.callSuccess
        if (!actionResultDetails.responseResult.status) {
            // The Host LMS check call has failed, stay in current state
            request.needsAttention = true;
            actionResultDetails.auditMessage = 'Host LMS integration: lookupPatron call failed. Review configuration and try again or deconfigure host LMS integration in settings. ' + borrowerCheck?.problems;
            actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_HOST_LMS_CALL_FAILED;
        }

        return(actionResultDetails);
    }
}

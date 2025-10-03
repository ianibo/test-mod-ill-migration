package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * This action checks whether the borrower is valid or not
 * @author Chas
 *
 */
public class ActionPatronRequestBorrowerCheckService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_BORROWER_CHECK);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Performs a lookup against the patron
        Map borrowerCheck = illActionService.lookupPatron(request, parameters);
        actionResultDetails.responseResult.status = borrowerCheck?.callSuccess && borrowerCheck?.patronValid
        if (actionResultDetails.responseResult.status) {
            // Call succeeded and the patron is valid, nothing to do
        } else if (!borrowerCheck?.callSuccess) {
            // The Host LMS check call has failed, stay in current state
            request.needsAttention = true;
            actionResultDetails.result = ActionResult.ERROR;
            actionResultDetails.auditMessage = 'Host LMS integration: lookupPatron call failed. Review configuration and try again or deconfigure host LMS integration in settings. ' + borrowerCheck?.problems;
            actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_HOST_LMS_CALL_FAILED;
        } else {
            // The call succeeded but patron is invalid
            actionResultDetails.result = ActionResult.ERROR;
            actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_INVALID_PATRON;
            String errors = (borrowerCheck?.problems == null) ? '' : (' (Errors: ' + borrowerCheck.problems + ')');
            String status = borrowerCheck?.status == null ? '' : (' (Patron state = ' + borrowerCheck.status + ')');
            actionResultDetails.auditMessage = "Failed to validate patron with id: \"${request.patronIdentifier}\".${status}${errors}";
            request.needsAttention = true;
        }

        return(actionResultDetails);
    }
}

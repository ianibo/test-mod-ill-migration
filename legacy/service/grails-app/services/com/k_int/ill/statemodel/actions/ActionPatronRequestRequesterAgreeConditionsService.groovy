package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestLoanCondition;
import com.k_int.ill.iso18626.NoteSpecials;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * This action performs the agreeing of conditions by the requester
 * @author Chas
 *
 */
public class ActionPatronRequestRequesterAgreeConditionsService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_REQUESTER_AGREE_CONDITIONS);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // If we are not the requester, flag it as an error
        if (request.isRequester) {
            if (parameters.containsKey('note')) {
                parameters.note = NoteSpecials.AGREE_LOAN_CONDITION + " ${parameters.note}";
            } else {
                parameters.note = NoteSpecials.AGREE_LOAN_CONDITION;
            }

            // Inform the responder
			actionResultDetails.sendProtocolMessage = true;
//            illActionService.sendRequestingAgencyMessage(request, 'Notification', parameters, actionResultDetails);

            PatronRequestLoanCondition[] conditions = PatronRequestLoanCondition.findAllByPatronRequestAndRelevantSupplier(request, request.resolvedSupplier);
            conditions.each { condition ->
                condition.accepted = true;
                condition.save(flush: true, failOnError: true);
            }

            actionResultDetails.auditMessage = 'Agreed to loan conditions';
        } else {
            actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
            actionResultDetails.auditMessage = 'Only the responder can accept the conditions';
        }

        return(actionResultDetails);
    }
}

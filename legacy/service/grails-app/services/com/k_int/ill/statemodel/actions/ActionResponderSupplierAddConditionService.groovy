package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.NoteSpecials;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Executes adding a condition on to the request for the responder
 * @author Chas
 *
 */
public class ActionResponderSupplierAddConditionService extends ActionResponderConditionService {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_ADD_CONDITION);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Add the condition and send it to the requester
        Map conditionParams = parameters

        if (parameters.containsKey('note')) {
            conditionParams.note = NoteSpecials.ADD_LOAN_CONDITION + " ${parameters.note}"
        } else {
            conditionParams.note = NoteSpecials.ADD_LOAN_CONDITION;
        }

        if (conditionParams.containsKey('loanCondition')) {
	        // Send over the supplier conditional warning
	        sendSupplierConditionalWarning(request, parameters, actionResultDetails);
        } else {
            log.warn('addCondition not handed any conditions');
        }

        // Do we need to hold the request
        if (!parameters.containsKey('holdingState') || parameters.holdingState == 'no') {
            // The supplying agency wants to continue with the request
            actionResultDetails.auditMessage = 'Added loan condition to request, request continuing';
        } else {
            // The supplying agency wants to go into a holding state
            actionResultDetails.auditMessage = 'Condition added to request, placed in hold state';
            actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_HOLDING
        }
        return(actionResultDetails);
    }
}

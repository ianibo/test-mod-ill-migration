package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;
import com.k_int.web.toolkit.refdata.RefdataCategory;
import com.k_int.web.toolkit.refdata.RefdataValue;

/**
 * Executes the actions action for the requester cancelling a request
 * @author Chas
 *
 */
public class ActionPatronRequestRequesterCancelService extends ActionPatronRequestCancelService {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_REQUESTER_CANCEL);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Do we have a reason
        if (parameters.reason) {
            RefdataCategory cat = RefdataCategory.findByDesc(RefdataValueData.VOCABULARY_CANCELLATION_REASONS);
            RefdataValue val = RefdataValue.findByOwnerAndValue(cat, parameters.reason);
            if (val) {
                request.cancellationReason = val;
            }
        }

        // If we do not already have a resolved supplier in hand we cannot send ISO18626 messages
        if (request.resolvedSupplier?.id) {
            sendCancel(request, Actions.ACTION_REQUESTER_REQUESTER_CANCEL, parameters, actionResultDetails);
        } else {
            // In this case, set the qualifier to no supplier
            actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_NO_SUPPLIER;
        }

        return(actionResultDetails);
    }
}

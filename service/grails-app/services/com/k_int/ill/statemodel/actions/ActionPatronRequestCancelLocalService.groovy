package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;
import com.k_int.web.toolkit.refdata.RefdataCategory;
import com.k_int.web.toolkit.refdata.RefdataValue;

/**
 * Action class that deals with the patron requesting a local cancel
 * @author Chas
 *
 */
public class ActionPatronRequestCancelLocalService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_CANCEL_LOCAL);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        actionResultDetails.auditMessage = 'Local request cancelled';
        if (parameters.reason) {
            RefdataCategory cat = RefdataCategory.findByDesc(RefdataValueData.VOCABULARY_CANCELLATION_REASONS);
            RefdataValue reason = RefdataValue.findByOwnerAndValue(cat, parameters.reason);
            if (reason) {
                request.cancellationReason = reason;
                actionResultDetails.auditMessage += ": ${reason}";
            }
        }

        return(actionResultDetails);
    }
}

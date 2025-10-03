package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Requester has agreed to the conditions, which is being manually marked by the responder
 * @author Chas
 *
 */
public class ActionResponderSupplierMarkConditionsAgreedService extends ActionResponderService {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_MARK_CONDITIONS_AGREED);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Mark all conditions as accepted
        illApplicationEventHandlerService.markAllLoanConditionsAccepted(request)

        actionResultDetails.auditMessage = 'Conditions manually marked as agreed';
        return(actionResultDetails);
    }
}

package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest
import com.k_int.ill.iso18626.codes.closed.ReasonForMessage;
import com.k_int.ill.statemodel.AbstractAction
import com.k_int.ill.statemodel.ActionResultDetails
import com.k_int.ill.statemodel.Actions

/**
 * Fill a loan digitally
 *
 */
public class ActionResponderFillDigitalLoanService extends AbstractAction {

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_FILL_DIGITAL_LOAN);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
		actionResultDetails.sendProtocolMessage = true;
//        illActionService.sendSupplyingAgencyMessage(request, ReasonForMessage.MESSAGE_REASON_STATUS_CHANGE, 'Loaned', [deliveredFormat: 'URL', *:parameters], actionResultDetails);
        actionResultDetails.auditMessage = 'Loaned digitally';

        return(actionResultDetails);
    }
}

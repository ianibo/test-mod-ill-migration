package com.k_int.ill.statemodel.actions.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.SupplyingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.ErrorCode;
import com.k_int.ill.iso18626.codes.closed.ReasonForMessage;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that deals with the ISO18626 Notification message
 * @author Chas
 *
 */
public class ActionPatronRequestISO18626CancelResponseService extends ActionISO18626RequesterService {

    @Override
    String name() {
        return(ReasonForMessage.MESSAGE_REASON_CANCEL_RESPONSE);
    }

    @Override
    ActionResultDetails performAction(
        PatronRequest request,
        SupplyingAgencyMessage supplyingAgencyMessage,
        ActionResultDetails actionResultDetails
    ) {
        // Must have an wnswerYesNo field
        if (supplyingAgencyMessage.messageInfo?.answerYesNo == null) {
            actionResultDetails.result == ActionResult.ERROR;
            actionResultDetails.responseResult.errorType = ErrorCode.NO_CANCEL_VALUE;
        } else {
            // Call the base class first
            actionResultDetails = super.performAction(request, supplyingAgencyMessage, actionResultDetails);

            // Only continue if we were successful
            if (actionResultDetails.result == ActionResult.SUCCESS) {
                // Ensure we are dealing with a string and that it is a case we are expecting
                switch (supplyingAgencyMessage.messageInfo.answerYesNo.toString().toUpperCase()) {
                    case 'Y':
                        // The cancel response ISO18626 message should contain a status of "Cancelled", and so this case will be handled by handleStatusChange
                        actionResultDetails.auditMessage = 'Cancelled allowed by supplier.';
                        break;

                    case 'N':
                        // Is this always the correct way of doing it ?
                        actionResultDetails.auditMessage = 'Supplier denied cancellation.';
                        actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_NO;
                        break;

                    default:
                        actionResultDetails.result == ActionResult.ERROR;
                        actionResultDetails.responseResult.errorType = ErrorCode.INVALID_CANCEL_VALUE;
                        actionResultDetails.responseResult.errorValue = supplyingAgencyMessage.messageInfo.answerYesNo.toString();
                        break;
                }
            }
        }

        // Now just call the base class
        return(actionResultDetails);
    }
}

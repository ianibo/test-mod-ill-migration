package com.k_int.ill.statemodel.actions;

import com.k_int.directory.DirectoryEntryService;
import com.k_int.ill.HostLmsService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Action that occurs when the responder checks the item into ill from the LMS
 * @author Ethan Freestone
 */
public class ActionResponderSupplierCheckInToIllAndSupplierMarkShippedService extends ActionResponderService {

    private static final String REASON_SPOOFED = 'spoofed';

    ActionResponderSupplierCheckInToIllService actionResponderSupplierCheckInToIllService;
    ActionResponderSupplierMarkShippedService actionResponderSupplierMarkShippedService;
    HostLmsService hostLmsService;
    DirectoryEntryService directoryEntryService;

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_CHECK_INTO_ILL_AND_MARK_SHIPPED);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Create ourselves an ActionResultDetails that we will pass to each of the actions we want to call
        ActionResultDetails resultDetails = new ActionResultDetails();

        // Default the result as being a success
        resultDetails.result = ActionResult.SUCCESS;

        String returnAuditMessage;

        if (actionResponderSupplierCheckInToIllService.performAction(request, parameters, resultDetails).result == ActionResult.SUCCESS) {

            // Store auditMessage from check in call in case of success
            if (resultDetails.auditMessage) {
                returnAuditMessage = "Combined action. Check in success: ${resultDetails.auditMessage}"

                // Unset auditMessage on resultDetails so we don't get the same audit message twice
                resultDetails.auditMessage = null;
            }

            // Now we can mark it as being shipped
            if (actionResponderSupplierMarkShippedService.performAction(request, parameters, resultDetails).result == ActionResult.SUCCESS) {
                // Its a success, so copy in the response result
                actionResultDetails.responseResult = resultDetails.responseResult;
				actionResultDetails.sendProtocolMessage = resultDetails.sendProtocolMessage;
				
                // Store auditMessage from check in call in case of success
                if (resultDetails.auditMessage && returnAuditMessage) {
                    returnAuditMessage += " Mark item shipped success: ${resultDetails.auditMessage}.";
                } else if (resultDetails.auditMessage) {
                    returnAuditMessage = "Combined action. Mark item shipped success: ${resultDetails.auditMessage}."
                }
            } else {
                actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_CHECKED_IN
            }
        }

        // At least one of our two calls failed
        if (resultDetails.result != ActionResult.SUCCESS) {
            // Failed so copy back the appropriate details so it can be diagnosed
            actionResultDetails.responseResult = resultDetails.responseResult;
            actionResultDetails.responseResult = resultDetails.responseResult;
            actionResultDetails.result = resultDetails.result;

            // Audit trail is slightly different. We might have success information from the first call
            if (resultDetails.auditMessage) {
                if (returnAuditMessage) {
                    // The first call must have succeeded, so the second call has failed
                    returnAuditMessage += " Mark item shipped failed: ${resultDetails.auditMessage}"
                } else {
                    // We don't know what has failed, only that something has
                    returnAuditMessage = "Combined action. Failure: ${resultDetails.auditMessage}"
                }
            }
        }

        // Even if both succeeded we may want audit message
        if (returnAuditMessage) {
            actionResultDetails.auditMessage = returnAuditMessage;
        }

        return(actionResultDetails);
    }
}

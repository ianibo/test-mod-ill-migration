package com.k_int.ill.statemodel.actions;

import com.k_int.directory.DirectoryEntryService;
import com.k_int.ill.HostLmsService
import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.codes.closed.Status;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;

/**
 * Requester has returned the item so we therefore need to check it out of ill
 * @author Chas
 *
 */
public class ActionResponderSupplierCheckOutOfIllService extends AbstractAction {

    HostLmsService hostLmsService;
    DirectoryEntryService directoryEntryService;

    @Override
    String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_CHECKOUT_OF_ILL);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        Map resultMap = [:];
        try {
            resultMap = hostLmsService.checkInRequestVolumes(request);
        }
        catch (Exception e) {
            log.error('NCIP Problem', e);
            request.needsAttention = true;
            illApplicationEventHandlerService.auditEntry(
                request,
                request.state,
                request.state,
                "Host LMS integration: NCIP CheckinItem call failed for volumes in request: ${request.id}. Review configuration and try again or deconfigure host LMS integration in settings. " + e.message,
                null);
            resultMap.result = false;
        }

        if (resultMap.result == false) {
            actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
            actionResultDetails.auditMessage = 'NCIP CheckinItem call failed';
            actionResultDetails.responseResult.code = -3; // NCIP action failed
            actionResultDetails.responseResult.message = actionResultDetails.auditMessage;
            actionResultDetails.responseResult.status = false;
        } else {
            log.debug('supplierCheckOutOfIll::transition and send status change');
            if (!parameters?.undo) {
                // We are not performing an undo of the checkInToIll action
				actionResultDetails.sendProtocolMessage = true;
//                illActionService.sendStatusChange(request, Status.LOAN_COMPLETED, actionResultDetails, parameters?.note);
            }
            actionResultDetails.responseResult.status = true;
        }

        return(actionResultDetails);
    }
}

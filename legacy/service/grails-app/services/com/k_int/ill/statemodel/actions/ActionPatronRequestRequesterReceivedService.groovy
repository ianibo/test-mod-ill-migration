package com.k_int.ill.statemodel.actions;

import com.k_int.ill.HostLmsService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestService;
import com.k_int.ill.RequestVolume;
import com.k_int.ill.constants.Counter;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;
import com.k_int.web.toolkit.refdata.RefdataValue;

/**
 * The requester has received the item
 * @author Chas
 *
 */
public class ActionPatronRequestRequesterReceivedService extends AbstractAction {

    private static final String VOLUME_STATUS_AWAITING_TEMPORARY_ITEM_CREATION = 'awaiting_temporary_item_creation';

    private static final String REASON_SPOOFED = 'spoofed';

    HostLmsService hostLmsService;
	PatronRequestService patronRequestService;
	
    @Override
    public String name() {
        return(Actions.ACTION_REQUESTER_REQUESTER_RECEIVED);
    }

    @Override
    public ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
		// Not a lot to do if this is regarded as a copy
		if (patronRequestService.isCopy(request)) {
			// Mark the request as not requiring attention
			request.needsAttention = false;
			actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_COPY;
		} else {
			// We are treating it as a loan
			receivedLoan(request, parameters, actionResultDetails);
		}

		// If the request does not require attention, let the supplier know we have received it		
		if (!request.needsAttention) {
			// Request is in a good state
			actionResultDetails.sendProtocolMessage = true;
//			illActionService.sendRequestingAgencyMessage(request, 'Received', parameters, actionResultDetails);
		}
		
		// return the result of the call
		return(actionResultDetails);
	}
	
    private void receivedLoan(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        boolean ncipResult = false;

        // Increment the active borrowing counter
        counterService.incrementCounter(request.institution, Counter.COUNTER_ACTIVE_BORROWING);

        // Check the item in to the local LMS
        RequestVolume[] volumesWithoutTemporaryItem = request.volumes.findAll { rv ->
            rv.status.value == VOLUME_STATUS_AWAITING_TEMPORARY_ITEM_CREATION
        }
        // Iterate over volumes without temp item in for loop so we can break out if we need to
        for (RequestVolume vol : volumesWithoutTemporaryItem) {
            try {
                // Call the host lms to check the item out of the host system and in to ill
                Map acceptResult = hostLmsService.acceptItem(
                    request,
                    vol.temporaryItemBarcode,
                    null
                );

                if (acceptResult?.result == true) {
                    // Let the user know if the success came from a real call or a spoofed one
                    String message = "Receive succeeded for item id: ${vol.itemId} (temporaryItemBarcode: ${vol.temporaryItemBarcode}). ${acceptResult.reason == REASON_SPOOFED ? '(No host LMS integration configured for accept item call)' : 'Host LMS integration: AcceptItem call succeeded.'}";
                    RefdataValue newVolState = acceptResult.reason == REASON_SPOOFED ? vol.lookupStatus('temporary_item_creation_(no_integration)') : vol.lookupStatus('temporary_item_created_in_host_lms');

                    illApplicationEventHandlerService.auditEntry(request,
                        request.state,
                        request.state,
                        message,
                        null);

                    log.debug("State for volume ${vol.itemId} set to ${newVolState}");
                    vol.status = newVolState;
                    vol.save(failOnError: true);
                } else {
                    String message = "Host LMS integration: NCIP AcceptItem call failed for temporary item barcode: ${vol.temporaryItemBarcode}. Review configuration and try again or deconfigure host LMS integration in settings. ";
                    // PR-658 wants us to set some state here but doesn't say what that state is. Currently we leave the state as is.
                    // IF THIS NEEDS TO GO INTO ANOTHER STATE, WE SHOULD DO IT AFTER ALL VOLS HAVE BEEN ATTEMPTED
                    illApplicationEventHandlerService.auditEntry(request,
                        request.state,
                        request.state,
                        message + acceptResult?.problems,
                        null);
                }
            } catch (Exception e) {
                log.error('NCIP Problem', e);
                illApplicationEventHandlerService.auditEntry(request, request.state, request.state, "Host LMS integration: NCIP AcceptItem call failed for temporary item barcode: ${vol.temporaryItemBarcode}. Review configuration and try again or deconfigure host LMS integration in settings. " + e.message, null);
            }
        }
        request.save(flush:true, failOnError:true);

        // At this point we should have all volumes' temporary items created. Check that again
        volumesWithoutTemporaryItem = request.volumes.findAll { rv ->
            rv.status.value == VOLUME_STATUS_AWAITING_TEMPORARY_ITEM_CREATION
        }

        if (volumesWithoutTemporaryItem.size() == 0) {
            // Let the user know if the success came from a real call or a spoofed one
            actionResultDetails.auditMessage = 'Host LMS integration: AcceptItem call succeeded for all items.';

            request.needsAttention = false;
            ncipResult = true;
        } else {
            actionResultDetails.auditMessage = 'Host LMS integration: AcceptItem call failed for some items.';
            request.needsAttention = true;
        }

        // Take into account if we failed on the ncip message
        if (!ncipResult) {
            actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
            if (actionResultDetails.auditMessage != null) {
                actionResultDetails.auditMessage = 'NCIP AcceptItem call failed';
            }
            actionResultDetails.responseResult.code = -3; // NCIP action failed
            actionResultDetails.responseResult.message = actionResultDetails.auditMessage;
        }
    }
}

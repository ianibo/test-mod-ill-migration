package com.k_int.ill.statemodel.actions.iso18626;

import java.util.regex.Matcher;

import com.k_int.directory.Symbol;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.RequestVolume;
import com.k_int.ill.iso18626.ExtractedNoteFieldResult;
import com.k_int.ill.iso18626.NoteSpecials;
import com.k_int.ill.iso18626.SupplyingAgencyMessage;
import com.k_int.ill.iso18626.codes.open.LoanCondition;
import com.k_int.ill.iso18626.types.StatusInfo;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that deals with interpreting ISO18626 on the requester side
 * @author Chas
 *
 */
public abstract class ActionISO18626RequesterService extends ActionISO18626Service {

    private static final String VOLUME_STATUS_AWAITING_TEMPORARY_ITEM_CREATION = 'awaiting_temporary_item_creation';

    ActionResultDetails performAction(
        PatronRequest request,
        SupplyingAgencyMessage supplyingAgencyMessage,
        ActionResultDetails actionResultDetails
    ) {
        // Grab hold of the statusInfo as we may want to override it
        StatusInfo statusInfo = supplyingAgencyMessage.statusInfo;
        String statusInfoStatus = statusInfo?.status;

        // Extract the sequence from the note
        Map sequenceResult = iso18626NotesService.extractSequenceFromNote(supplyingAgencyMessage.messageInfo?.note);
        String note = sequenceResult.note;
        request.lastSequenceReceived = sequenceResult.sequence;


        // if supplyingAgencyMessage.deliveryInfo.itemId then we should stash the item id
        if (supplyingAgencyMessage.deliveryInfo) {
            if (supplyingAgencyMessage.deliveryInfo?.loanCondition) {
                // Are we in a valid state for loan conditions ?
                log.debug("Loan condition found: ${supplyingAgencyMessage.deliveryInfo?.loanCondition?.toString()}")
                statusInfoStatus = 'Conditional';

                // Save the loan condition to the patron request
                List<String> loanConditions = supplyingAgencyMessage.deliveryInfo?.loanCondition;
                Symbol relevantSupplier = illApplicationEventHandlerService.resolveSymbol(supplyingAgencyMessage.header.supplyingAgencyId.agencyIdType.code, supplyingAgencyMessage.header.supplyingAgencyId.agencyIdValue);
				if (loanConditions != null)  {
					loanConditions.each { LoanCondition loanCondition ->
						illApplicationEventHandlerService.addLoanConditionToRequest(request, loanCondition.code, relevantSupplier, note);
					}
				}
            }

            // We have itemId as being a list so we now go through the same route regardless of whether we have 1 or many
            List<String> itemIds = supplyingAgencyMessage?.deliveryInfo?.itemId;
            if (itemIds) {
                // Item ids coming in, handle those
                itemIds.each { iid ->
					// If we're being told about the barcode of the selected item (and we don't already have one saved), stash it in selectedItemBarcode on the requester side
		            if (!request.selectedItemBarcode) {
						// We just take the first one supplied
		                request.selectedItemBarcode = iid;
		            }

					// See whether it is a multi volume one of a particular format 
                    Matcher matcher = iid =~ /multivol:(.*),((?!\s*$).+)/;
					String volumeId = iid;
					String volumeName = null;
					
                    if (matcher.size() > 0) {
                        // At this point we have an itemId of the form "multivol:<name>,<id>"
                        volumeId = matcher[0][2];
                        volumeName = matcher[0][1];
					}

					// Now add the volume					
					addVolume(request, volumeId, volumeName);
                }
            }

            // If the deliveredFormat is URL and a URL is present, store it on the request
            if (supplyingAgencyMessage.deliveryInfo?.deliveredFormat == 'URL') {
                ExtractedNoteFieldResult extractedUrlResult = iso18626NotesService.extractFieldFromNote(note, NoteSpecials.DELIVERY_URL);
                if (extractedUrlResult.data != null) {
                    request.pickupURL = extractedUrlResult.data;
                    note = extractedUrlResult.note;
                }
            }
        }

        // If there is a note, create notification entry
        if (note) {
            incomingNotificationEntry(
                request,
                supplyingAgencyMessage,
                note
            );
        }

        // Is there a due date
        if (statusInfo?.dueDate) {
            request.dueDateRS = statusInfo.dueDate;
            try {
                request.parsedDueDateRS = illActionService.parseDateString(request.dueDateRS);
            } catch (Exception e) {
                log.warn("Unable to parse ${request.dueDateRS} to date: ${e.getMessage()}");
            }
        }

        // Deal with the status
        handleStatusChange(request, statusInfoStatus, actionResultDetails);

        return(actionResultDetails);
    }

	protected void addVolume(PatronRequest request, String itemId, String itemName) {
		// Check if a RequestVolume exists for this itemId, and if not, create one
		RequestVolume rv = request.volumes.find { RequestVolume rv -> rv.itemId == itemId };
		if (!rv) {
			rv = new RequestVolume(
				name: itemName ?: request.volume ?: itemId,
				itemId: itemId,
				status: RequestVolume.lookupStatus(VOLUME_STATUS_AWAITING_TEMPORARY_ITEM_CREATION)
			);

			request.addToVolumes(rv);

			/*
				This _should_ be handled on the following save,
				but there seems to not be an intial save which
				adds the temporary barcode necessary for acceptItem.
				Since these are added sequentially, in known multivol cases
				we can enforce the multivolume rule so that the first item
				does not rely on `volumes.size() > 1`
			*/
			rv.temporaryItemBarcode = rv.generateTemporaryItemBarcode(true)
		}
	}

    // ISO18626 states are RequestReceived, ExpectToSupply, WillSupply, Loaned Overdue, Recalled, RetryPossible, Unfilled, CopyCompleted, LoanCompleted, CompletedWithoutReturn and Cancelled
    protected void handleStatusChange(PatronRequest request, String status, ActionResultDetails actionResultDetails) {
        log.debug("handleStatusChange(${request.id},${status})");

        if (status) {
            // Set the qualifier on the result
            actionResultDetails.qualifier = status;
        }
    }
}

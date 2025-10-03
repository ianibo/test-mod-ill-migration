package com.k_int.ill.statemodel.actions.iso18626;

import java.time.ZonedDateTime;

import com.k_int.ill.IllApplicationEventHandlerService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestNotification;
import com.k_int.ill.iso18626.RequestingAgencyMessage;
import com.k_int.ill.iso18626.SupplyingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.ErrorCode;
import com.k_int.ill.iso18626.codes.closed.Status;
import com.k_int.ill.iso18626.complexTypes.AgencyId;
import com.k_int.ill.protocols.iso18626.Iso18626NotesService;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that has the general methods used by both the requester and responder
 * @author Chas
 *
 */
public abstract class ActionISO18626Service extends AbstractAction {

    IllApplicationEventHandlerService illApplicationEventHandlerService;
	Iso18626NotesService iso18626NotesService;

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Should not come through here
		log.error("******************");
		log.error("*** Coming through ActionISO18626Service::performAction when this should not be possible");
		log.error("******************");
		return(actionResultDetails);
    }

    /**
     * This methods checks for our hack to see if the caller wants to verify that they received our last message
     * @param request The request that we are acting upon
     * @param note The note we need to extract the last sequence from
     * @param actionResultDetails The result details that need updating if it is our hack
     * @return true if it is our hack for checking if the last message was received, otherwise false if it is a normal message
     */
    public boolean checkForLastSequence(PatronRequest request, String note, ActionResultDetails actionResultDetails) {
        boolean result = false;
        Map noteParts = iso18626NotesService.extractLastSequenceFromNote(note);

        // Are they wanting us to check as to whether we received their last message or not
        if (noteParts.sequence != null) {
            // This is our hack to see if their last message was received, everything else in the message is ignored
            result = true;

            // So we need to see if the last sequence we received is the same one specified in the note
            if (request.lastSequenceReceived == null) {
                // We havn't previously received a sequence
                actionResultDetails.result = ActionResult.ERROR;
                actionResultDetails.responseResult.errorType = 'NoPreviousMessage';
            } else if (request.lastSequenceReceived.equals(noteParts.sequence)) {
                // The last one they sent, was the last one we received
                actionResultDetails.result = ActionResult.SUCCESS;

                // We also set the error value to be the last received sequence, so that we know that the response is from our hack
                actionResultDetails.responseResult.errorType = ErrorCode.NO_ERROR;
                actionResultDetails.responseResult.errorValue = request.lastSequenceReceived.toString();
            } else {
                // We are out of sequence with the Responder, so return an error
                actionResultDetails.result = ActionResult.ERROR;
                actionResultDetails.responseResult.errorType = 'SequenceDifferent';
                actionResultDetails.responseResult.errorValue = request.lastSequenceReceived.toString();
            }
        }

        // Let the caller know if it was our hack
        return(result);
    }

    protected void incomingNotificationEntry(
        PatronRequest patronRequest,
        RequestingAgencyMessage requestingAgencyMessage,
        String note
    ) {
        incomingNotificationEntry(
            patronRequest,
            requestingAgencyMessage.header.timestamp,
            requestingAgencyMessage.header.supplyingAgencyId,
            requestingAgencyMessage.header.requestingAgencyId,
            note,
            requestingAgencyMessage.findActionCode()
        );
    }

    protected void incomingNotificationEntry(
        PatronRequest patronRequest,
        SupplyingAgencyMessage supplyingAgencyMessage,
        String note
    ) {
        // We might want more specific information than the reason for message alone
        // also sometimes the status isn't enough by itself
        String status = supplyingAgencyMessage.statusInfo?.status?.code;
        String actionData = null;
        if (status) {
            if (status == Status.UNFILLED) {
                actionData = supplyingAgencyMessage.messageInfo?.reasonUnfilled?.code;
            }
        }

        // We overwrite the status information if there are loan conditions
        String loanConditions = supplyingAgencyMessage.deliveryInfo?.loanCondition;
        if ((loanConditions != null) && !loanConditions.isEmpty()) {
            status = "Conditional";
            actionData = loanConditions.toString();
        }

        incomingNotificationEntry(
            patronRequest,
            supplyingAgencyMessage.header.timestamp,
            supplyingAgencyMessage.header.requestingAgencyId,
            supplyingAgencyMessage.header.supplyingAgencyId,
            note,
            supplyingAgencyMessage.messageInfo?.reasonForMessage?.code,
            status,
            actionData
        );
    }

    protected void incomingNotificationEntry(
        PatronRequest patronRequest,
        String headerTimeStamp,
        AgencyId agencyReceiver,
        AgencyId agencySender,
        String note,
        String actionOrReason,
        String status = null,
        String actionData = null
    ) {
        PatronRequestNotification patronRequestNotification = new PatronRequestNotification()

        patronRequestNotification.setPatronRequest(patronRequest);
        patronRequestNotification.setSeen(false);

        // This line should grab timestamp from message rather than current time.
        patronRequestNotification.setTimestamp(ZonedDateTime.parse(headerTimeStamp).toInstant())
        patronRequestNotification.setMessageSender(
            illApplicationEventHandlerService.resolveSymbol(agencySender.agencyIdType.code, agencySender.agencyIdValue)
        );
        patronRequestNotification.setMessageReceiver(
            illApplicationEventHandlerService.resolveSymbol(agencyReceiver.agencyIdType?.code, agencyReceiver.agencyIdValue)
        );

        patronRequestNotification.setActionStatus(status);
        patronRequestNotification.setActionData(actionData);
        patronRequestNotification.setAttachedAction(actionOrReason);
        patronRequestNotification.setMessageContent(note)
        patronRequestNotification.setIsSender(false)

        log.debug("Inbound Message: ${patronRequestNotification.messageContent}")
        patronRequest.addToNotifications(patronRequestNotification)
    }
}

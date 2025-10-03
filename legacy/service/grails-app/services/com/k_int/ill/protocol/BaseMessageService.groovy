package com.k_int.ill.protocol;

import java.time.Instant;

import com.k_int.directory.ServiceAccount;
import com.k_int.directory.ServiceAccountService;
import com.k_int.directory.Symbol;
import com.k_int.ill.IllActionService;
import com.k_int.ill.IllApplicationEventHandlerService;
import com.k_int.ill.NetworkStatus;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestNotification;
import com.k_int.ill.Protocol;
import com.k_int.ill.ProtocolActionEvent;
import com.k_int.ill.ProtocolMessageToSend;
import com.k_int.ill.ProtocolResultStatus;
import com.k_int.ill.ProtocolSendResult;
import com.k_int.ill.constants.Directory
import com.k_int.ill.iso18626.Iso18626Message
import com.k_int.ill.iso18626.types.SenderHeader;
import com.k_int.ill.logging.ProtocolAuditService;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.statemodel.ActionEvent
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService;

import groovy.time.Duration;

public abstract class BaseMessageService {

    IllActionService illActionService;
    IllApplicationEventHandlerService illApplicationEventHandlerService;
	InstitutionSettingsService institutionSettingsService;
	ProtocolAuditService protocolAuditService;
	ProtocolIdService protocolIdService;
	ServiceAccountService serviceAccountService;
	
	/**
	 * Retrieves the protocol record	
	 * @return The protocol record
	 */
	public abstract Protocol getProtocol();

	/**
	 * Retrieves the protocol service type for this version of the protocol
	 * @return the service type code for this protocol 
	 */
	public abstract String getProtocolServiceType();

	/**
	 * Builds a requester side message 
	 * @param patronRequest the patron request to build the message against
	 * @param actionEventCode the action / event that triggered this message to be sent
	 * @param additionalInfo additional information required to build the message 
	 * @return the message that needs to be sent
	 */
	public abstract ProtocolMessageToSend buildRequesterMessage(
		PatronRequest patronRequest,
		String actionEventCode,
		Map additionalInfo
	);

	/**
	 * Retrieves the list of action events that are applicable for the protocol
	 * @return A collection of action events that are applicable for the protocol
	 */
	public Collection<ActionEvent> validActionEvents() {
		Collection<ActionEvent> actionEvents = new ArrayList<ActionEvent>();
		ProtocolActionEvent.findAllByProtocol(getProtocol()).each { ProtocolActionEvent protocolActionEvent ->
			actionEvents.add(protocolActionEvent.actionEvent);
		}
		return(actionEvents);
	}

	/**
	 * Builds a responder side message 
	 * @param patronRequest the patron request to build the message against
	 * @param actionEventCode the action / event that triggered this message to be sent
	 * @param additionalInfo additional information required to build the message 
	 * @return the message that needs to be sent
	 */
	public ProtocolMessageToSend buildSupplierMessage(
		PatronRequest patronRequest,
		String actionEventCode,
		Map additionalInfo,
		String actionEventResultQualifier
	) {
		// By default we just return null
		return(null);
	}

	/**
	 * Sends the message that was previously built
	 * @param serviceAccount The details about where we are to send the message
	 * @param messageToSend the message to be sent
	 * @return An object containing the details about trying to send the message
	 */
	protected abstract ProtocolSendResult sendMessage(
		Institution institution,
		ServiceAccount serviceAccount,
		ProtocolMessageToSend messageToSend
	);

    protected PatronRequest lookupRequest(
		String requestId,
		String peerRequestId,
		boolean isRequester
	) {
        // lookup the request
        PatronRequest patronRequest = lookupPatronRequestWithRole(requestId, isRequester, true);

        // Did we find the request
        if (patronRequest == null) {
            log.warn("Unable to locate PatronRequest corresponding to ID or Hrid \"${requestId}\".");
            log.warn("Looking to see if we can find the request using the peer request id \"${peerRequestId}\".");
            patronRequest = lookupPatronRequestByPeerId(peerRequestId, true);
        }

        // Return the request to the caller
        return(patronRequest);
    }

    protected PatronRequest lookupPatronRequestWithRole(
		String id,
		boolean isRequester,
		boolean withLock = false
	) {
        PatronRequest result = null;
        if (id) {
            log.debug("LOCKING IllApplicationEventHandlerService::lookupPatronRequestWithRole(${id},${withLock})");
            result = PatronRequest.createCriteria().get {
                and {
                    or {
                        eq('id', id)
                        eq('hrid', id)
                    }
                    eq('isRequester', isRequester)
                }
                lock withLock
            }

            log.debug("LOCKING baseMessageService::lookupPatronRequestWithRole located ${result?.id}/${result?.hrid}");
        }
        return(result);
    }

    protected PatronRequest lookupPatronRequestByPeerId(String id, boolean withLock) {
        PatronRequest result = null;
        if (id) {
            result = PatronRequest.createCriteria().get {
                eq('peerRequestIdentifier', id)
                lock withLock
            };
        }
        return(result);
    }

	protected boolean isForCurrentRotaLocation(SenderHeader header, PatronRequest patronRequest) {
        // By default we assume it is not
        boolean isCorrectRotaLocation = false;

        // First of all we will see if the rota position is in the id field
        long idRotaPosition = protocolIdService.extractRotaPositionFromProtocolId(header.requestingAgencyRequestId);
        if (idRotaPosition < 0) {
            // We failed to find the rota position, so we need to fallback on checking symbols, this can still fail as the same location can be in the rota multiple times
            Map symbols = illActionService.requestingAgencyMessageSymbol(patronRequest);
            if (symbols.receivingSymbol != null) {
                // Just compare the symbols
                isCorrectRotaLocation = symbols.receivingSymbol.equals(header.supplyingAgencyId.toSymbol());
            }
        } else {
            // That makes life nice and easy, just need to compare it with the rotaPosition
            isCorrectRotaLocation = patronRequest.rotaPosition.equals(idRotaPosition);
        }

        return(isCorrectRotaLocation);
    }

	protected void outgoingNotificationEntry(
		PatronRequest patronRequest,
		String note,
		Map actionMap,
		Symbol messageSender,
		Symbol messageReceiver,
		Boolean isRequester
	) {
		// Must have a note to add an outgoing notification
		if (note != null) {
			// It is a normal note
			String attachedAction = actionMap.action;
			String actionStatus = actionMap.status;
			String actionData = actionMap.data;

			PatronRequestNotification outboundMessage = new PatronRequestNotification();
			outboundMessage.patronRequest = patronRequest;
			outboundMessage.timestamp = Instant.now();
			outboundMessage.messageSender = messageSender;
			outboundMessage.messageReceiver = messageReceiver;
			outboundMessage.isSender = true;

			outboundMessage.attachedAction = attachedAction;
			outboundMessage.actionStatus = actionStatus;
			outboundMessage.actionData = actionData;

			outboundMessage.messageContent = note;

			log.debug("Outbound Message: ${outboundMessage.messageContent}");
			patronRequest.addToNotifications(outboundMessage);
		}
	}

    protected void appendStringToBuffer(StringBuffer stringBuffer, String value, String concatenator) {
        if (value != null) {
            String trimmedValue = value.trim();
            if (trimmedValue.length() > 0) {
                if (stringBuffer.length() > 0) {
                    // Add the concatenator as it is not the first string
                    stringBuffer.append(concatenator);
                }

                // Now just add the trimmed value
                stringBuffer.append(trimmedValue);
            }
        }
    }
}

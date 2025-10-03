package com.k_int.ill.protocol;

import com.k_int.directory.ServiceAccount;
import com.k_int.directory.ServiceAccountService;
import com.k_int.directory.Symbol;
import com.k_int.ill.IllApplicationEventHandlerService;
import com.k_int.ill.NetworkStatus
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestService;
import com.k_int.ill.Protocol;
import com.k_int.ill.ProtocolConversion;
import com.k_int.ill.ProtocolMessageToSend;
import com.k_int.ill.ProtocolResultStatus;
import com.k_int.ill.ProtocolSendResult;
import com.k_int.ill.constants.Directory;
import com.k_int.ill.constants.PropertyContext;
import com.k_int.ill.logging.ProtocolAuditService;
import com.k_int.ill.protocols.illEmail.IllEmailMessageService;
import com.k_int.ill.protocols.iso18626.Iso18626_2017MessageService;
import com.k_int.ill.protocols.iso18626.Iso18626_2021MessageService;

public class ProtocolService {

	IllApplicationEventHandlerService illApplicationEventHandlerService;
	IllEmailMessageService illEmailMessageService;
	Iso18626_2017MessageService iso18626_2017MessageService;
	Iso18626_2021MessageService iso18626_2021MessageService;
	PatronRequestService patronRequestService;
	ProtocolAuditService protocolAuditService;
	ServiceAccountService serviceAccountService;
	
	/**
	 * Fetches a Protocol object, given the code for it
	 * @param protocolCode the code that needs to be looked up
	 * @return the protocol object found for the code if found otherwise null
	 */
	public Protocol getProtocol(String protocolCode) {
		return(Protocol.findByCode(protocolCode));	
	}

	/**
	 * Determine the protocol to by used by the institution respresented by this supplied symbol
	 * @param symbol The institutions symbol
	 * @return The protocol to be used or null if no protocol could be determined
	 */
	public Protocol determineProtocol(Symbol symbol) {
		Protocol protocol = null;
		List<ServiceAccount> serviceAccounts = serviceAccountService.findServices(
			symbol,
			Directory.SERVICE_BUSINESS_FUNCTION_ILL,
			com.k_int.ill.constants.Protocol.PROTOCOL_SERVICE_TYPES
		);
		
		// Did we find any service accounts
		if (serviceAccounts != null) {
			serviceAccounts.each { ServiceAccount serviceAccount ->
				// ISO18626 takes priority, the order of precedence should probably be on the ServiceAccount record
				if ((protocol == null) || !com.k_int.ill.constants.Protocol.ISO18626_VARIANTS.contains(protocol.code)) {
					// Lookup the protocol
					protocol = getProtocol(com.k_int.ill.constants.Protocol.serviceTypeProtocol[serviceAccount.service.type.label]);
				}	
			}
		}
		
		// Return the result to the caller
		return(protocol);
	}

	/**
	 * Obtains the message service that is applicable for the supplied protocol
	 * @param protocol The protocol we want the message service for
	 * @return The determined message service
	 */
	private BaseMessageService getMessageService(Protocol protocol) {
		// Default the message service to null
		BaseMessageService baseMessageService = null;

		// Have we been supplied a protocol
		if (protocol != null) {
			switch (protocol.code) {
				case com.k_int.ill.constants.Protocol.ISO18626_2017:
					baseMessageService = iso18626_2017MessageService;
					break;

				case com.k_int.ill.constants.Protocol.ISO18626_2021:
					baseMessageService = iso18626_2021MessageService;
					break;

				case com.k_int.ill.constants.Protocol.ILL_SMTP:
					// Hand back the ILL Email message service
					baseMessageService = illEmailMessageService;
					break;
			} 
		}

		// Return the message service to the caller
		return(baseMessageService);
	}

	/**
	 * Logs an error and adds an audit message to the request
	 * @param patronRequest The request to add the audit message to
	 * @param errorMessage The error message to be logged and audited
	 * @param additionalInfo Any additional information to add to the audit message
	 */
	protected void logError(
		PatronRequest patronRequest,
		String errorMessage,
		Map additionalInfo = null
	) {
		// Log to the file first
		log.error(errorMessage);

		// Now add an audit entry for it
		illApplicationEventHandlerService.auditEntry(
			patronRequest,
			patronRequest.state,
			patronRequest.state,
			errorMessage,
			additionalInfo
		);
	}

	/**
	 * Generates a message and sends it using the appropriate protocol to the other side
	 * @param patronRequest The request to which we want to send a message for
	 * @param actionEventCode The action / event that generated the request for the message to be sent
	 * @param additionalInfo Any additional information required for the message to be sent
	 */
	public void sendMessage(
		PatronRequest patronRequest,
		String actionEventCode,
		Map<String, Object> additionalInfo,
		String actionEventResultQualifier
	) {
		// Get hold of the message service
		BaseMessageService baseMessageService = getMessageService(patronRequest.currentProtocol);

		// Did we get hold of the message service
		if (baseMessageService == null) {
			logError(
				patronRequest,
				"Failed to find a message service for protocol: " + patronRequest?.currentProtocol?.code,
				additionalInfo
			);
		} else {
			ProtocolMessageToSend messageToSend = null;

			// We have different calls to make depending on whether we are the requester or responder
			if (patronRequest.isRequester) {
				// Create a requester side message
				messageToSend = baseMessageService.buildRequesterMessage(
					patronRequest,
					actionEventCode,
					additionalInfo
				);
			} else {
				// Create a responder side message
				messageToSend = baseMessageService.buildSupplierMessage(
					patronRequest,
					actionEventCode,
					additionalInfo,
					actionEventResultQualifier
				);
			}

			// reset the number of attempts
			patronRequest.numberOfSendAttempts = 0;
			
			// Attempt to send the message
			sendMessageInternal(
				patronRequest,
				messageToSend,
				baseMessageService
			);
		}
	}

	/**
	 * Resends the previously sent message
	 * @param patronRequest The request for which we want to send the message for
	 */
	public void sendMessage(
		PatronRequest patronRequest
	) {
		// Get hold of the message service
		BaseMessageService baseMessageService = getMessageService(patronRequest.currentProtocol);

		// Did we get hold of the message service
		if (baseMessageService == null) {
			logError(
				patronRequest,
				"Failed to find a message service for protocol: " + patronRequest?.currentProtocol?.code
			);
		} else {
			// We are resending the last message
			sendMessageInternal(
				patronRequest,
				patronRequest.lastProtocolData,
				baseMessageService
			);
		}
	}

	/**
	 * Sends a protocol message to the other library
	 * @param patronRequest The request we are sending the message for
	 * @param messageToSend The message to be sent
	 * @param baseMessageService The service to use to send the message
	 */
	private void sendMessageInternal(
		PatronRequest patronRequest,
		ProtocolMessageToSend messageToSend,
		BaseMessageService baseMessageService
	) {
		// Have we been supplied a message to send, everything else should have been checked prior to getting here
		if ((messageToSend == null) || (messageToSend.message == null)) {
			logError(
				patronRequest,
				"Attempting to send an empty message, so not sending"
			);
		} else {
			// Ensure we have a symbol for who we are sending to
			Symbol symbol = patronRequest.isRequester ? patronRequest.resolvedSupplier : patronRequest.resolvedRequester;  

			if (symbol == null) {
				logError(
					patronRequest,
					"We do not a supplier symbol for patron request: " + patronRequest.id
				);
			} else {
				// Lookup the service account so we can send the message
				List<ServiceAccount> serviceAccounts = serviceAccountService.findServices(
					symbol,
					Directory.SERVICE_BUSINESS_FUNCTION_ILL,
					[ baseMessageService.getProtocolServiceType() ]
				);

				// Did we find one
				if ((serviceAccounts == null) || serviceAccounts.isEmpty()) {
					logError(
						patronRequest,
						"Failed to find a service account for responder " + symbol.owner,name
					);
				} else {
					// We have a service account
					ProtocolSendResult sendResult = baseMessageService.sendMessage(
						patronRequest.institution,
						serviceAccounts[0],
						messageToSend
					);
					if (sendResult.auditDetails != null) {
						protocolAuditService.save(patronRequest, sendResult.auditDetails);
					}

					// Now let us interpret the result
					switch (sendResult.status) {
						case ProtocolResultStatus.Sent:
							// TODO: Need to take into account a status of ERROR being returned in the protocol message, this assumed everything was received and processed without problems at the moment
							// Mark, it as sent, no longer need the eventData
							patronRequestService.setNetworkStatus(
								patronRequest,
								NetworkStatus.Sent,
								null,
								false
							);
							break;

						case ProtocolResultStatus.Timeout:
							// Mark it down to a timeout, need to save the event data for a potential retry
							patronRequestService.setNetworkStatus(
								patronRequest,
								NetworkStatus.Timeout,
								messageToSend.message,
								true
							);
							illApplicationEventHandlerService.auditEntry(
								patronRequest,
								patronRequest.state,
								patronRequest.state,
								'Encountered a network timeout while trying to send a message',
								null
							);
							log.warn('Hit a timeout trying to send protocol message: ' + sendResult.toString());
							break;
			
						case ProtocolResultStatus.Error:
							// Mark it as a retry, need to save the event data
							patronRequestService.setNetworkStatus(
								patronRequest,
								NetworkStatus.Retry,
								messageToSend.message,
								true
							);
							illApplicationEventHandlerService.auditEntry(
								patronRequest,
								patronRequest.state,
								patronRequest.state,
								'Encountered a network error while trying to send message',
								null
							);
							log.warn("Unable to send protocol message (" + sendResult.toString() + ")");
							break;
			
						case ProtocolResultStatus.ProtocolError:
							patronRequestService.setNetworkStatus(
								patronRequest,
								NetworkStatus.Error,
								messageToSend.message,
								false
							);
							illApplicationEventHandlerService.auditEntry(
								patronRequest,
								patronRequest.state,
								patronRequest.state,
								'Protocol error interpreting response',
								sendResult.response.toString()
							);
							log.error('Encountered a protocol error for request: ' + patronRequest.id);
							// Should we set the status to error as it now requires manual intervention ?
							break;
					}
				}			
			}
		}
	}

	/**
	 * Fetches the external service type for the supplied service type	
	 * @param protocol the protocol that we want to convert the service type into
	 * @param serviceType the service type that needs to be converted
	 * @return the value found for the value otherwise null
	 */
	public String getServiceTypeValue(
		Protocol protocol,
		String serviceType
	) {
		String protocolServiceType = null;

		// Have we been passed a service type
		if (serviceType != null) {
			// Can we find a mapping for this internal value for this protocol
			ProtocolConversion protocolConversion = ProtocolConversion.findByProtocolAndContextAndInternalValue(
				protocol,
				PropertyContext.SERVICE_TYPE,
				serviceType
			);

			// Did we find a conversion
			if (protocolConversion != null) {
				// We did find a value
				protocolServiceType = protocolConversion.protocolValue; 
			} 
		}

		// Return the result to the caller
		return(protocolServiceType);		
	}

	/**
	 * Fetches the internal service type for the protocol service type	
	 * @param protocol the protocol that we want to convert the service type into
	 * @param protocolServiceType the protocol service type that needs to be converted
	 * @return the value found for the value otherwise null
	 */
	public String getInternalServiceTypeValue(
		Protocol protocol,
		String protocolServiceType
	) {
		String internalServiceType = null;

		// Have we been passed a service type		
		if (protocolServiceType != null) {
			// Can we find a mapping for this internal value for this protocol
			ProtocolConversion protocolConversion = ProtocolConversion.findByProtocolAndContextAndProtocolValue(
				protocol,
				PropertyContext.SERVICE_TYPE,
				protocolServiceType
			);

			// Did we find a conversion
			if (protocolConversion != null) {
				// We did find a value
				internalServiceType = protocolConversion.internalValue; 
			} 
		}

		// Return the result to the caller
		return(internalServiceType);		
	}
}

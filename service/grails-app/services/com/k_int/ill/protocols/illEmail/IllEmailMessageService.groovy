package com.k_int.ill.protocols.illEmail;

import com.k_int.directory.ServiceAccount;
import com.k_int.ill.IllSmtpMessage;
import com.k_int.ill.MailQueueService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.Protocol;
import com.k_int.ill.ProtocolMessageToSend;
import com.k_int.ill.ProtocolMessageToSendEmail;
import com.k_int.ill.ProtocolResultStatus;
import com.k_int.ill.ProtocolSendResult
import com.k_int.ill.constants.Directory;
import com.k_int.ill.constants.Template;
import com.k_int.ill.logging.IIso18626LogDetails;
import com.k_int.ill.logging.ProtocolAuditService;
import com.k_int.ill.protocol.BaseMessageService;
import com.k_int.ill.protocol.ProtocolService;
import com.k_int.ill.statemodel.ActionEvent;
import com.k_int.ill.templating.TemplateContainer;
import com.k_int.ill.templating.TemplatingService;
import com.k_int.institution.Institution;

public class IllEmailMessageService extends BaseMessageService {

	IllEmailMessageTokensService illEmailMessageTokensService;
	MailQueueService mailQueueService;
    ProtocolAuditService protocolAuditService;
	ProtocolService protocolService;
	TemplatingService templatingService;

	/**
	 * Retrieves the protocol object for ILL SMTP	
	 * @return The protocol object for ILL SMTP
	 */
	@Override
	public Protocol getProtocol() {
		return(protocolService.getProtocol(com.k_int.ill.constants.Protocol.ILL_SMTP));
	}

	public String getProtocolServiceType() {
		return(Directory.SERVICE_TYPE_ILL_SMTP);
	}

	@Override
	public ProtocolMessageToSend buildRequesterMessage(
		PatronRequest patronRequest,
		String actionEventCode,
		Map additionalInfo
	) {
		ProtocolMessageToSend message = null;
		TemplateContainer templateContainer = null;
		ActionEvent actionEvent = ActionEvent.findByCode(actionEventCode);

		// Is this a valid action / event code
		if (actionEvent == null) {
			protocolService.logError(patronRequest, "Invalid actionEventCpde passed to buildMessage, failed to build Email message");
		} else {
			// Look to see if we have an active message to send for this scenario
			// It is sorted by the service type, as we allow null service type to be a wildcard and null should come last
			IllSmtpMessage.findAllByActiveAndActionEventAndInstitution(true, actionEvent, patronRequest.institution, [max: 10, sort: "serviceType"]).each { IllSmtpMessage illSmtpMessage ->
				// If we already have a container then we have already found what we are looking for
				if (templateContainer == null) {
					// Does the service type match or is it null
					if ((illSmtpMessage.serviceType == null) || (patronRequest.serviceType == illSmtpMessage.serviceType)) {
						// We have found what we are looking for
						templateContainer = illSmtpMessage.templateContainer; 
					}
				}
			}
			
			// Did we find a template container
			if (templateContainer == null) {
				protocolService.logError(patronRequest, "No template contaner found to send ILL Email message");
			} else {
				Map<String, Map<String, String>> tokenValues = illEmailMessageTokensService.tokenValues(patronRequest);
				Map templateResult = templatingService.performTemplate(templateContainer, tokenValues, Template.LOCALITY_ENGLISH);
	
				// If we have a header and a body then we will continue
				if ((templateResult == null) || (templateResult.result == null)) {
					log.error("No result after calling templatingService.performTemplate for container " + templateContainer.name + "(" + templateContainer.id + ")");
				} else if ((templateResult.result.body == null) || (templateResult.result.header == null)) {
					log.error("No header or body after templatingService.performTemplate for container " + templateContainer.name + "(" + templateContainer.id + "), result: " + templateResult.toString());
				} else {
					// We successfully have a body and subject
					message = new ProtocolMessageToSendEmail(templateResult.result.header, templateResult.result.body, patronRequest);
				}
			}
		}

		// Return the result to the caller
		return(message);
	}

	@Override
	protected ProtocolSendResult sendMessage(
		Institution institution,
		ServiceAccount serviceAccount,
		ProtocolMessageToSend messageToSend
	) {
		ProtocolSendResult sendResult = new ProtocolSendResult();
		IIso18626LogDetails logDetails = protocolAuditService.getIso18626LogDetails(institution);
		sendResult.auditDetails = logDetails;

		// If we do not have an email address in which to send the email then we have to error
		String email = serviceAccount.service.address;
		logDetails.request(email, messageToSend.message);
		if (email == null) {
			// No email address
			log.error("No email address on the service account with slug: " + serviceAccount.slug);
			sendResult.status = ProtocolResultStatus.Error;
		} else {
			ProtocolMessageToSendEmail protocolMessageToSendEmail = (ProtocolMessageToSendEmail)messageToSend;
			mailQueueService.save(
				email,
				protocolMessageToSendEmail.subject,
				protocolMessageToSendEmail.message,
				protocolMessageToSendEmail.patronRequest
			);

			// Assume it has been a success
		    sendResult.status = ProtocolResultStatus.Sent;
		}

		// Return the result to the caller		
		return(sendResult);
	}
}

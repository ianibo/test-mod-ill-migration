package com.k_int.ill.protocols.iso18626;

import static groovyx.net.http.ContentTypes.XML;

import java.time.LocalDate;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.grails.databinding.xml.GPathResultMap;

import com.k_int.directory.ServiceAccount;
import com.k_int.directory.Symbol;
import com.k_int.ill.CopyrightMessage;
import com.k_int.ill.CopyrightMessageService;
import com.k_int.ill.IllApplicationEventHandlerService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestCopyright;
import com.k_int.ill.PatronRequestRota;
import com.k_int.ill.Protocol;
import com.k_int.ill.ProtocolMessageToSend;
import com.k_int.ill.ProtocolResultStatus;
import com.k_int.ill.ProtocolSendResponse;
import com.k_int.ill.ProtocolSendResult;
import com.k_int.ill.RequestIdentifier;
import com.k_int.ill.SharedIndexService;
import com.k_int.ill.iso18626.ExtractedNoteFieldResult;
import com.k_int.ill.iso18626.Iso18626Message;
import com.k_int.ill.iso18626.NoteSpecials;
import com.k_int.ill.iso18626.Request;
import com.k_int.ill.iso18626.RequestConfirmation;
import com.k_int.ill.iso18626.RequestingAgencyMessage;
import com.k_int.ill.iso18626.RequestingAgencyMessageConfirmation;
import com.k_int.ill.iso18626.SupplyingAgencyMessage;
import com.k_int.ill.iso18626.SupplyingAgencyMessageConfirmation;
import com.k_int.ill.iso18626.codes.closed.Action;
import com.k_int.ill.iso18626.codes.closed.ErrorCode;
import com.k_int.ill.iso18626.codes.closed.MessageStatus;
import com.k_int.ill.iso18626.codes.closed.ReasonForMessage;
import com.k_int.ill.iso18626.codes.closed.Status;
import com.k_int.ill.iso18626.codes.open.BibliographicItemIdentifierCode;
import com.k_int.ill.iso18626.codes.open.BibliographicRecordIdentifierCode;
import com.k_int.ill.iso18626.complexTypes.Address;
import com.k_int.ill.iso18626.complexTypes.BibliographicItemId;
import com.k_int.ill.iso18626.complexTypes.BibliographicRecordId;
import com.k_int.ill.iso18626.complexTypes.ElectronicAddress;
import com.k_int.ill.iso18626.complexTypes.PhysicalAddress;
import com.k_int.ill.iso18626.types.BibliographicInfo;
import com.k_int.ill.iso18626.types.ErrorData;
import com.k_int.ill.iso18626.types.MessageInfo;
import com.k_int.ill.iso18626.types.PatronInfo;
import com.k_int.ill.iso18626.types.PublicationInfo;
import com.k_int.ill.iso18626.types.RequestHeader;
import com.k_int.ill.iso18626.types.RequestedDeliveryInfo;
import com.k_int.ill.iso18626.types.RequestingAgencyHeader;
import com.k_int.ill.iso18626.types.SenderHeader;
import com.k_int.ill.iso18626.types.ServiceInfo;
import com.k_int.ill.logging.IIso18626LogDetails;
import com.k_int.ill.logging.ProtocolAuditService;
import com.k_int.ill.protocol.BaseMessageService;
import com.k_int.ill.protocol.ProtocolService;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.sharedindex.SharedIndexResult;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.ActionService;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.Events;
import com.k_int.ill.statemodel.StatusService;
import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionService;

import groovyx.net.http.ApacheHttpBuilder;
import groovyx.net.http.FromServer;
import groovyx.net.http.HttpBuilder;

public abstract class Iso18626MessageService extends BaseMessageService {

    private static final String ADDRESS_CONCATENATOR = " ";

	public static final String STATUS_PROTOCOL_ERROR = 'PROTOCOL_ERROR';

	/** Timeout period in seconds */
	private static final int DEFAULT_TIMEOUT_PERIOD = 30;
	
    ActionService actionService;
	CopyrightMessageService copyrightMessageService;
    IllApplicationEventHandlerService illApplicationEventHandlerService;
    InstitutionService institutionService;
	Iso18626NotesService iso18626NotesService;
	Iso18626Service iso18626Service;
    ProtocolAuditService protocolAuditService;
	ProtocolService protocolService;
    SharedIndexService sharedIndexService;
    StatusService statusService;

	@Override
	public abstract Protocol getProtocol();

	@Override
	public abstract String getProtocolServiceType();

	/**
	 * Retrieves the builder to be used with this message service
	 * @return the builder to be used for this protocol 
	 */
	public abstract Iso18626BuilderService getBuilder();

    /**
     * Process an incoming ISO18626 message
     * @param institution The institution the call is for
     * @param xml The raw xml that was received, if logging enabled will be recorded with the request
     * @param iso18626Message The instance
     * @return The confirmation message that is to be returned
     */
    public Iso18626Message processMessage(Institution institution, String xml, Iso18626Message iso18626Message) {
        Iso18626Message confirmationMessage  = null;
        IIso18626LogDetails iso18626LogDetails = protocolAuditService.getIso18626LogDetails(institution);
        iso18626LogDetails.request(ProtocolAuditService.RECEIVED_MESSAGED, xml);

        // Start a transaction to deal with the message
        PatronRequest.withTransaction { status ->
            // What sort of message did we receive
            if (iso18626Message.requestingAgencyMessage != null) {
                // We have a requesting agency message
                confirmationMessage = processRequestingAgencyMessage(iso18626LogDetails, iso18626Message.requestingAgencyMessage);
            } else if (iso18626Message.supplyingAgencyMessage != null) {
                // We have a supplying agency message
                confirmationMessage = processSupplyingAgencyMessage(iso18626LogDetails, iso18626Message.supplyingAgencyMessage);
            } else if (iso18626Message.request != null) {
                // We have a request message
                confirmationMessage = processRequestMessage(iso18626LogDetails, iso18626Message.request);
            } else {
                log.error("Unknown Iso18626 message has been received, xml: " + xml);
            }
        }
        return(confirmationMessage);
    }

    protected void finaliseLogging(
        PatronRequest patronRequest,
        IIso18626LogDetails iso18626LogDetails,
        Iso18626Message confirmationMessage
    ) {
        iso18626LogDetails.response("200", iso18626Service.toXml(confirmationMessage))
        protocolAuditService.save(patronRequest, iso18626LogDetails);
    }

	/**
	 * Converts a list of strings into a String using ", " as the separator
	 * @param list the string list that needs to be converted
	 * @return the string generated from processing all the items
	 */
	protected String stringListAsString(List<String> list) {
		StringBuffer stringBuffer = null;

		// Do we have a list with any items
		if ((list != null) && !list.isEmpty()) {
			// We do sp process each one
			list.each({String listItem ->
				// Not interested in a null or blank item
				if ((listItem != null) && !listItem.isBlank()) {
					// Is this the first time through
					if (stringBuffer == null) {
						// It is so allocate a new StringBuffer
						stringBuffer = new StringBuffer();
					} else {
						// Nope, so add a separator between the items
						stringBuffer += ", "
					}

					// Finally we can add the string to the string buffer
					stringBuffer += listItem;
				}  
			})
		}

		// Return a string to the caller or null
		return(stringBuffer == null ? null : stringBuffer.toString());
	}

    protected Iso18626Message processRequestMessage(
        IIso18626LogDetails iso18626LogDetails,
        Request requestMessage
    ) {
        Iso18626Message confirmationMessage = new Iso18626Message(new RequestConfirmation(requestMessage));

        // Check that we understand both the requestingAgencyId (our peer)and the SupplyingAgencyId (us)
        if ((requestMessage.bibliographicInfo != null) &&
            (requestMessage.header != null)) {
            // For recording any errors
            ErrorData errorData = null;

            log.debug('*** Create new request***');
            PatronRequest patronRequest = new PatronRequest();

            // Deal with the bibliographic info first of all
            BibliographicInfo bibliographicInfo = requestMessage.bibliographicInfo;
            patronRequest.supplierUniqueRecordId = bibliographicInfo.supplierUniqueRecordId;
            patronRequest.title = bibliographicInfo.title;
            patronRequest.author = bibliographicInfo.author;
            patronRequest.subtitle = bibliographicInfo.subtitle;
            patronRequest.edition = bibliographicInfo.edition;
            patronRequest.titleOfComponent = bibliographicInfo.titleOfComponent;
            patronRequest.authorOfComponent = bibliographicInfo.authorOfComponent;
            patronRequest.volume = stringListAsString(bibliographicInfo.volume);
            patronRequest.issue = bibliographicInfo.issue;
            patronRequest.sponsor = bibliographicInfo.sponsor;
            patronRequest.informationSource = bibliographicInfo.informationSource;

            // Extract the identifiers in bibliographicInfo.bibliographicItemId
            if (bibliographicInfo.bibliographicItemId != null) {
                bibliographicInfo.bibliographicItemId.each { BibliographicItemId bibliographicItemId ->
                    if (bibliographicItemId.bibliographicItemIdentifierCode == null) {
                        patronRequest.bibliographicRecordId = bibliographicItemId.bibliographicItemIdentifier;
                    } else if (bibliographicItemId.bibliographicItemIdentifierCode == BibliographicItemIdentifierCode.ISBN) {
                        patronRequest.isbn = bibliographicItemId.bibliographicItemIdentifier;
                    } else if (bibliographicItemId.bibliographicItemIdentifierCode == BibliographicItemIdentifierCode.ISSN) {
                        patronRequest.issn = bibliographicItemId.bibliographicItemIdentifier;
                    } else {
                        // Just add it as an identifier
                        patronRequest.addToRequestIdentifiers(new RequestIdentifier(
                            identifierType: bibliographicItemId.bibliographicItemIdentifierCode,
                            identifier: bibliographicItemId.bibliographicItemIdentifier
                        ));
                    }
                }
            }

            // Extract the identifiers in bibliographicInfo.bibliographicRecordId
            if (bibliographicInfo.bibliographicRecordId != null) {
                bibliographicInfo.bibliographicRecordId.each { BibliographicRecordId bibliographicRecordId->
                    if (bibliographicRecordId.bibliographicRecordIdentifierCode == BibliographicRecordIdentifierCode.OCLC) {
                        patronRequest.oclcNumber = bibliographicRecordId.bibliographicRecordIdentifier;
                    } else {
                        // Just add it as an identifier
                        patronRequest.addToRequestIdentifiers(new RequestIdentifier(
                            identifierType: bibliographicRecordId.bibliographicRecordIdentifierCode,
                            identifier: bibliographicRecordId.bibliographicRecordIdentifier
                        ));
                    }
                }
            }

            // Add publisher information to Patron Request
            PublicationInfo publicationInfo = requestMessage.publicationInfo;
            if (requestMessage.publicationInfo != null) {
                if (publicationInfo.publisher) {
                    patronRequest.publisher = publicationInfo.publisher;
                }
                if (publicationInfo.publicationType) {
                    patronRequest.publicationType = patronRequest.lookupPublicationType(publicationInfo.publicationType.code);
                }
                if (publicationInfo.publicationDate) {
                    patronRequest.publicationDate = publicationInfo.publicationDate;
                }
                if (publicationInfo.placeOfPublication) {
                    patronRequest.placeOfPublication = publicationInfo.placeOfPublication;
                }
            }

            // Add service information to Patron Request
            ServiceInfo serviceInfo = requestMessage.serviceInfo;
            if (serviceInfo != null) {
                if (serviceInfo.serviceType) {
					// Obtain the internal service type value
					String internalServiceType = protocolService.getInternalServiceTypeValue(
						getProtocol(),
						serviceInfo.serviceType.code
					);
					
					// If we found an internal service type value set it on the request
					if (internalServiceType != null) {
						patronRequest.serviceType = patronRequest.lookupServiceType(internalServiceType);
					}
                }
				if (serviceInfo.copyrightCompliance != null) {
					String copyrightCode = serviceInfo.copyrightCompliance.code;
					
					// Do we know about this copyright code
					CopyrightMessage copyrightMessage = copyrightMessageService.ensure(
						copyrightCode,
						copyrightCode,
						"UK",
						"To be filled in when known"
					);

					// Did we find / create a copyright message
					if (copyrightMessage) {
						// We did so associate with the request					
						patronRequest.copyright = new PatronRequestCopyright();
						patronRequest.copyright.copyrightMessage = copyrightMessage;
					}
				}
                if (serviceInfo.needBeforeDate) {
                    // This will come in as a string, will need parsing
                    try {
                        patronRequest.neededBy = LocalDate.parse(serviceInfo.needBeforeDate);
                    } catch (Exception e) {
                        log.debug("Failed to parse neededBy date (${serviceInfo.needBeforeDate}): ${e.message}");
                    }
                }
                if (serviceInfo.note) {
                    String note = serviceInfo.note;

                    // Do we have a patron reference
                    ExtractedNoteFieldResult extractedFieldResult = iso18626NotesService.extractFieldFromNote(note, NoteSpecials.ILL_FIELD_PATRON_REFERENCE_PREFIX);
                    if (extractedFieldResult.data != null) {
                        patronRequest.patronReference = extractedFieldResult.data;
                        note = extractedFieldResult.note;
                    }

                    // publication date of component is also a special in the note
                    extractedFieldResult = iso18626NotesService.extractFieldFromNote(note, NoteSpecials.ILL_FIELD_PUBLICATION_DATE_OF_COMPONENT_PREFIX);
                    if (extractedFieldResult.data != null) {
                        patronRequest.publicationDateOfComponent = extractedFieldResult.data;
                        note = extractedFieldResult.note;
                    }

                    // We may have a sequence number that needs to be extracted
                    Map sequenceResult = iso18626NotesService.extractSequenceFromNote(note);
                    patronRequest.patronNote = sequenceResult.note;
                    patronRequest.lastSequenceReceived = sequenceResult.sequence;
                }
            }

            if ((requestMessage.requestedDeliveryInfo != null) &&
                (requestMessage.requestedDeliveryInfo.size() > 0)) {
                // We have multiple addresses, so loop through them,
                requestMessage.requestedDeliveryInfo.each{ RequestedDeliveryInfo requestedDeliveryInfo ->
                    // Note we do not deal with multiple addresses, so we just take the last one processed of each type
                    Address address = requestedDeliveryInfo?.address;
                    if (address != null) {
                        PhysicalAddress physicalAddress = address.physicalAddress;
                        if (physicalAddress != null) {
                            log.debug("Incoming request contains physical delivery info");
                            // We join all the lines of physical address and stuff them into pickup location for now.
                            StringBuffer pickupLocationBuffer = new StringBuffer();
                            appendStringToBuffer(pickupLocationBuffer, physicalAddress.line1, ADDRESS_CONCATENATOR);
                            appendStringToBuffer(pickupLocationBuffer, physicalAddress.line2, ADDRESS_CONCATENATOR);
                            appendStringToBuffer(pickupLocationBuffer, physicalAddress.locality, ADDRESS_CONCATENATOR);
                            appendStringToBuffer(pickupLocationBuffer, physicalAddress.postalCode, ADDRESS_CONCATENATOR);
                            appendStringToBuffer(pickupLocationBuffer, physicalAddress.region, ADDRESS_CONCATENATOR);
                            appendStringToBuffer(pickupLocationBuffer, physicalAddress.country, ADDRESS_CONCATENATOR);

                            // If we've not been given any address information, don't translate that into a pickup location
                            if (pickupLocationBuffer.length() > 0) {
                                patronRequest.pickupLocation = pickupLocationBuffer.toString();
                            }
                        } else {
                            // Since ISO18626-2017 doesn't yet offer DeliveryMethod here we encode it as an ElectronicAddressType
                            // Should really have gone in as a note special, since it was not supported by the protocol
                            ElectronicAddress electronicAddress = address.electronicAddress;
                            if (electronicAddress != null) {
                                patronRequest.deliveryMethod = patronRequest.lookupDeliveryMethod(electronicAddress.electronicAddressType.code);
                            }
                        }
                    }
                }
            }

            // Add patron information to Patron Request
            PatronInfo patronInfo = requestMessage.patronInfo;
            if (patronInfo != null) {
                if (patronInfo.patronId) {
                    patronRequest.patronIdentifier = patronInfo.patronId;
                }
                if (patronInfo.surname) {
                    patronRequest.patronSurname = patronInfo.surname;
                }
                if (patronInfo.givenName) {
                    patronRequest.patronGivenName = patronInfo.givenName;
                }
                if (patronInfo.patronType) {
                    patronRequest.patronType = patronInfo.patronType;
                }
            }

            RequestHeader header = requestMessage.header;

            Symbol resolvedSupplyingAgency = illApplicationEventHandlerService.resolveSymbol(header.supplyingAgencyId?.agencyIdType?.code, header.supplyingAgencyId?.agencyIdValue);
            Symbol resolvedRequestingAgency = illApplicationEventHandlerService.resolveSymbol(header.requestingAgencyId?.agencyIdType?.code, header.requestingAgencyId?.agencyIdValue);

            patronRequest.supplyingInstitutionSymbol = header.supplyingAgencyId.toSymbol();
            patronRequest.requestingInstitutionSymbol = header.requestingAgencyId.toSymbol();

            patronRequest.resolvedRequester = resolvedRequestingAgency;
            patronRequest.resolvedSupplier = resolvedSupplyingAgency;
            patronRequest.peerRequestIdentifier = header.requestingAgencyRequestId;

            // For ill - we assume that the requester is sending us a globally unique HRID and we would like to be
            // able to use that for our request.
            patronRequest.hrid = protocolIdService.extractIdFromProtocolId(header?.requestingAgencyRequestId);

            if (patronRequest.bibliographicRecordId) {
                log.debug("Incoming request with pr.bibliographicRecordId - calling fetchSharedIndexRecords ${patronRequest.bibliographicRecordId}");
                SharedIndexResult sharedIndexResult = sharedIndexService.getSharedIndexActions().fetchSharedIndexRecords([systemInstanceIdentifier: patronRequest.bibliographicRecordId]);
                if (sharedIndexResult?.totalRecords > 0) {
                    // have commented out the saving of the bib record, we will now need to convert it to json first
                    // patronRequest.bibRecord = bibRecords[0];
                    if (sharedIndexResult?.totalRecords > 1) {
                        illApplicationEventHandlerService.auditEntry(patronRequest, null, patronRequest.state, "WARNING: shared index ID ${patronRequest.bibliographicRecordId} matched multiple records", null);
                    }
                }
            }

            log.debug("new request from ${patronRequest.requestingInstitutionSymbol} to ${patronRequest.supplyingInstitutionSymbol}");

            // Set the institution this request belongs to
			// Needs to be set before we get hold of the state model
            patronRequest.institution = institutionService.getInstitution(patronRequest.resolvedSupplier?.owner);

			// Set the current protocol
			patronRequest.currentProtocol = getProtocol();

            patronRequest.isRequester = false;
            patronRequest.stateModel = statusService.getStateModel(patronRequest);
            patronRequest.state = patronRequest.stateModel.initialState;
            illApplicationEventHandlerService.auditEntry(
                patronRequest,
                null,
                patronRequest.state,
                'New request (Lender role) created as a result of protocol interaction',
                null
            );

            // Set the result on the confirmation message
            confirmationMessage.requestConfirmation.header.result(errorData);

            // We can finalise the logging now we have processed the request
            finaliseLogging(patronRequest, iso18626LogDetails, confirmationMessage);

            log.debug("Saving new PatronRequest(SupplyingAgency) - Req:${patronRequest.resolvedRequester} Res:${patronRequest.resolvedSupplier} PeerId:${patronRequest.peerRequestIdentifier}");
            patronRequest.save(flush:true, failOnError:true)
        } else {
            confirmationMessage.requestConfirmation.header.result(new ErrorData(ErrorCode.BADLY_FORMED_MESSAGE, "No header element or bibliographic element found"));
            log.error("Unable to create request from xml");
        }

        return(confirmationMessage)
    }

    protected Iso18626Message processRequestingAgencyMessage(
        IIso18626LogDetails iso18626LogDetails,
        RequestingAgencyMessage requestingAgencyMessage
    ) {
        Iso18626Message confirmationMessage = new Iso18626Message(new RequestingAgencyMessageConfirmation(requestingAgencyMessage));

        RequestingAgencyHeader header = requestingAgencyMessage.header;
        if (header == null) {
            confirmationMessage.requestingAgencyMessageConfirmation.header.result(new ErrorData(ErrorCode.BADLY_FORMED_MESSAGE, "No header element found"));
        } else {
            // Obtain the request id
            String requestId = header.supplyingAgencyRequestId;

            // Attempt to find the request we need to process
            PatronRequest patronRequest = lookupRequest(requestId, header.requestingAgencyRequestId, true);

            // Did we find the request
            if (patronRequest == null) {
                // We did not
                confirmationMessage.requestingAgencyMessageConfirmation.header.result(new ErrorData(ErrorCode.UNRECONISED_DATA_VALUE, "Cannot find request for supplyingAgencyRequestId: " + requestId + ", requestingAgencyRequestId: " + header.requestingAgencyRequestId));
            } else {
                // We have found the request
                ErrorData errorData = null;

                // It is, so lets check the reason for message
                String action = requestingAgencyMessage.findActionCode();
                if (action == null) {
                    errorData = new ErrorData(ErrorCode.BADLY_FORMED_MESSAGE, "No Action element found");
                } else {
                    // Now perform the action required
                    ActionResultDetails actionResults = actionService.performAction('ISO18626' + action, patronRequest, requestingAgencyMessage);

                    // Deal with what we have been returned
                    if (actionResults.result != ActionResult.SUCCESS) {
                        errorData = new ErrorData(actionResults.responseResult.errorType, actionResults.responseResult.errorValue);
                    }
                }

                // Complete the protocol logging
                confirmationMessage.requestingAgencyMessageConfirmation.header.result(errorData);
                finaliseLogging(patronRequest, iso18626LogDetails, confirmationMessage);
            }
        }
        return(confirmationMessage)
    }

    protected Iso18626Message processSupplyingAgencyMessage(
        IIso18626LogDetails iso18626LogDetails,
        SupplyingAgencyMessage supplyingAgencyMessage
    ) {
        Iso18626Message confirmationMessage = new Iso18626Message(new SupplyingAgencyMessageConfirmation(supplyingAgencyMessage));
        SenderHeader header = supplyingAgencyMessage.header;
        if (header == null) {
            confirmationMessage.supplyingAgencyMessageConfirmation.header.result(new ErrorData(ErrorCode.BADLY_FORMED_MESSAGE, "No header element found"));
        } else {
            // Obtain the request id
            String requestId = protocolIdService.extractIdFromProtocolId(header.requestingAgencyRequestId);

            // Attempt to find the request we need to process
            PatronRequest patronRequest = lookupRequest(requestId, null, true);

            // Did we find the request
            if (patronRequest == null) {
                // We did not
                confirmationMessage.supplyingAgencyMessageConfirmation.header.result(new ErrorData(ErrorCode.UNRECONISED_DATA_VALUE, "Cannot find request for requestingAgencyRequestId: " + requestId));
            } else {
                // We have found the request
                ErrorData errorData = null;

                // Is it for the current rota position
                if (isForCurrentRotaLocation(header, patronRequest)) {
                    // It is, so lets check the reason for message
                    MessageInfo messageInfo = supplyingAgencyMessage.messageInfo;
                    if (messageInfo == null) {
                        errorData = new ErrorData(ErrorCode.BADLY_FORMED_MESSAGE, "No MessageInfo element found");
                    } else {
                        String reasonForMessage = messageInfo.reasonForMessage;
                        if (reasonForMessage == null) {
                            errorData = new ErrorData(ErrorCode.BADLY_FORMED_MESSAGE, "No ReasonForMessage element found");
                        } else {
                            // Now perform the action required
                            ActionResultDetails actionResults = actionService.performAction('ISO18626' + reasonForMessage, patronRequest, supplyingAgencyMessage);

                            // Deal with what we have been returned
                            if (actionResults.result != ActionResult.SUCCESS) {
                                errorData = new ErrorData(actionResults.responseResult.errorType, actionResults.responseResult.errorValue);
                            }
                        }
                    }
                } else {
                    // It is not for the correct rota location, so we need to reject the message
                    errorData = new ErrorData(ErrorCode.UNRECONISED_DATA_VALUE, "Request is not active for supplier " + header.supplyingAgencyId.toSymbol());
                }

                // Complete the protocol logging
                confirmationMessage.supplyingAgencyMessageConfirmation.header.result(errorData);
                finaliseLogging(patronRequest, iso18626LogDetails, confirmationMessage);
            }
        }

		// Return the confirmation message
        return(confirmationMessage)
    }

	@Override
	protected void outgoingNotificationEntry(
		PatronRequest patronRequest,
		String note,
		Map actionMap,
		Symbol messageSender,
		Symbol messageReceiver,
		Boolean isRequester
	) {
		// We do not do anything if the note begins with the update field special
		if ((note != null) && !note.startsWith(NoteSpecials.UPDATE_FIELD)) {
			// Just call the parent
			super.outgoingNotificationEntry(
				patronRequest,
				note,
				actionMap,
				messageSender,
				messageReceiver,
				isRequester
			);	
		}
	}
	
	@Override
	public ProtocolMessageToSend buildRequesterMessage(
		PatronRequest patronRequest,
		String actionEventCode,
		Map additionalInfo
	) {
		Iso18626Message iso18626Message = null;
		
		switch (actionEventCode) {
			case Events.EVENT_STATUS_REQ_SUPPLIER_IDENTIFIED_INDICATION:
			case Events.EVENT_STATUS_REQ_UNFILLED_INDICATION:
				iso18626Message = buildRequestMessage(patronRequest);
				break;
				
			default:
				iso18626Message = buildRequesterAgencyMessage(
					patronRequest,
					actionEventCode,
					additionalInfo
				);
				break;
		}

		// Convert the message to xml
		return(new ProtocolMessageToSend(iso18626Service.toXml(iso18626Message)));
	}

	/**
	 * Builds the request message for the requester
	 * @param patronRequest the patron request that the request is to be built from
	 * @return The ISO19626 request message
	 */
	protected Iso18626Message buildRequestMessage(
		PatronRequest patronRequest
	) {
		PatronRequestRota patronRequestRota = patronRequest.rota.find {PatronRequestRota prr ->
			return(patronRequest.rotaPosition == prr.rotaPosition);
		}

		// Build the message
		Iso18626Message iso18626Message = getBuilder().buildRequestMessage(
			patronRequest,
			patronRequestRota
		);

		// Return the message
		return(iso18626Message);
	}

	/**
	 * Builds the requester agency message
	 * @param patronRequest the patron request that the request agency message is to be built from 
	 * @param actionEventCode the action / event that trigger this message to be sent
	 * @param additionalInfo Additional information that was supplied that may not be on the request
	 * @return The ISO19626 requester agency message
	 */
	protected Iso18626Message buildRequesterAgencyMessage(
		PatronRequest patronRequest,
		String actionEventCode,
		Map additionalInfo
	) {
		// Build the message
		Iso18626Message iso18626Message = getBuilder().buildRequestingAgencyMessage(
			patronRequest,
			determineISO18626Action(actionEventCode),
			additionalInfo
		);

		// If we have a note create a notification entry for it
		if (additionalInfo?.note != null) {
			Map actionMap = [action: iso18626Message.requestingAgencyMessage?.findActionCode()];
			outgoingNotificationEntry(
			    patronRequest,
			    additionalInfo.note,
			    actionMap,
			    patronRequest.resolvedRequester,
			    patronRequest.resolvedSupplier,
			    true
		    )
		}

		// Return the message
		return(iso18626Message);
	}

	@Override
	public ProtocolMessageToSend buildSupplierMessage(
		PatronRequest patronRequest,
		String actionEventCode,
		Map additionalInfo,
		String actionEventResultQualifier
	) {
		String reasonForMessage = determineISO18626ReasonForMessage(patronRequest, actionEventCode);
		Iso18626Message iso18626Message = getBuilder().buildSupplyingAgencyMessage(
			patronRequest,
			reasonForMessage,
			determineISO18626Status(actionEventCode, actionEventResultQualifier),
			additionalInfo
		);
	
		// If this for a loan condition
		if (additionalInfo.loanCondition) {
			illApplicationEventHandlerService.addLoanConditionToRequest(
				patronRequest,
				additionalInfo.loanCondition,
				patronRequest.resolvedSupplier,
				additionalInfo.note
			);
		}

		// Do we have a note		
		if (additionalInfo.note != null) {
			Map actionMap = [ action : reasonForMessage ]
			actionMap.status = iso18626Message.supplyingAgencyMessage?.statusInfo?.status?.code;
	
			if (additionalInfo.loanCondition) {
			    actionMap.status = "Conditional"
			    actionMap.data = additionalInfo.loanCondition;
			}
			if (additionalInfo.reason) {
			    actionMap.data = additionalInfo.reason;
			}
	
		    outgoingNotificationEntry(
				patronRequest,
				additionalInfo.note,
				actionMap,
				patronRequest.resolvedSupplier,
				patronRequest.resolvedRequester,
				false
			);
		}

		// Convert the message to xml
		return(new ProtocolMessageToSend(iso18626Service.toXml(iso18626Message)));
	}
	
	@Override
	protected ProtocolSendResult sendMessage(
		Institution institution,
		ServiceAccount serviceAccount,
		ProtocolMessageToSend messageToSend
	) {
		ProtocolSendResult sendResult = new ProtocolSendResult();
		sendResult.auditDetails = protocolAuditService.getIso18626LogDetails(institution);
		
		try {
		    Map additionalHeaders = serviceAccountService.getAdditonalHeaders(serviceAccount);
		    sendResult.response = sendMessageInternal(
                institution,
                messageToSend.message,
                serviceAccount.service.address,
                additionalHeaders,
                (IIso18626LogDetails)sendResult.auditDetails
            );
		    sendResult.status = (sendResult.response.messageStatus == STATUS_PROTOCOL_ERROR) ? ProtocolResultStatus.ProtocolError : ProtocolResultStatus.Sent;
		    log.debug("ISO18626 message sent")
		} catch(Exception e) {
		    if ((e.cause != null) && (e.cause instanceof java.net.SocketTimeoutException)) {
		        // We have hit a timeout
		        sendResult.status = ProtocolResultStatus.Timeout;
		    } else {
		        // Everything else treated as not sent
		        sendResult.status = ProtocolResultStatus.Error;
		    }
		    log.error("ISO18626 message failed to send. ${e}/${e?.class?.name}/${e.message}",e)
		}
		return(sendResult);
	}

	protected ProtocolSendResponse sendMessageInternal(
		Institution institution,
		String messageToSend,
		String address,
		Map additionalHeaders,
		IIso18626LogDetails iso18626LogDetails
	) {
	  ProtocolSendResponse sendResponse = new ProtocolSendResponse(MessageStatus.ERROR);
	  log.debug("ISO18626 address: ${address}, additional headers: ${additionalHeaders}, message:\n${messageToSend}");
//      new File("D:/Source/Folio/mod-ill/logs/isomessages.log").append(message + "\n\n");

		if ( address != null ) {
			  // It is stored as seconds in the settings, so need to multiply by 1000
			int timeoutPeriod = institutionSettingsService.getSettingAsInt(
				institution,
				SettingsData.SETTING_NETWORK_TIMEOUT_PERIOD,
				DEFAULT_TIMEOUT_PERIOD,
				false
			) * 1000;

			// Audit this message
			iso18626LogDetails.request(address, messageToSend);

			HttpBuilder http_client = ApacheHttpBuilder.configure {
				// HttpBuilder http_client = configure {

				client.clientCustomizer { HttpClientBuilder builder ->
					RequestConfig.Builder requestBuilder = RequestConfig.custom();
					requestBuilder.connectTimeout = timeoutPeriod;
					requestBuilder.connectionRequestTimeout = timeoutPeriod;
					requestBuilder.socketTimeout = timeoutPeriod;
					builder.defaultRequestConfig = requestBuilder.build();
				}

				request.uri = address;
				request.contentType = XML[0];
				request.headers['accept'] = 'application/xml, text/xml';
				additionalHeaders?.each { k,v ->
					request.headers[k] = v
				}
			}

			Date transactionStarted = new Date();
			def iso18626_response = http_client.post {
				request.body = messageToSend;

				response.failure { FromServer fs ->
					logMessageAudit(transactionStarted, new Date(), address, fs.getStatusCode(), messageToSend);
					log.error("Got failure response from remote ISO18626 site (${address}): ${fs.getStatusCode()} ${fs}");
					String respomseStatus = fs.getStatusCode().toString() + " " + fs.getMessage();
					iso18626LogDetails.response(respomseStatus, fs.hasBody ? fs.toString() : null);
					throw new RuntimeException("Failure response from remote ISO18626 service (${address}): ${fs.getStatusCode()} ${fs}");
				}

				response.success { FromServer fs, xml ->
					String respomseStatus = fs.getStatusCode().toString() + " " + fs.getMessage();
					logMessageAudit(transactionStarted, new Date(), address, fs.getStatusCode(), messageToSend);
					log.debug("Got OK response: ${fs}");
					if (xml == null) {
						 // We did not get an xml response
						sendResponse.messageStatus = STATUS_PROTOCOL_ERROR;
						sendResponse.errorData = ErrorCode.NO_XML_SUPPLIED;
						sendResponse.rawData = fs.toString();
						iso18626LogDetails.response(respomseStatus, fs.hasBody ? fs.toString() : null);
					} else {
						// Pass back the raw xml, just in case the caller wants to do anything with it
						sendResponse.rawData = groovy.xml.XmlUtil.serialize(xml);

						// Add an audit record
						iso18626LogDetails.response(respomseStatus, sendResponse.rawData);

						// Now attempt to interpret the result
						GPathResultMap iso18626Response = new GPathResultMap(xml);
						GPathResultMap responseNode = null
						if (iso18626Response.requestConfirmation != null) {
							// We have a response to a request
							responseNode = iso18626Response.requestConfirmation;
						} else if (iso18626Response.supplyingAgencyMessageConfirmation != null) {
							// We have response to a supplier message
							responseNode = iso18626Response.supplyingAgencyMessageConfirmation;
						} else if (iso18626Response.requestingAgencyMessageConfirmation != null) {
							// We have a response to a requester message
							responseNode = iso18626Response.requestingAgencyMessageConfirmation;
						}

						// Did we find a response, by default we mark it as an error
						sendResponse.messageStatus = MessageStatus.ERROR;
						sendResponse.errorData = ErrorCode.NO_CONFIRMATION_ELEMENT_IN_RESPONSE;
						if (responseNode != null) {
							// Do we have a confirmationHeader that conforms to the xsd
							if (responseNode?.confirmationHeader?.messageStatus != null) {
								// We have a confirmation header
								sendResponse.messageStatus = responseNode.confirmationHeader.messageStatus;
								sendResponse.errorData = responseNode.confirmationHeader.errorData;
							} else if (responseNode?.header?.messageStatus != null) {
								// We Have a header that conforms to the standard
								sendResponse.messageStatus = responseNode.header.messageStatus;
								sendResponse.errorData = responseNode.header.errorData;
							}
						}
					}
				}
			}

			log.debug("Got response message: ${iso18626_response}");
		} else {
			log.error("No address for message recipient");
			throw new RuntimeException("No address given for sendISO18626Message: ${messageToSend}");
		}
		return(sendResponse);
	}

	protected Map initialiseAuditMap(String senderSymbol, String receiverSymbol, Iso18626Message iso18626Message) {
		String messageType = null;
		String action = null;
		if (iso18626Message.request != null) {
			messageType = "Request";
		} else if (iso18626Message.requestingAgencyMessage != null) {
			messageType = "RequestingAgencyMessage";
			action = iso18626Message.requestingAgencyMessage.findActionCode();
		} else {
			messageType = "SupplyingAgencyMessage";
			action = iso18626Message.supplyingAgencyMessage.messageInfo.reasonForMessage;
		}
		return([
			senderSymbol: senderSymbol,
			receiverSymbol: receiverSymbol,
			messageType: messageType,
			action: action
		]);
	}

	protected void logMessageAudit(
		Date timeStarted,
		Date timeEnded,
		String address,
		Integer result,
		String message
	) {
		String[] messageParts = [
			'ProtocolMessageAudit',
			result.toString(),
			timeStarted.toString(),
			timeEnded.toString(),
			(timeEnded.getTime() - timeStarted.getTime()).toString(),
			address,
			message.length().toString()
		];
		log.info(messageParts.join(','));
	}

	protected String determineISO18626Action(
		String actionEventCode
	) {
		// Only the first response can be a RequestResponse
		String action = Action.NOTIFICATION; 

		// Do we have an override for the action
		if (actionEventCode != null) {
			switch (actionEventCode) {
				case Actions.ACTION_REQUESTER_REQUESTER_CANCEL:
				case Actions.ACTION_REQUESTER_REQUESTER_REJECT_CONDITIONS:
					action = Action.CANCEL;
					break;

				case Actions.ACTION_REQUESTER_REQUESTER_RECEIVED:
					action = Action.RECEIVED;
					break;

				case Actions.ACTION_REQUESTER_PATRON_RETURNED_ITEM_AND_SHIPPED:
				case Actions.ACTION_REQUESTER_SHIPPED_RETURN:
					action = Action.SHIPPED_RETURN;
					break;
			}
		}

		// Return it to the caller
		return(action);
	}
	
	protected String determineISO18626ReasonForMessage(
		PatronRequest patronRequest,
		String actionEventCode
	) {
		// Only the first response can be a RequestResponse
		String reasonForMessage = patronRequest.sentISO18626RequestResponse ? 
			ReasonForMessage.MESSAGE_REASON_STATUS_CHANGE : 
			ReasonForMessage.MESSAGE_REASON_REQUEST_RESPONSE;

		// Mark the request response as being sent, so we do not try and send it again
		patronRequest.sentISO18626RequestResponse = true;

		// Do we have an override for the reason for message
		if (actionEventCode != null) {
			switch (actionEventCode) {
				case Actions.ACTION_MANUAL_CLOSE:
				case Actions.ACTION_MESSAGE:
				case Actions.ACTION_REQUESTER_EDIT:
				case Actions.ACTION_RESPONDER_SUPPLIER_ADD_CONDITION:
				case Actions.ACTION_RESPONDER_SUPPLIER_CONDITIONAL_SUPPLY:
					reasonForMessage = ReasonForMessage.MESSAGE_REASON_NOTIFICATION;
					break;
					
				case Actions.ACTION_RESPONDER_SUPPLIER_RESPOND_TO_CANCEL:
				case Events.EVENT_STATUS_RES_CANCEL_REQUEST_RECEIVED_INDICATION:
					reasonForMessage = ReasonForMessage.MESSAGE_REASON_CANCEL_RESPONSE;
					break;
			}
		}

		// Return it to the caller
		return(reasonForMessage);
	}
	
	protected String determineISO18626Status(
		String actionEventCode,
		String actionEventResultQualifier
	) {
		String status = null; 

		// Determine the status based on the action / event
		if (actionEventCode != null) {
			switch (actionEventCode) {
				case Actions.ACTION_RESPONDER_SUPPLIER_CHECK_INTO_ILL_AND_MARK_SHIPPED:
				case Actions.ACTION_RESPONDER_SUPPLIER_FILL_DIGITAL_LOAN:
				case Actions.ACTION_RESPONDER_SUPPLIER_MARK_SHIPPED:
					status = Status.LOANED;
					break;

				case Actions.ACTION_RESPONDER_SUPPLIER_RESPOND_TO_CANCEL:
					if (actionEventResultQualifier != ActionEventResultQualifier.QUALIFIER_NO) {
						status = Status.CANCELLED;
					}
					break;

				case Events.EVENT_STATUS_RES_CANCEL_REQUEST_RECEIVED_INDICATION:
					if (actionEventResultQualifier == ActionEventResultQualifier.QUALIFIER_CANCELLED) {
						status = Status.CANCELLED;
					}
					break;

				case Actions.ACTION_RESPONDER_SUPPLIER_CHECKOUT_OF_ILL:
					status = Status.LOAN_COMPLETED;
					break;

				case Events.EVENT_STATUS_RES_AWAIT_DESEQUESTRATION_INDICATION:
				case Events.EVENT_STATUS_RES_OVERDUE_INDICATION:
					status = Status.OVERDUE;
					break;
					
				case Actions.ACTION_RESPONDER_RESPOND_YES:
				case Actions.ACTION_RESPONDER_SUPPLIER_CONDITIONAL_SUPPLY:
					status = Status.EXPECT_TO_SUPPLY;
					break;

				case Actions.ACTION_RESPONDER_SUPPLIER_CANNOT_SUPPLY:
					status = Status.UNFILLED;
					break;

				case Events.EVENT_RESPONDER_NEW_PATRON_REQUEST_INDICATION:
					if (actionEventResultQualifier == ActionEventResultQualifier.QUALIFIER_LOCATED) {
						status = Status.EXPECT_TO_SUPPLY;
					} else if (actionEventResultQualifier == ActionEventResultQualifier.QUALIFIER_UNFILLED) {
						status = Status.UNFILLED;
					}
					break;
			}
		}

		// Return it to the caller
		return(status);
	}
}

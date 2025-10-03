package com.k_int.ill.protocols.iso18626;

import com.k_int.directory.Symbol;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestRota;
import com.k_int.ill.Protocol;
import com.k_int.ill.iso18626.Iso18626Message;
import com.k_int.ill.iso18626.NoteSpecials;
import com.k_int.ill.iso18626.Request;
import com.k_int.ill.iso18626.RequestingAgencyMessage;
import com.k_int.ill.iso18626.SupplyingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.RequestSubType;
import com.k_int.ill.iso18626.codes.closed.RequestType;
import com.k_int.ill.iso18626.codes.closed.ServiceType;
import com.k_int.ill.iso18626.codes.open.BibliographicItemIdentifierCode;
import com.k_int.ill.iso18626.codes.open.BibliographicRecordIdentifierCode;
import com.k_int.ill.iso18626.codes.open.ElectronicAddressType;
import com.k_int.ill.iso18626.codes.open.ServiceLevel;
import com.k_int.ill.iso18626.complexTypes.Address;
import com.k_int.ill.iso18626.complexTypes.AgencyId;
import com.k_int.ill.iso18626.complexTypes.ElectronicAddress;
import com.k_int.ill.iso18626.complexTypes.PhysicalAddress;
import com.k_int.ill.iso18626.types.ActiveSection;
import com.k_int.ill.iso18626.types.BibliographicInfo;
import com.k_int.ill.iso18626.types.BillingInfo;
import com.k_int.ill.iso18626.types.DeliveryInfo;
import com.k_int.ill.iso18626.types.MessageInfo;
import com.k_int.ill.iso18626.types.PatronInfo;
import com.k_int.ill.iso18626.types.PublicationInfo;
import com.k_int.ill.iso18626.types.RequestHeader;
import com.k_int.ill.iso18626.types.RequestedDeliveryInfo;
import com.k_int.ill.iso18626.types.RequestingAgencyHeader;
import com.k_int.ill.iso18626.types.RequestingAgencyInfo;
import com.k_int.ill.iso18626.types.RetryInfo;
import com.k_int.ill.iso18626.types.ReturnInfo;
import com.k_int.ill.iso18626.types.SenderHeader;
import com.k_int.ill.iso18626.types.ServiceInfo;
import com.k_int.ill.iso18626.types.ShippingInfo;
import com.k_int.ill.iso18626.types.StatusInfo;
import com.k_int.ill.iso18626.types.SupplierInfo;
import com.k_int.ill.protocol.ProtocolIdService;
import com.k_int.ill.protocol.ProtocolService;

/**
 * The base class for building an ISO18626 builder
 * The differences between the versions can be found at https://docs.google.com/spreadsheets/d/1BMFX6GsI_7zpu-hPt8Y0KMB2lel1_h5VfKRUdd2U2eM/edit?gid=0#gid=0
 */
public abstract class Iso18626BuilderService {

	Iso18626NotesService iso18626NotesService;
    ProtocolIdService protocolIdService;
	ProtocolService protocolService;

	/**
	 * Builds an ISO18626 message from the passed in details
	 * @param patronRequest the request that the message is to be built from
	 * @param patronRequestRota the current rota record that specifies who is going to receive the message
	 * @param actionOrReason the action or reason for the message to be sent
	 * @param status the status that is to be associated with the message
	 * @param messageParameters any parameters that may influence what is contained in the message
	 * @return an Iso18626Message that is ready to be sent or null if the passed in details do not contain enough information
	 */
	public Iso18626Message build(
		PatronRequest patronRequest,
		PatronRequestRota patronRequestRota,
		String actionOrReason,
		String status,
		Map messageParameters
	) {
		Iso18626Message iso18626Message = null;

		// Have we been supplied a request
		if (patronRequest == null) {
			log.error("Unable to build iso18626 message as no request has been supplied");
		} else {
			// We have been supplied a request
			// Are we dealing with the requester or responder
			if (patronRequest.isRequester) {
				// We are the requester
				// Do we have an action
				if (actionOrReason == null) {
					// We do not, so this must be the initial request
					iso18626Message = buildRequestMessage(patronRequest, patronRequestRota);
				} else {
					iso18626Message = buildRequestingAgencyMessage(patronRequest, actionOrReason, messageParameters);
				}
			} else {
				// We are the supplier so we need to build a supplier agency message
				iso18626Message = buildSupplyingAgencyMessage(patronRequest, actionOrReason, status, messageParameters);
			}

			// Set the version of the protocol we are using			
			iso18626Message.updateVersionInUse(getProtocolVersion());
		}

		// Return the built Iso18626Message to the caller
		return(iso18626Message);
	}

	/**
	 * Obtains the version of the protocol to be contained in the message
	 * This method should always be overridden, so should become abstract when this becomes an abstract class
	 * @return the version of the protocol being used
	 */
	protected abstract String getProtocolVersion();

	/**
	 * Returns the code for the protocol, this should always be overridden
	 * @return The protocol code
	 */
	public abstract String getProtocolCode();

	/**
	 * Builds the Iso18626Message request message from the patron request and rota
	 * @param patronRequest the patron request that will be used to build the message
	 * @param patronRequestRota the rota record that will be used to build the message
	 * @return an Iso18626Message instance that represents a request message for this patron request 
	 */
	public Iso18626Message buildRequestMessage(PatronRequest patronRequest, PatronRequestRota patronRequestRota) {

		Request request = new Request(
			buildRequestHeader(patronRequest),
			buildBibliograpicInfo(patronRequest, patronRequestRota),
			buildPublicationInfo(patronRequest),
			buildServiceInfo(patronRequest),
			buildRequestingAgencyInfo(patronRequest),
			buildPatronInfo(patronRequest),
			buildBillingInfo(patronRequest)
		);
		request.addSupplierInfo(buildSupplierInfo(patronRequest));
		request.addRequestedDeliveryInfo(buildRequestedDeliveryInfo(patronRequest));

		return(new Iso18626Message(request));
	}

	/**
	 * Builds the Iso18626Message requesting agency message from the patron request, action and message parameters
	 * @param patronRequest the patron request that will be used to build the message
	 * @param action the action that has generated this message
	 * @param messageParameters the parameters passed through with the message
	 * @return an Iso18626Message instance that represents a requesting agency message for this patron request 
	 */
	public Iso18626Message buildRequestingAgencyMessage(PatronRequest patronRequest, String action, Map messageParameters) {
		return(new Iso18626Message(new RequestingAgencyMessage(
			buildRequestingAgencyHeader(patronRequest),
			buildActiveSection(patronRequest, action, messageParameters)
		)));
	}

	/**
	 * Builds the Iso18626Message supplying agency message from the patron request, reason, status and message parameters
	 * @param patronRequest the patron request that will be used to build the message
	 * @param reason the reason that has generated this message
	 * @param status the status that the iso18626 request is in
	 * @param messageParameters the parameters passed through with the message
	 * @return an Iso18626Message instance that represents a supplying agency message for this patron request 
	 */
	public Iso18626Message buildSupplyingAgencyMessage(
		PatronRequest patronRequest,
		String reason,
		String status,
		Map messageParameters
	) {
		SupplyingAgencyMessage supplyingAgencyMessage = new SupplyingAgencyMessage(
			buildSenderHeader(patronRequest),
			buildMessageInfo(patronRequest, reason, messageParameters),
			buildStatusInfo(patronRequest, status),
			buildDeliveryInfo(patronRequest, messageParameters),
			buildReturnInfo(patronRequest)
		);
	

		// Give them a chance to modify it
		modifySupplyingAgencyMessage(
			supplyingAgencyMessage,
			patronRequest
		);

		// Return the result to the caller
		return(new Iso18626Message(supplyingAgencyMessage));
	}
	
	/**
	 * Override this method if there is version specific information to populate the SupplyingAgencyMessage instance
	 * @param supplyingAgencyMessage the SupplyingAgencyMessage instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifySupplyingAgencyMessage(
		SupplyingAgencyMessage supplyingAgencyMessage,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Retrieves the protocol record for the protocol in use
	 * @return The protocol record
	 */
	public Protocol getProtocol() {
		return(protocolService.getProtocol(getProtocolCode()));
	}

	/**
	 * Builds the request header object
	 * @param patronRequest the request to build the object from
	 * @return the built request header
	 */
	protected RequestHeader buildRequestHeader(PatronRequest patronRequest) {
		RequestHeader requestHeader = new RequestHeader(
			new AgencyId(textAuthority(patronRequest.resolvedSupplier), textSymbol(patronRequest.resolvedSupplier)),
			new AgencyId(textAuthority(patronRequest.resolvedRequester), textSymbol(patronRequest.resolvedRequester)),
			protocolIdService.buildProtocolId(patronRequest),
			null,
			null
		);

		// Allow any version specific stuff to be added		
		modifyRequestHeader(requestHeader, patronRequest);

		// return the header to the caller		
		return(requestHeader);
	}

	/**
	 * Any version specific details can be added to the RequestHeader by overriding this class
	 * @param requestHeader the request header that can be modified
	 * @param patronRequest the request that was used to populate the base details
	 */
	protected void modifyRequestHeader(
		RequestHeader requestHeader,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the BibliographicInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @param patronRequestRota the supplier who will be receiving the request
	 * @return The built BibliographicInfo instance
	 */
	protected BibliographicInfo buildBibliograpicInfo(
		PatronRequest patronRequest,
		PatronRequestRota patronRequestRota
	) {
		BibliographicInfo bibliographicInfo = new BibliographicInfo(
			patronRequestRota.instanceIdentifier,
			patronRequest.title,
			patronRequest.author,
			patronRequest.subtitle,
			null,
			patronRequest.edition,
			patronRequest.titleOfComponent,
			patronRequest.authorOfComponent,
			patronRequest.volume,
			patronRequest.issue,
			patronRequest.startPage,
			numberOfPages(patronRequest),
			patronRequest.sponsor,
			patronRequest.informationSource
		);

		// Now the item identifiers
		bibliographicInfo.addBibliographicItemIdentifier(
			BibliographicItemIdentifierCode.ISSN,
			patronRequest.issn
		);
		bibliographicInfo.addBibliographicItemIdentifier(
			BibliographicItemIdentifierCode.ISBN,
			patronRequest.isbn
		);
		bibliographicInfo.addBibliographicItemIdentifier(
			null,
			patronRequest.systemInstanceIdentifier
		);

		// The record identifiers
		bibliographicInfo.addBibliographicRecordIdentifier(
			BibliographicRecordIdentifierCode.OCLC,
			patronRequest.oclcNumber
		);

		// Give them a chance to modify it
		modifyBibliograpicInfo(
			bibliographicInfo,
			patronRequest,
			patronRequestRota
		);

		// Return the result to the caller
		return(bibliographicInfo);
	}

	/**
	 * Override this method if there is version specific information to populate the BibliograpicInfo instance 
	 * @param bibliographicInfo the BibliographicInfo instance that is being built 
	 * @param patronRequest the request to build the object from
	 * @param patronRequestRota the supplier who will be receiving the request
	 */
	protected void modifyBibliograpicInfo(
		BibliographicInfo bibliographicInfo,
		PatronRequest patronRequest,
		PatronRequestRota patronRequestRota
	) {
		// By default we do nothing
	}

	/**
	 * Builds the PublicationInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built PublicationInfo instance
	 */
	protected PublicationInfo buildPublicationInfo(PatronRequest patronRequest) {
        PublicationInfo publicationInfo = null;
        if ((patronRequest.publisher != null) ||
            (patronRequest.publicationType != null) ||
            (patronRequest.publicationDate != null) ||
            (patronRequest.placeOfPublication != null)) {
            publicationInfo = new PublicationInfo(
                patronRequest.publisher,
                patronRequest.publicationType?.value,
                patronRequest.publicationDate,
                patronRequest.placeOfPublication
            );

			// Give them a chance to modify it
			modifyPublicationInfo(
				publicationInfo,
				patronRequest
			);
        }

		// Return the result to the caller
		return(publicationInfo);
	}

	/**
	 * Override this method if there is version specific information to populate the PublicationInfo instance 
	 * @param publicationInfo the PublicationInfo instance that is being built 
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyPublicationInfo(
		PublicationInfo publicationInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the ServiceInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built ServiceInfo instance
	 */
	protected ServiceInfo buildServiceInfo(PatronRequest patronRequest) {
        String note = buildNote(patronRequest, patronRequest.patronNote);
        if (patronRequest.patronReference) {
            // We have a patron reference, so this needs to be appended to the note
            note += (NoteSpecials.ILL_FIELD_PATRON_REFERENCE_PREFIX + patronRequest.patronReference + NoteSpecials.SPECIAL_WRAPPER);
        }
        if (patronRequest.publicationDateOfComponent) {
            // We have a publication date of component, so this needs to be appended to the note
            note += (NoteSpecials.ILL_FIELD_PUBLICATION_DATE_OF_COMPONENT_PREFIX + patronRequest.publicationDateOfComponent + NoteSpecials.SPECIAL_WRAPPER);
        }

		// Lookup the protocol version of the service type
		String serviceType = protocolService.getServiceTypeValue(getProtocol(), patronRequest.serviceType?.label);

        // Now we have built the note, we can build the service info
		ServiceInfo serviceInfo = new ServiceInfo(
			RequestType.NEW,
			null,
			serviceType == null ? ServiceType.LOAN : serviceType,
			ServiceLevel.NORMAL,
			null,
			patronRequest.neededBy.toString(),
			(patronRequest.copyright == null) ? 
				null : 
				((patronRequest.copyright.copyrightMessage == null) ? 
					null :
					patronRequest.copyright.copyrightMessage.code),
			true,
			null,
			null,
			note
		);

		// Add the request sub type
		serviceInfo.addRequestSubType(RequestSubType.PATRON_REQUEST );

		// Give them a chance to modify it
		modifyServiceInfo(
			serviceInfo,
			patronRequest
		);

		// Return the result to the caller
		return(serviceInfo);
	}

	/**
	 * Override this method if there is version specific information to populate the ServiceInfo instance 
	 * @param serviceInfo the ServiceInfo instance that is being built 
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyServiceInfo(
		ServiceInfo serviceInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the SupplierInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built SupplierInfo instance
	 */
	protected SupplierInfo buildSupplierInfo(PatronRequest patronRequest) {
		SupplierInfo supplierInfo = new SupplierInfo(
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);

		// Give them a chance to modify it
		modifySupplierInfo(
			supplierInfo,
			patronRequest
		);
		
		// Return the result to the caller
		return(supplierInfo);
	}
	
	/**
	 * Override this method if there is version specific information to populate the SupplierInfo instance
	 * @param serviceInfo the SupplierInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifySupplierInfo(
		SupplierInfo supplierInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the RequestedDeliveryInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built RequestedDeliveryInfo instance
	 */
	protected RequestedDeliveryInfo buildRequestedDeliveryInfo(PatronRequest patronRequest) {
		RequestedDeliveryInfo requestedDeliveryInfo = null;
		Address address = null;
		if (patronRequest?.deliveryMethod?.value == ElectronicAddressType.URL) {
			address = new Address(
				new ElectronicAddress(
					patronRequest.deliveryMethod.label,
					null
				)
			);
		} else if (patronRequest.pickupLocation != null) {
			address = new Address(
				new PhysicalAddress(
					patronRequest.pickupLocation,
					null,
					null,
					null,
					null,
					null
				)
			);
		}

		if (address != null) {
			 requestedDeliveryInfo = new RequestedDeliveryInfo(
				1,
				address
			);
			
			// Give them a chance to modify it
			modifyRequestedDeliveryInfo(
				requestedDeliveryInfo,
				patronRequest
			);
		}

		// Return the result to the caller
		return(requestedDeliveryInfo);
	}

	/**
	 * Override this method if there is version specific information to populate the RequestedDeliveryInfo instance
	 * @param requestedDeliveryInfo the RequestedDeliveryInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyRequestedDeliveryInfo(
		RequestedDeliveryInfo requestedDeliveryInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the RequestingAgencyInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built RequestingAgencyInfo instance
	 */
	protected RequestingAgencyInfo buildRequestingAgencyInfo(PatronRequest patronRequest) {
		RequestingAgencyInfo requestingAgencyInfo = new RequestingAgencyInfo(
			null,
			null
		);
		// Add address here
		// requestingAgencyInfo.addAddress(address);

		// Give them a chance to modify it
		modifyRequestingAgencyInfo(
			requestingAgencyInfo,
			patronRequest
		);

		// Return the result to the caller
		return(requestingAgencyInfo);
	}
	
	/**
	 * Override this method if there is version specific information to populate the RequestingAgencyInfo instance
	 * @param requestingAgencyInfo the RequestingAgencyInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyRequestingAgencyInfo(
		RequestingAgencyInfo requestingAgencyInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the PatronInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built PatronInfo instance
	 */
	protected PatronInfo buildPatronInfo(PatronRequest patronRequest) {
		PatronInfo patronInfo = new PatronInfo(
			patronRequest.patronIdentifier,
			patronRequest.patronSurname,
			patronRequest.patronGivenName,
			patronRequest.patronType,
			false
		);
		// Add address here
		// patronInfo.addAddress(address);

		// Give them a chance to modify it
		modifyPatronInfo(
			patronInfo,
			patronRequest
		);

		// Return the result to the caller
		return(patronInfo);
	}

	/**
	 * Override this method if there is version specific information to populate the PatronInfo instance
	 * @param patronInfo the PatronInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyPatronInfo(
		PatronInfo patronInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the BillingInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built BillingInfo instance
	 */
	protected BillingInfo buildBillingInfo(patronRequest) {
		BillingInfo billingInfo = new BillingInfo(
			null,
			null,
			null,
			null,
			null
		);

		// Give them a chance to modify it
		modifyBillingInfo(
			billingInfo,
			patronRequest
		);

		// Return the result to the caller
		return(billingInfo);
	}
	
	/**
	 * Override this method if there is version specific information to populate the BillingInfo instance
	 * @param billingInfo the BillingInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyBillingInfo(
		BillingInfo billingInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the SenderHeader instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built SenderHeader instance
	 */
	protected SenderHeader buildSenderHeader(PatronRequest patronRequest) {
		SenderHeader senderHeader = new SenderHeader(
			new AgencyId(textAuthority(patronRequest.resolvedSupplier), textSymbol(patronRequest.resolvedSupplier)),
			new AgencyId(textAuthority(patronRequest.resolvedRequester), textSymbol(patronRequest.resolvedRequester)),
			patronRequest.peerRequestIdentifier,
			patronRequest.id
		);

		// Give them a chance to modify it
		modifySenderHeader(
			senderHeader,
			patronRequest
		);

		// Return the result to the caller
		return(senderHeader);
	}
	
	/**
	 * Override this method if there is version specific information to populate the SenderHeader instance
	 * @param senderHeader the SenderHeader instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifySenderHeader(
		SenderHeader senderHeader,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the MessageInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @param reason the reason for the message
	 * @param messageParameters parameters supplied for this message 
	 * @return The built MessageInfo instance
	 */
	protected MessageInfo buildMessageInfo(
		PatronRequest patronRequest,
		String reason,
		Map messageParameters
	) {
		// If we have a url we need to add it to the note in a special as there is no other way to transmit it
		String note = messageParameters?.note;
		if (messageParameters?.url) {
			if (note == null) {
				note = "";
			}
			note += NoteSpecials.DELIVERY_URL + messageParameters?.url + NoteSpecials.SPECIAL_WRAPPER;
		}

		// Now we can generate the MessageInfo
		MessageInfo messageInfo = new MessageInfo(
			reason,
			messageParameters?.cancelResponse ? messageParameters.cancelResponse == "yes" : false,
			buildNote(patronRequest, note),
			messageParameters?.reason,
			null,
			null,
			null,
			null
		);

		// Give them a chance to modify it
		modifyMessageInfo(
			messageInfo,
			patronRequest,
			reason,
			messageParameters
		);

		// Return the result to the caller
		return(messageInfo);
	}
	
	/**
	 * Override this method if there is version specific information to populate the MessageInfo instance
	 * @param messageInfo the MessageInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 * @param reason the reason for the message
	 * @param messageParameters parameters supplied for this message 
	 */
	protected void modifyMessageInfo(
		MessageInfo messageInfo,
		PatronRequest patronRequest,
		String reason,
		Map messageParameters
	) {
		// By default we do nothing
	}

	/**
	 * Builds the StatusInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @param status the status for the message
	 * @return The built StatusInfo instance
	 */
	protected StatusInfo buildStatusInfo(PatronRequest patronRequest, String status) {
        StatusInfo statusInfo = null;
        if ((status != null) ||
            (patronRequest.dueDateRS != null)) {
            statusInfo = new StatusInfo(
    			status,
    			null,
    			patronRequest.dueDateRS,
    			null
    		);
        }

		// Give them a chance to modify it
		modifyStatusInfo(
			statusInfo,
			patronRequest,
			status
		);

		// Return the result to the caller
        return(statusInfo);
	}

	/**
	 * Override this method if there is version specific information to populate the StatusInfo instance
	 * @param statusInfo the StatusInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 * @param status the status for the message
	 */
	protected void modifyStatusInfo(
		StatusInfo statusInfo,
		PatronRequest patronRequest,
		String status
	) {
		// By default we do nothing
	}

	/**
	 * Builds the RetryInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @param messageParameters parameters supplied for this message 
	 * @return The built RetryInfo instance
	 */
	protected RetryInfo buildRetryInfo(PatronRequest patronRequest) {
		RetryInfo retryInfo = new RetryInfo(
			null,
			null,
			null
		);

		// Give them a chance to modify it
		modifyRetryInfo(
			retryInfo,
			patronRequest
		);

		// Return the result to the caller
		return(retryInfo);
	}
	
	/**
	 * Override this method if there is version specific information to populate the RetryInfo instance
	 * @param retryInfo the RetryInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyRetryInfo(
		RetryInfo retryInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the DeliveryInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @param messageParameters parameters supplied for this message 
	 * @return The built DeliveryInfo instance
	 */
	protected DeliveryInfo buildDeliveryInfo(PatronRequest patronRequest, Map messageParameters) {
		// Now we have determine the item id we can generate the DeliveryInfo
		DeliveryInfo deliveryInfo = new DeliveryInfo(
			null,
			null, // This is different on the different versions so we do not set it here
			null,
			false,
			messageParameters?.loanCondition,
			null,
			null
		);

		// Give them a chance to modify it
		modifyDeliveryInfo(
			deliveryInfo,
			patronRequest,
			messageParameters
		);

		// Return the result to the caller
		return(deliveryInfo);
	}

	/**
	 * Override this method if there is version specific information to populate the DeliveryInfo instance
	 * @param deliveryInfo the DeliveryInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 * @param messageParameters parameters supplied for this message 
	 */
	protected void modifyDeliveryInfo(
		DeliveryInfo deliveryInfo,
		PatronRequest patronRequest,
		Map messageParameters
	) {
		// By default we do nothing
	}

	/**
	 * Builds the ShippingInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built ShippingInfo instance
	 */
	protected ShippingInfo buildShippingInfo(PatronRequest patronRequest) {
		// Now we have determine the item id we can generate the ShippingInfo
		ShippingInfo shippingInfo = new ShippingInfo(
			null,
			null,
			null,
			null
		);

		// Give them a chance to modify it
		modifyShippingInfo(
			shippingInfo,
			patronRequest
		);

		// Return the result to the caller
		return(shippingInfo);
	}

	/**
	 * Override this method if there is version specific information to populate the ShippingInfo instance
	 * @param deliveryInfo the ShippingInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyShippingInfo(
		ShippingInfo modifyShippingInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the ReturnInfo instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built ReturnInfo instance
	 */
	protected ReturnInfo buildReturnInfo(PatronRequest patronRequest) {
		ReturnInfo returnInfo = new ReturnInfo(
			null,
			null,
			null
		);

		// Give them a chance to modify it
		modifyReturnInfo(
			returnInfo,
			patronRequest
		);

		// Return the result to the caller
		return(returnInfo);
	}
	
	/**
	 * Override this method if there is version specific information to populate the ReturnInfo instance
	 * @param returnInfo the ReturnInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyReturnInfo(
		ReturnInfo returnInfo,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the RequestingAgencyHeader instance for the request
	 * @param patronRequest the request to build the instance from
	 * @return The built RequestingAgencyHeader instance
	 */
	protected RequestingAgencyHeader buildRequestingAgencyHeader(PatronRequest patronRequest) {
		RequestingAgencyHeader requestingAgencyHeader = new RequestingAgencyHeader(
			new AgencyId(textAuthority(patronRequest.resolvedSupplier), textSymbol(patronRequest.resolvedSupplier)),
			new AgencyId(textAuthority(patronRequest.resolvedRequester), textSymbol(patronRequest.resolvedRequester)),
			protocolIdService.buildProtocolId(patronRequest),
			patronRequest.peerRequestIdentifier,
			null
		);

		// Give them a chance to modify it
		modifyRequestingAgencyHeader(
			requestingAgencyHeader,
			patronRequest
		);

		// Return the result to the caller
		return(requestingAgencyHeader);
	}

	/**
	 * Override this method if there is version specific information to populate the RequestingAgencyHeader instance
	 * @param requestingAgencyHeader the ReturnInfo instance that is being built
	 * @param patronRequest the request to build the instance from
	 */
	protected void modifyRequestingAgencyHeader(
		RequestingAgencyHeader requestingAgencyHeader,
		PatronRequest patronRequest
	) {
		// By default we do nothing
	}

	/**
	 * Builds the ActiveSection instance for the request
	 * @param patronRequest the request to build the instance from
	 * @param action the action that was performed
	 * @param messageParameters the message parameters that were supplied
	 * @return The built ActiveSection instance
	 */
	protected ActiveSection buildActiveSection(PatronRequest patronRequest, String action, Map messageParameters) {
		ActiveSection activeSection = new ActiveSection(
			action,
			buildNote(patronRequest, messageParameters?.note)
		);

		// Give them a chance to modify it
		modifyActiveSection(
			activeSection,
			patronRequest,
			action,
			messageParameters
		);

		// Return the result to the caller
		return(activeSection);
	}
	
	/**
	 * Override this method if there is version specific information to populate the ActiveSection instance
	 * @param activeSection the ActiveSection instance that is being built
	 * @param patronRequest the request to build the instance from
	 * @param action the action that was performed
	 * @param messageParameters the message parameters that were supplied
	 */
	protected void modifyActiveSection(
		ActiveSection activeSection,
		PatronRequest patronRequest,
		String action,
		Map messageParameters
	) {
		// By default we do nothing
	}

	protected String textAuthority(Symbol symbol) {
		return(symbol?.authority?.symbol);
	}

	protected String textSymbol(Symbol symbol) {
		return(symbol?.symbol);
	}

	protected Integer numberOfPages(PatronRequest patronRequest) {
		Integer result = null;
		if (patronRequest.numberOfPages != null) {
			try {
				result = patronRequest.numberOfPages.toInteger();
			} catch (Exception) {
				log.info("Exception ignored, Number of pages \"" + patronRequest.numberOfPages + "\" is not an integer for request: " + patronRequest.hrid);
			}
		}
		return(result);
	}

    protected String buildNote(PatronRequest request, String note, boolean appendSequence = true) {
        String constructedNote = note;

        // Now do we need to append the sequence
		if (appendSequence) {
			String lastSequence = iso18626NotesService.buildSequence(request.incrementLastSequence()); 
			
			if (constructedNote == null) {
				constructedNote = lastSequence;
			} else {
				constructedNote += lastSequence;
			}
		}
		return(constructedNote);
	}
}

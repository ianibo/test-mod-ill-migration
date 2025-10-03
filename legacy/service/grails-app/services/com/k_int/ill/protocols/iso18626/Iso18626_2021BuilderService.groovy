package com.k_int.ill.protocols.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestRota;
import com.k_int.ill.RequestVolume;
import com.k_int.ill.constants.Protocol;
import com.k_int.ill.iso18626.Iso18626Message;
import com.k_int.ill.iso18626.SupplyingAgencyMessage;
import com.k_int.ill.iso18626.codes.open.ItemFormat;
import com.k_int.ill.iso18626.complexTypes.AgencyId;
import com.k_int.ill.iso18626.types.BibliographicInfo;
import com.k_int.ill.iso18626.types.DeliveryInfo;
import com.k_int.ill.iso18626.types.PublicationInfo;
import com.k_int.ill.iso18626.types.RequestHeader;
import com.k_int.ill.iso18626.types.ServiceInfo;

public class Iso18626_2021BuilderService extends Iso18626BuilderService {

	@Override
	public String getProtocolCode() {
		return(Protocol.ISO18626_2021);
	}

	@Override
	protected String getProtocolVersion() {
		return(Iso18626Message.VERSION_2021_2);
	}

	@Override
	protected void modifySupplyingAgencyMessage(
		SupplyingAgencyMessage supplyingAgencyMessage,
		PatronRequest patronRequest
	) {
		// Set the retryInfo
		supplyingAgencyMessage.retryInfo = buildRetryInfo(patronRequest);
	
		// Set the shippingInfo
		supplyingAgencyMessage.shippingInfo = buildShippingInfo(patronRequest);
	}

	@Override
	protected void modifyRequestHeader(
		RequestHeader requestHeader,
		PatronRequest patronRequest
	) {
		// This is the opportunity to set the consortial id
		// TODO: reset this to null after tests
		requestHeader.consortialId = new AgencyId("ISIL", "notSet");
	}

	@Override
	protected void modifyBibliograpicInfo(
		BibliographicInfo bibliographicInfo,
		PatronRequest patronRequest,
		PatronRequestRota patronRequestRota
	) {
		// This is the opportunity to set the authorId
		//bibliographicInfo.authorId = null;
	}

	@Override
	protected void modifyPublicationInfo(
		PublicationInfo publicationInfo,
		PatronRequest patronRequest
	) {
		// This is the opportunity to set the publicationId
		//publicationInfo.publicationId = null;
	}

	@Override
	protected void modifyServiceInfo(
		ServiceInfo serviceInfo,
		PatronRequest patronRequest
	) {
		// This is the opportunity to set the itemFormat, preferredEdition and loanCondition 
		//serviceInfo.itemFormat = null;
		//serviceInfo.preferredEdition = null;
		//addLoanCondition("xxx");
	}

	@Override
	protected void modifyDeliveryInfo(
		DeliveryInfo deliveryInfo,
		PatronRequest patronRequest,
		Map messageParameters
	) {
		// We need to build up the item ids as there maybe multiple
		String itemId = null;
		patronRequest.volumes.each { RequestVolume requestVolume -> 
			deliveryInfo.addItemId(requestVolume.itemId);
		}
		if (messageParameters?.deliveredFormat != null) {
			deliveryInfo.itemFormat = new ItemFormat(messageParameters.deliveredFormat);
		}
	}
}

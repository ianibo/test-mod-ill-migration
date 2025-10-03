package com.k_int.ill.protocols.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.constants.Protocol;
import com.k_int.ill.iso18626.Iso18626Message;
import com.k_int.ill.iso18626.types.DeliveryInfo;
import com.k_int.ill.iso18626.types.MessageInfo;
import com.k_int.ill.iso18626.types.SenderHeader;
import com.k_int.ill.iso18626.types.ServiceInfo;

public class Iso18626_2017BuilderService extends Iso18626BuilderService {

	@Override
	public String getProtocolCode() {
		return(Protocol.ISO18626_2017);
	}

	@Override
	protected String getProtocolVersion() {
		return(Iso18626Message.VERSION_2017_1);
	}

	@Override
	protected void modifyServiceInfo(
		ServiceInfo serviceInfo,
		PatronRequest patronRequest
	) {
		// This is the opportunity to set the preferredFormat
		//serviceInfo.preferredFormat = null;
	}

	@Override
	protected void modifyMessageInfo(
		MessageInfo messageInfo,
		PatronRequest patronRequest,
		String reason,
		Map messageParameters
	) {
		// This is the opportunity to set the offeredCosts, retryAfter and retryBefore
		//senderHeader.offeredCosts = null;
		//senderHeader.retryAfter = null;
		//senderHeader.retryBefore = null;
	}

	@Override
	protected void modifyDeliveryInfo(
		DeliveryInfo deliveryInfo,
		PatronRequest patronRequest,
		Map messageParameters
	) {
		// We need to build up the item ids as there maybe multiple
		String itemId = null;
		switch (patronRequest.volumes.size()) {
			case 0:
				// No volumes checked out, so nothing to do
				break;

			case 1:
				// We have 1 item checked out
				itemId = patronRequest.volumes[0].itemId;
				break;

			default:
				itemId = patronRequest.volumes.collect { vol -> "multivol:${vol.name},${vol.itemId}" };
				break;
		}
		deliveryInfo.addItemId(itemId);
		deliveryInfo.deliveredFormat = messageParameters?.deliveredFormat;
	}
}

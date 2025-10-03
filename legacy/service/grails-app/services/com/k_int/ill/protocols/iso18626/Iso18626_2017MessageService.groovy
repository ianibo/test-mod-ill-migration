package com.k_int.ill.protocols.iso18626;

import com.k_int.ill.Protocol;
import com.k_int.ill.constants.Directory;

public class Iso18626_2017MessageService extends Iso18626MessageService {

	Iso18626_2017BuilderService iso18626_2017BuilderService;

	@Override
	public Protocol getProtocol() {
		return(iso18626_2017BuilderService.getProtocol());
	}

	@Override
	public String getProtocolServiceType() {
		return(Directory.SERVICE_TYPE_ISO18626_2017);
	}

	@Override
	public Iso18626BuilderService getBuilder() {
		return(iso18626_2017BuilderService);
	}
}

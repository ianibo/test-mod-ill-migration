package com.k_int.ill.referenceData;

import com.k_int.ill.constants.PropertyContext;
import com.k_int.ill.constants.ServiceType;
import com.k_int.ill.referenceData.protocolActionEvent.ISO18626ProtocolActionEvents;
import com.k_int.ill.referenceData.protocolActionEvent.IllSmtpProtocolActionEvents;
import com.k_int.ill.Protocol;
import com.k_int.ill.ProtocolConversion;

import groovy.util.logging.Slf4j;

/**
 * Loads the ActionEvent data required for the system to process requests
 */
@Slf4j
public class ProtocolData {

	private void load() {
		log.info("Adding protocols to the database");

		loadIso18626();
		loadIllSMTP();
	}
	
	private void loadIso18626() {
		log.info("Adding protocol ISO18626");
		
		Protocol iso18626_2017Protocol = Protocol.ensure(
			com.k_int.ill.constants.Protocol.ISO18626_2017,
			"ISO18626 (2017) protocol for inter library loans",
			new ISO18626ProtocolActionEvents()
		);
		
		Protocol iso18626_2021Protocol = Protocol.ensure(
			com.k_int.ill.constants.Protocol.ISO18626_2021,
			"ISO18626 (2021) protocol for inter library loans",
			new ISO18626ProtocolActionEvents()
		);
		
		// The service types
		ProtocolConversion.ensure(iso18626_2017Protocol, PropertyContext.SERVICE_TYPE, ServiceType.COPY, com.k_int.ill.iso18626.codes.closed.ServiceType.COPY);
		ProtocolConversion.ensure(iso18626_2017Protocol, PropertyContext.SERVICE_TYPE, ServiceType.LOAN, com.k_int.ill.iso18626.codes.closed.ServiceType.LOAN);
		ProtocolConversion.ensure(iso18626_2021Protocol, PropertyContext.SERVICE_TYPE, ServiceType.COPY, com.k_int.ill.iso18626.codes.closed.ServiceType.COPY);
		ProtocolConversion.ensure(iso18626_2021Protocol, PropertyContext.SERVICE_TYPE, ServiceType.LOAN, com.k_int.ill.iso18626.codes.closed.ServiceType.LOAN);
	} 

	private void loadIllSMTP() {
		log.info("Adding protocol ILL SMTP");
		
		Protocol iso18626Protocol = Protocol.ensure(
			com.k_int.ill.constants.Protocol.ILL_SMTP,
			"ILL SMTP protocol for inter library loans",
			new IllSmtpProtocolActionEvents()
		);
	} 

	public static void loadAll() {
		(new ProtocolData()).load();
	}
}

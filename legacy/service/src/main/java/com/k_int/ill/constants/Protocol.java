package com.k_int.ill.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that contains the constants used to define the protocols we use
 * @author Chas
 *
 */
public class Protocol {

    public static final String ILL_SMTP      = "ILL_SMTP";
    public static final String ISO18626_2017 = "ISO18626-2017";
    public static final String ISO18626_2021 = "ISO18626-2021";

	/** all the ill service types that we have implemented */	
	public static final List<String> PROTOCOL_SERVICE_TYPES = new ArrayList<String>();

	/** All the ISO18626 variants */
	public static final List<String> ISO18626_VARIANTS = new ArrayList<String>();

	/** Mapping between the protocol and the directory service type */
	public static final Map<String, String> serviceTypeProtocol = new HashMap<String, String>();

	static {
		PROTOCOL_SERVICE_TYPES.add(Directory.SERVICE_TYPE_ILL_SMTP);
		PROTOCOL_SERVICE_TYPES.add(Directory.SERVICE_TYPE_ISO18626_2017);
		PROTOCOL_SERVICE_TYPES.add(Directory.SERVICE_TYPE_ISO18626_2021);

		ISO18626_VARIANTS.add(ISO18626_2017);
		ISO18626_VARIANTS.add(ISO18626_2021);
		
		serviceTypeProtocol.put(Directory.SERVICE_TYPE_ISO18626_2017, ISO18626_2017);
		serviceTypeProtocol.put(Directory.SERVICE_TYPE_ISO18626_2021, ISO18626_2021);
		serviceTypeProtocol.put(Directory.SERVICE_TYPE_ILL_SMTP, ILL_SMTP);
	}
}

package com.k_int.ill;

enum ProtocolType {

    /** The ill iso 18626 protocol for lending items between libraries */
    ISO18626,

    /** The library circulation protocol */
    NCIP,
	
	/** Shared index */
	SHARED_INDEX,
	
    /** Uses Z3950 to see who holds the item to build the rota */
    Z3950_REQUESTER,

    /** Uses Z3950 to look to see if the library holds the item */
    Z3950_RESPONDER
}

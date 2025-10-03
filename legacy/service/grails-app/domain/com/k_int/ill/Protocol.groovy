package com.k_int.ill

import com.k_int.ill.referenceData.protocolActionEvent.BaseProtocolActionEvent;
import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class Protocol implements MultiTenant<Protocol> {

	String id;
	String code;
	String description;
	
	static hasMany = [
		protocolConversions : ProtocolConversion,
		actionEvents : ProtocolActionEvent
	];

	static constraints = {
		       code (nullable: false, blank : false, unique: true)
		description (nullable: false, blank : false)
	}
	
    static mapping = {
                         id column : 'p_id',          length : 36, generator : 'uuid2'
                    version false
                       code column : 'p_code',        length : 32
                description column : 'p_description', length : 255
        protocolConversions cascade: 'all-delete-orphan'
               actionEvents cascade: 'all-delete-orphan'
    }

    public static Protocol ensure(
        String code,
        String description,
		BaseProtocolActionEvent protocolActionEvents
    ) {
        // Lookup to see if the code exists
        Protocol protocol = findByCode(code);

        // Did we find it
        if (protocol == null) {
            // No we did not, so create a new one
            protocol = new Protocol (
                code: code
            );
        }

        // Just update the other fields as something may have changed
        protocol.description = description;

		// Do we have any action events to load
		if (protocolActionEvents != null) {
			// We do so load them
			protocolActionEvents.load(protocol);
		}

        // and save it
        protocol.save(flush:true, failOnError:true);

        // Return the protocol to the caller
        return(protocol);
    }
}

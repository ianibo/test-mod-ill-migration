package com.k_int.ill

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class ProtocolConversion implements MultiTenant<ProtocolConversion> {

	/** internal ID of the conversion record */
	String id;

	/** The context of this mapping */
	String context;
	
	/** the internal value we use that needs mapping to a protocol value */
	String internalValue;

    /** The value the reference data will be converted to for the specified protocol */
    String protocolValue;

	static belongsTo = [protocol : Protocol];

    static constraints = {
             protocol (nullable : false)
              context (nullable : false, blank : false)
        internalValue (nullable : false, blank : false, unique: [ 'context', 'protocol' ])
        protocolValue (nullable : false, blank : true)
    }

    static mapping = {
		        table "protocol_conversion"
                   id column : 'pc_id', generator : 'uuid2', length : 36
              version false
             protocol column : 'pc_protocol'
              context column : 'pc_context', length : 32
        internalValue column : 'pc_internal_value', length : 32
        protocolValue column : 'pc_protocol_value', length : 255
    }

    public static ProtocolConversion ensure(
        Protocol protocol,
		String context,
        String internalValue,
		String protocolValue
    ) {
        // Lookup to see if this conversion already exists
        ProtocolConversion protocolConversion = findByProtocolAndContextAndInternalValue(protocol, context, internalValue);

        // Did we find it
        if (protocolConversion == null) {
            // No we did not, so create a new one
            protocolConversion = new ProtocolConversion (
                protocol: protocol,
				context: context,
				internalValue: internalValue
            );
        }

        // Just update the other fields as something may have changed
        protocolConversion.protocolValue = protocolValue;

        // and save it
        protocolConversion.save(flush:true, failOnError:true);

        // Return the protocolConversion to the caller
        return(protocolConversion);
    }
}

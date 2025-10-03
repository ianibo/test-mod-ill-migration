package com.k_int.ill

import com.k_int.ill.statemodel.ActionEvent;
import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class ProtocolActionEvent implements MultiTenant<ProtocolActionEvent> {

	String id;

	/** The protocol that supports this action / event */	
	Protocol protocol;

	/** The action / event that is supported by the protocol */	
	ActionEvent actionEvent;

    static belongsTo = [ protocol: Protocol ];

	static constraints = {
		     protocol (nullable: false)
		  actionEvent (nullable: false, unique: 'protocol')
	}
	
    static mapping = {
                   id column : 'pa_id',             length : 36, generator : 'uuid2'
              version false
             protocol column : 'pa_protocol',       length : 36
          actionEvent column : 'pa_action_event',   length : 36
    }

    public static ProtocolActionEvent ensure(
		Protocol protocol,
        String actionEventCode
    ) {
		ProtocolActionEvent result = null;
		
		// Lookup the action / event
		ActionEvent actionEvent = ActionEvent.lookup(actionEventCode);

		// if we do not have an actionevent then we cannot do anything
		if ((protocol != null) && (actionEvent != null)) {
			// Lookup to see if the code exists
			result = protocol.actionEvents.find { ProtocolActionEvent protocolActionEvent ->
				return(protocolActionEvent.actionEvent == actionEvent); 
			}

			// Did we find one	
	        if (result == null) {
	            // No we did not, so create a new one
	            result = new ProtocolActionEvent (
	                protocol: protocol,
					actionEvent: actionEvent
	            );
				
				// Add it to the protocol
				protocol.addToActionEvents(result);
	        }
			
			// Update the other fields

			// Note: This is saved when the protocol is saved
		}		

        // Return the protocol to the caller
        return(result);
    }
}

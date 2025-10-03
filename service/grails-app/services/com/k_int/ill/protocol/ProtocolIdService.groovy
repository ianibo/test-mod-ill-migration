package com.k_int.ill.protocol;

import com.k_int.ill.PatronRequest;

public class ProtocolIdService {

	/** The separator for the protocol id to may include to separate the id from the rota position */
	private static final String REQUESTER_ID_SEPARATOR = '~';
	private static final int REQUESTER_ID_SEPARATOR_LENGTH = REQUESTER_ID_SEPARATOR.length();

    /**
     * Extracts the id of the request from the protocol id
     * @param protocolId The supplied protocol id
     * @return null if it could not extract the id otherwise the id
     */
    public String extractIdFromProtocolId(String protocolId) {
        String id = protocolId;
        if (id != null) {
            // The id may contains the id and rota position
            int separatorPosition = id.indexOf(REQUESTER_ID_SEPARATOR);
            if (separatorPosition > 0) {
                // We found a separator so remove it and everything after it
                id = id.substring(0, separatorPosition);
            }
        }
        return(id);
    }

    /**
     * Attempts to extract the rota position from the protocol id
     * @param protocolId The protocol id we have been supplied with
     * @return a value less than 0 if a rota position was not found, otherwise the rota position
     */
    public long extractRotaPositionFromProtocolId(String protocolId) {
        long rotaPosition = -1;
        if (protocolId != null) {
            // The id may contains the id and rota position
            int separatorPosition = protocolId.indexOf(REQUESTER_ID_SEPARATOR);
            if (separatorPosition > 0) {
                // We found a separator so just take everything after it
                String rotaPositionAsString = protocolId.substring(separatorPosition + REQUESTER_ID_SEPARATOR_LENGTH);

                // Now turn it into an int
                try {
                    rotaPosition = rotaPositionAsString.toLong();
                } catch (Exception e) {
                    // We will ignore all exceptions as it wasn't a string for some reason
                    log.error('Error converting ' + rotaPositionAsString + ' into a rota position from id ' + protocolId);
                }
            }
        }
        return(rotaPosition);
    }

    /**
     * Builds the protocol id from the request
     * @param request The request we want to build the protocol id from
     * @return The protocol id
     */
    public String buildProtocolId(PatronRequest patronRequest) {
        return((patronRequest.hrid ?: patronRequest.id) + REQUESTER_ID_SEPARATOR + patronRequest.rotaPosition.toString());
    }
}

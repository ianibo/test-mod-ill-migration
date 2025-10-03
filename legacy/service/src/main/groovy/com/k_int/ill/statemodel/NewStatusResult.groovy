package com.k_int.ill.statemodel;

/**
 * Holds the outcome of looking up what the status should become when an action or event has been processed
 */
public class NewStatusResult {
	/** New status the request is to be set to */
	public Status status;

	/** Do we update the current rota location status as well */
	public boolean updateRotaLocation;

    public NewStatusResult(Status status, boolean updateRotaLocation = false) {
        this.status = status;
        this.updateRotaLocation = updateRotaLocation;
    }
	
	public String toString() {
		return("Status: " + status?.code + ", updateRotaLocation: " + updateRotaLocation.toString());
	}
}

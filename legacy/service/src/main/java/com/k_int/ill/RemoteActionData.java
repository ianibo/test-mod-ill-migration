package com.k_int.ill;

/**
 * Holds relevant information for a remote action 
 */
public class RemoteActionData {
    /** The token to be used for the remote action */
    public String token;
	
	/** The action to be performed for this token */
	public String action;

	/** Any parameters required for the action */
	public String parameters;

	/** The number of days to expiry */
	public int expiryDays;

	public RemoteActionData(
		String token,
		String action,
		String parameters,
		int expiryDays
	) {
		this.token = token;
		this.action = action;
		this.parameters = parameters;
		this.expiryDays = expiryDays;
	}
}

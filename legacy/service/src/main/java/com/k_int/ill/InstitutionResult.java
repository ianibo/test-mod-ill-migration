package com.k_int.ill;

import com.k_int.GenericResult;

/**
 * Holds the outcome of some processing that went on in a service, that the controller can pass back to the caller
 */
public class InstitutionResult extends GenericResult {
    /** The id of the institution this result is for */
    public String institutionId;

    /** The name of the institution the result is for */
    public String institutionName;

    public InstitutionResult() {
    }

    public InstitutionResult(String id, String institutionId, String institutionName) {
        super(id);
        this.institutionId = institutionId;
        this.institutionName = institutionName;
    }
}

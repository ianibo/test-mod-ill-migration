package com.k_int.institution.results;

import java.util.ArrayList;
import java.util.List;

import com.k_int.ResultIdName;

/**
 * Holds the result details for an institution that a user has access to
 */
public class InstitutionResult {

    /** The id and name of the institution */
    public ResultIdName institution;

    /** The groups the institution belongs to that the user also belongs to */
    public List<ResultIdName> groups = new ArrayList<ResultIdName>();

    public InstitutionResult(String id, String name) {
        institution = new ResultIdName(id, name);
    }
}

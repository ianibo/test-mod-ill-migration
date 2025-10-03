package com.k_int.institution.results;

import java.util.ArrayList;
import java.util.List;

import com.k_int.ResultIdName;

/**
 * Holds the result details for an user that belongs to an institution
 */
public class InstitutionUserResult {

    /** The id and name of the user */
    public ResultIdName user;

    /** The institution that the user is managing */
    public ResultIdName institutionManaging;

    /** The groups the user belongs to that the institution also belongs to */
    public List<ResultIdName> groups = new ArrayList<ResultIdName>();

    public InstitutionUserResult(String id, String name) {
        user = new ResultIdName(id, name);
    }
}

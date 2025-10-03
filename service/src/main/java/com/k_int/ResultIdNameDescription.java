package com.k_int;

public class ResultIdNameDescription extends ResultIdName {

    public String description;

    public ResultIdNameDescription(String id, String name, String description) {
    	super(id, name);
        this.description = description;
    }
}

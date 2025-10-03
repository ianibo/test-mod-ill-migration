package com.k_int.ill.results;

import com.k_int.ResultIdNameDescription;
import com.k_int.ill.CopyrightMessage;

import groovy.transform.CompileStatic;

@CompileStatic
public class BriefCopyrightMessage extends ResultIdNameDescription {

    public BriefCopyrightMessage(CopyrightMessage copyrightMessage) {
		super(copyrightMessage.id, copyrightMessage.code, copyrightMessage.description);
    }
}

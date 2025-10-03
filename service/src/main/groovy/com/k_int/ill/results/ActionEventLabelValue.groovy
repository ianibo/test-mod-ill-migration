package com.k_int.ill.results;

import com.k_int.LabelValue;
import com.k_int.ill.statemodel.ActionEvent;

import groovy.transform.CompileStatic;

@CompileStatic
public class ActionEventLabelValue extends LabelValue {

    public ActionEventLabelValue(ActionEvent actionEvent) {
        super(actionEvent.code, actionEvent.id);
    }
}

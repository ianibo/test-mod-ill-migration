package com.k_int.ill.results;

import com.k_int.LabelValue;
import com.k_int.ill.templating.TemplateContainer;

import groovy.transform.CompileStatic;

@CompileStatic
public class TemplateContainerLabelValue extends LabelValue {

    public TemplateContainerLabelValue(TemplateContainer templateContainer) {
        super(templateContainer.name, templateContainer.id);
    }
}

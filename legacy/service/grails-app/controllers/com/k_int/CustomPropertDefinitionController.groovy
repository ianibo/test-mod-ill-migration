package com.k_int;

import com.k_int.permissions.OkapiApi;
import com.k_int.web.toolkit.custprops.CustomPropertyDefinition;

import grails.databinding.SimpleMapDataBindingSource;
import grails.gorm.multitenancy.CurrentTenant;
import grails.web.Controller;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Slf4j
@CurrentTenant
@Controller
@Api(value = "/ill/custprops", tags = ["Custom Properties"])
@OkapiApi(name = "custprops")
@ExcludeFromGeneratedCoverageReport
public class CustomPropertyDefinitionController extends OkapiTenantAwareSwaggerController<CustomPropertyDefinition> {

    public CustomPropertyDefinitionController() {
        super(CustomPropertyDefinition);
    }

    @Override
    protected CustomPropertyDefinition createResource(Map parameters) {
        def res;
        if (!parameters.type) {
            res = super.createResource(parameters);
        } else {
            res = resource.forType("${parameters.type}", parameters);
        }

        return(res);
    }

    @Override
    protected CustomPropertyDefinition createResource() {
        def instance;
        def json = getObjectToBind();
        if (json && json.type) {
            instance = resource.forType("${json.type}");
        }

        if (!instance) {
            instance = super.createResource();
        }

        bindData instance, (json ? new SimpleMapDataBindingSource(json) : getObjectToBind()), ['exclude': ['type']];
        return(instance);
    }
}

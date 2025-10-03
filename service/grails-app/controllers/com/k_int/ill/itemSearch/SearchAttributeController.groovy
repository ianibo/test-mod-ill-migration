package com.k_int.ill.itemSearch;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Slf4j
@CurrentTenant
@Api(value = "/ill/searchAttribute", tags = ["Search Attribute"])
@OkapiApi(name = "searchattribute")
@ExcludeFromGeneratedCoverageReport
public class SearchAttributeController extends OkapiTenantAwareSwaggerController<SearchAttribute>  {

	public SearchAttributeController() {
		super(SearchAttribute)
	}
}

package com.k_int.ill.itemSearch;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Slf4j
@CurrentTenant
@Api(value = "/ill/searchGroup", tags = ["Search Group"])
@OkapiApi(name = "searchgroup")
@ExcludeFromGeneratedCoverageReport
public class SearchGroupController extends OkapiTenantAwareSwaggerController<SearchGroup>  {

	public SearchGroupController() {
		super(SearchGroup)
	}
}

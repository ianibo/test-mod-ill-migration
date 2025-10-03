package com.k_int.ill.itemSearch;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Slf4j
@CurrentTenant
@Api(value = "/ill/searchTree", tags = ["Search Tree"])
@OkapiApi(name = "searchtree")
@ExcludeFromGeneratedCoverageReport
public class SearchTreeController extends OkapiTenantAwareSwaggerController<SearchTree>  {

	public SearchTreeController() {
		super(SearchTree)
	}
}

package com.k_int.directory;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Slf4j
@CurrentTenant
@Api(value = "/ill/directoryGroups", tags = ["Directory Groups"])
@OkapiApi(name = "directorygroups")
@ExcludeFromGeneratedCoverageReport
public class DirectoryGroupsController extends OkapiTenantAwareSwaggerController<DirectoryGroups>  {

	public DirectoryGroupsController() {
		super(DirectoryGroups)
	}
}

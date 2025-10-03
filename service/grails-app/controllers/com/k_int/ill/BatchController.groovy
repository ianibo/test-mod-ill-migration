package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Slf4j
@CurrentTenant
@Api(value = "/ill/batch", tags = ["Batch"])
@OkapiApi(name = "batch")
@ExcludeFromGeneratedCoverageReport
public class BatchController extends OkapiTenantAwareSwaggerController<Batch>  {

	public BatchController() {
		super(Batch)
	}
}

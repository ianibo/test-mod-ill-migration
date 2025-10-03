package com.k_int.directory;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

/**
 * Access to InternalContact resources
 */
@Slf4j
@CurrentTenant
@Api(value = "/ill/directory/service", tags = ["Directory Service"])
@OkapiApi(name = "service")
@ExcludeFromGeneratedCoverageReport
public class ServiceController extends OkapiTenantAwareSwaggerController<Service>  {

    public ServiceController() {
        super(Service)
    }
}

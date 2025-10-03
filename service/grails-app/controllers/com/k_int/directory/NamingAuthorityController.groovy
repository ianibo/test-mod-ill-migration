package com.k_int.directory;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

/**
 */
@Slf4j
@CurrentTenant
@Api(value = "/ill/directory/namingAuthority", tags = ["Directory Naming"])
@OkapiApi(name = "namingAuthority")
@ExcludeFromGeneratedCoverageReport
public class NamingAuthorityController extends OkapiTenantAwareSwaggerController<NamingAuthority>  {

    public NamingAuthorityController() {
        super(NamingAuthority)
    }
}

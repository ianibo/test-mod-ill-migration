package com.k_int.directory;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

/**
 * Access to Symbol resources
 */
@Slf4j
@CurrentTenant
@Api(value = "/ill/directory/symbol", tags = ["Directory Symbol"])
@OkapiApi(name = "symbol")
@ExcludeFromGeneratedCoverageReport
public class SymbolController extends OkapiTenantAwareSwaggerController<Symbol>  {

    public SymbolController() {
        super(Symbol)
    }
}

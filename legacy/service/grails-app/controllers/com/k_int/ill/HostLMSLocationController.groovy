package com.k_int.ill;

import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@CurrentTenant
@Api(value = "/ill/hostLMSLocations", tags = ["Host LMS Location"])
@OkapiApi(name = "hostLMSLocations")
@ExcludeFromGeneratedCoverageReport
public class HostLMSLocationController extends HasHiddenRecordController<HostLMSLocation> {

    static responseFormats = ['json', 'xml']

    public HostLMSLocationController() {
        super(HostLMSLocation)
    }
}

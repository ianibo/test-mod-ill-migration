package com.k_int.ill;

import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@CurrentTenant
@Api(value = "/ill/hostLMSPatronProfiles", tags = ["Host LMS Patron Profiles"])
@OkapiApi(name = "hostLMSPatronProfiles")
@ExcludeFromGeneratedCoverageReport
public class HostLMSPatronProfileController extends HasHiddenRecordController<HostLMSPatronProfile> {

    static responseFormats = ['json', 'xml']

    public HostLMSPatronProfileController() {
        super(HostLMSPatronProfile)
    }
}

package com.k_int.ill;

import com.k_int.permissions.OkapiApi;

import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Api(value = "/ill/shelvingLocations", tags = ["Shelving Locations"])
@OkapiApi(name = "shelvingLocations")
@ExcludeFromGeneratedCoverageReport
public class ShelvingLocationsController extends HasHiddenRecordController<HostLMSShelvingLocation> {

    public ShelvingLocationsController() {
        super(HostLMSShelvingLocation)
    }
}

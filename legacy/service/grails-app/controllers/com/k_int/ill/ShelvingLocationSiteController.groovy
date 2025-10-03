package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Api(value = "/ill/shelvingLocationSites", tags = ["Shelving Location Sites"])
@OkapiApi(name = "shelvingLocationSite")
@ExcludeFromGeneratedCoverageReport
public class ShelvingLocationSiteController extends OkapiTenantAwareSwaggerController<ShelvingLocationSite> {

  public ShelvingLocationSiteController() {
    super(ShelvingLocationSite);
  }

}

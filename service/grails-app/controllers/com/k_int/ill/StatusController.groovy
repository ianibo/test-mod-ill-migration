package com.k_int.ill

import com.k_int.OkapiTenantAwareSwaggerGetController;
import com.k_int.ill.statemodel.Status;
import com.k_int.permissions.OkapiApi;

import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Api(value = "/ill/status", tags = ["Status"])
@OkapiApi(name = "status")
@ExcludeFromGeneratedCoverageReport
public class StatusController extends OkapiTenantAwareSwaggerGetController<Status> {

  public StatusController() {
    super(Status, true)
  }
}

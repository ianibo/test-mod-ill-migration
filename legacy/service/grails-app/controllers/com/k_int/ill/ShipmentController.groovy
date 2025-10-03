package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Api(value = "/ill/shipments", tags = ["Shipments"])
@OkapiApi(name = "shipment")
@ExcludeFromGeneratedCoverageReport
public class ShipmentController extends OkapiTenantAwareSwaggerController<Shipment> {

  static responseFormats = ['json', 'xml']

  public ShipmentController() {
    super(Shipment)
  }
}

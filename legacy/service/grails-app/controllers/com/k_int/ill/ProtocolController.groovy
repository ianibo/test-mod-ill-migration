package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Api(value = "/ill/protocol", tags = ["Protocol"])
@OkapiApi(name = "protocol")
@ExcludeFromGeneratedCoverageReport
public class ProtocolController extends OkapiTenantAwareSwaggerController<Protocol> {

	static responseFormats = ['json', 'xml']

	public ProtocolController() {
		super(Protocol)
	}
}

package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Api(value = "/ill/noticePolicies", tags = ["Notice Policies"])
@OkapiApi(name = "noticePolicies")
@ExcludeFromGeneratedCoverageReport
public class NoticePolicyController extends OkapiTenantAwareSwaggerController<NoticePolicy> {

  static responseFormats = ['json', 'xml']

  public NoticePolicyController() {
    super(NoticePolicy)
  }
}

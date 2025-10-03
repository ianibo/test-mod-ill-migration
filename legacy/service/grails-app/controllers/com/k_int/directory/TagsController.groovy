package com.k_int.directory;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;
import com.k_int.web.toolkit.tags.Tag;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Slf4j
@CurrentTenant
@Api(value = "/ill/tags", tags = ["Tags"])
@OkapiApi(name = "tags")
@ExcludeFromGeneratedCoverageReport
public class TagsController extends OkapiTenantAwareSwaggerController<Tag> {

  public TagsController() {
    super(Tag)
  }
}

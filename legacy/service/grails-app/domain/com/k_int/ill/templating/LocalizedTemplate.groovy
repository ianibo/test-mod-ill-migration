package com.k_int.ill.templating;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class LocalizedTemplate implements MultiTenant<LocalizedTemplate> {
  String id;
  String locality;
  Template template;

  static belongsTo = [owner: TemplateContainer];

  static mapping = {
                id column: 'ltm_id', generator: 'uuid2', length:36
           version column: 'ltm_version'
          locality column: 'ltm_locality'
             owner column: 'ltm_owner_fk'
          template column: 'ltm_template_fk'
  }
}

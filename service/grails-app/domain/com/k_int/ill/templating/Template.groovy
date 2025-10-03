package com.k_int.ill.templating;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class Template implements MultiTenant<Template> {
  String id
  String templateBody
  String header

  static mapping = {
                  id column: 'tm_id', generator: 'uuid2', length:36
             version column: 'tm_version'
        templateBody column: 'tm_template_body'
              header column: 'tm_header'
  }

}
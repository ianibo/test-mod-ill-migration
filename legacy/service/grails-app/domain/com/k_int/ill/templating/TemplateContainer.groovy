package com.k_int.ill.templating;

import com.k_int.web.toolkit.refdata.Defaults;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class TemplateContainer implements MultiTenant<TemplateContainer> {
  String id;
  String name;

  @Defaults(['Handlebars'])
  RefdataValue templateResolver;

  String description;

  Date dateCreated;
  Date lastUpdated;

  String context;

  static hasMany = [localizedTemplates: LocalizedTemplate];

  static mapping = {
                     id column: 'tmc_id', generator: 'uuid2', length:36
                version column: 'tmc_version'
                   name column: 'tmc_name'
       templateResolver column: 'tmc_template_resolver'
            description column: 'tmc_description'
            dateCreated column: 'tmc_date_created'
            lastUpdated column: 'tmc_last_updated'
                context column: 'tmc_context'
    localizedTemplates cascade: 'all-delete-orphan'
  }
}

package mod.ill;

import com.k_int.okapi.OkapiTenantAdminService

class BootStrap {

  def grailsApplication
  def housekeepingService
  OkapiTenantAdminService okapiTenantAdminService
  
  def init = { servletContext ->
    log.info("Starting mod-ill");

    String module_version =grailsApplication.config.getProperty('info.app.version', String);
    String datasource_url =grailsApplication.config.getProperty('dataSource.url', String);

    log.info("${grailsApplication.getMetadata().getApplicationName()}  (${module_version}) initialising");
    log.info("          build number -> ${grailsApplication.metadata['build.number']}");
    log.info("        build revision -> ${grailsApplication.metadata['build.git.revision']}");
    log.info("          build branch -> ${grailsApplication.metadata['build.git.branch']}");
    log.info("          build commit -> ${grailsApplication.metadata['build.git.commit']}");
    log.info("            build time -> ${grailsApplication.metadata['build.time']}");
    log.info("            build host -> ${grailsApplication.metadata['build.host']}");
    log.info("         Base JDBC URL -> ${datasource_url}");
  }

  def destroy = {
  }
}

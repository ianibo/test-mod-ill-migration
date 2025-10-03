package com.k_int.directory

import com.k_int.ill.constants.Directory;
import com.k_int.web.toolkit.custprops.CustomProperties;
import com.k_int.web.toolkit.databinding.BindUsingWhenRef;
import com.k_int.web.toolkit.refdata.Defaults;
import com.k_int.web.toolkit.refdata.RefdataValue;
import com.k_int.web.toolkit.tags.Tag;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * A service represents an internet callable endpont. A service can support many
 * accounts or tenants. For example, an iso 10161 service endpoint might support
 * symbols from multiple different institutions. This class then models the service
 * itself.
 */
@BindUsingWhenRef({ obj, propName, source, isCollection = false ->
  CustomBinders.bindService(obj, propName, source, isCollection)
})
@ExcludeFromGeneratedCoverageReport
class Service  implements CustomProperties,MultiTenant<Service>  {

  String id
  String name
  String address

  /**
   * The actual protocol in use
   */
  @Defaults([
	Directory.SERVICE_TYPE_Z3950,
	Directory.SERVICE_TYPE_ILL_SMTP,
    Directory.SERVICE_TYPE_ISO10161_TCP,
    Directory.SERVICE_TYPE_ISO10161_SMTP,
    Directory.SERVICE_TYPE_ISO18626_2017,
    Directory.SERVICE_TYPE_ISO18626_2021,
    Directory.SERVICE_TYPE_GSM_SMTP,
    Directory.SERVICE_TYPE_OAI_PMH,
    Directory.SERVICE_TYPE_NCIP,
    Directory.SERVICE_TYPE_HTTP,
    Directory.SERVICE_TYPE_SRU,
    Directory.SERVICE_TYPE_SRW
  ])
  RefdataValue type

  @Defaults(['Managed', 'Reference'])
  RefdataValue status

  /**
   * The business function served - if I want to list all services providing ILL, query this for ILL
   */
  @Defaults([
	Directory.SERVICE_BUSINESS_FUNCTION_ILL,
	Directory.SERVICE_BUSINESS_FUNCTION_CIRC,
	Directory.SERVICE_BUSINESS_FUNCTION_RTAC,
	Directory.SERVICE_BUSINESS_FUNCTION_HARVEST,
	Directory.SERVICE_BUSINESS_FUNCTION_RS_STATS
  ])
  RefdataValue businessFunction

  static hasMany = [
    tags:Tag
  ]

  static mappedBy = [
  ]

  static mapping = {
                  id column:'se_id', generator: 'uuid2', length:36
                name column:'se_name'
             address column:'se_address'
                type column:'se_type_fk'
              status column:'se_status_fk'
    businessFunction column:'se_business_function_fk'
                tags cascade:'save-update'
  }

  static constraints = {
                name(nullable:true, blank:false)
                type(nullable:false)
              status(nullable:true)
             address(nullable:false, blank:false)
    businessFunction(nullable:true)
  }
}

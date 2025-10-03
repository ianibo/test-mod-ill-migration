package com.k_int.ill.shared;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 *
 */
@ExcludeFromGeneratedCoverageReport
public class TenantSymbolMapping implements MultiTenant<TenantSymbolMapping> {

  String id
  String symbol
  String tenant
  Boolean blockLoopback = Boolean.FALSE

  static mapping = {
                  id column : 'tsm_id', generator: 'uuid2', length:36
             version column : 'tsm_version'
              symbol column : 'tsm_symbol'
              tenant column : 'tsm_tenant'
       blockLoopback column : 'tsm_block_loopback'
  }
}


package com.k_int.directory

import com.k_int.web.toolkit.databinding.BindUsingWhenRef;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * Some special sauce to allow us to transparently state the authority as a string instead of an object
 */
@BindUsingWhenRef({ obj, propName, source, isCollection = false ->
  CustomBinders.bindNamingAuthority(obj, propName, source, isCollection)
})
@ExcludeFromGeneratedCoverageReport
class NamingAuthority implements MultiTenant<NamingAuthority>  {

  String id
  String symbol

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static mapping = {
                 id column:'na_id', generator: 'uuid2', length:36
             symbol column:'na_symbol'
  }

  static constraints = {
           symbol(nullable:false, blank:false, unique: true)
  }

  def beforeInsert() {
    if ( ! this.symbol.toUpperCase().equals(this.symbol) ) {
      this.symbol = this.symbol.toUpperCase();
    }
  }

  def beforeUpdate() {
    if ( ! this.symbol.toUpperCase().equals(this.symbol) ) {
      this.symbol = this.symbol.toUpperCase();
    }
  }

}

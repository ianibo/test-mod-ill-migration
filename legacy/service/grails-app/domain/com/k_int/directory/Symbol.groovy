package com.k_int.directory

import com.k_int.web.toolkit.databinding.BindUsingWhenRef;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@BindUsingWhenRef({ obj, propName, source, isCollection = false ->
  CustomBinders.bindSymbol(obj, propName, source, isCollection)
})
@ExcludeFromGeneratedCoverageReport
class Symbol  implements MultiTenant<Symbol>  {

  String id
  String symbol
  String priority
  NamingAuthority authority

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static belongsTo = [
    owner: DirectoryEntry,
    authority: NamingAuthority
  ]

  static mapping = {
                 id column:'sym_id', generator: 'uuid2', length:36
              owner column:'sym_owner_fk'
          authority column:'sym_authority_fk'
             symbol column:'sym_symbol', unique: 'authority'
           priority column:'sym_priority'
  }

  static constraints = {
              owner(nullable:false)
          authority(nullable:false)
             symbol(nullable:false, blank:false)
           priority(nullable:true, blank:false)

  }

  public boolean equals(Object other) {
    boolean result = false;
    if ( ( other instanceof Symbol ) &&
         ( other.symbol == this.symbol  ) &&
         ( other.authority?.symbol == this.authority?.symbol ) ) {
      result = true
    }
    return result;
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

  public int hashCode() {
    "SYMBOL:${authority.symbol}:${symbol}".toString().hashCode()
  }
}

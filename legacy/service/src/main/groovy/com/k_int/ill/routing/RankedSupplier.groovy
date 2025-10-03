package com.k_int.ill.routing;


/**
 * a POJO representing a possible supplier with some reason for the selection and
 * an indication of priority/rank
 */
public class RankedSupplier {
  public String supplier_symbol
  public String instance_identifier
  public String copy_identifier
  public String ill_policy
  public Long rank
  public String rankReason

  public String toString() {
    return "RankedSupplier(${supplier_symbol},${instance_identifier},${copy_identifier},${ill_policy},${rank},${rankReason})".toString()
  }
}

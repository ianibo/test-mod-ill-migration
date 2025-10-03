package com.k_int.ill.hostlms;

import com.k_int.ill.PatronRequest
import com.k_int.ill.lms.HostLMSActions;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.logging.INcipLogDetails;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

import groovy.transform.CompileStatic;

/**
 * The interface between mod-ill and any host Library Management Systems
 *
 */
@CompileStatic
public class ManualHostLmsService implements HostLMSActions {
  Map placeHold(String instanceIdentifier, String itemIdentifier) {
    def result=[:]
    result
  }

  ItemLocation determineBestLocation(ISettings settings, PatronRequest pr, IHoldingLogDetails holdingLogDetails) {
    ItemLocation location = null;
    return location;
  }

  public Map lookupPatron(
      Institution institution,
      ISettings settings,
      String patron_id,
      INcipLogDetails ncipLogDetails
  ) {
    log.debug("lookupPatron(${patron_id})");
    Map result = [status: 'OK', reason: 'spoofed', result: true ];
    return result
  }

  public Map checkoutItem(
    Institution institution,
    ISettings settings,
    String requestId,
    String itemBarcode,
    String borrowerBarcode,
    INcipLogDetails ncipLogDetails
  ) {
    log.debug("checkoutItem(${itemBarcode},${borrowerBarcode})");

    return [
      result:true,
      reason: 'spoofed'
    ]
  }

  public Map acceptItem(
    Institution institution,
    ISettings settings,
    String item_id,
    String request_id,
    String user_id,
    String author,
    String title,
    String isbn,
    String call_number,
    String pickup_location,
    String requested_action,
    INcipLogDetails ncipLogDetails
  ) {

    return [
      result:true,
      reason: 'spoofed'
    ];
  }

  public Map checkInItem(
      Institution institution,
      ISettings settings,
      String item_id,
      INcipLogDetails ncipLogDetails
  ) {
    return [
      result:true,
      reason: 'spoofed'
    ];
  }
}

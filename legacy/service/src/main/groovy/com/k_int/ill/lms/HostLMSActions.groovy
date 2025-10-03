package com.k_int.ill.lms;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.logging.INcipLogDetails;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;

import groovy.transform.CompileStatic;

@CompileStatic
public interface HostLMSActions {

  /**
   * Re:Share has determined that an item located using RTAC is a candidate to be loaned,
   * and that the item has been pulled from the shelf by staff. The core engine would like
   * the host LMS to check the item out of the core LMS so that it can be checked into the
   * ill system for loaning. This function is called with the local item barcode and
   * the barcode of the borrower at the remote system.
   * @param itemBarcode - the barcode of the item to be checked out of the host LMS and into ill
   * @param borrowerBarcode - the borrower at the remote LMS
   * @param ncipLogDetails the object used to log the details of  what went of
   *
   * @return A map containing the following keys
   *    'result' - a mandatory Boolean True if the checkout succeeded, False otherwise
   *    'status' - an optional String which is the state in the Supplier state model that this request should be transitioned to. Possible states are currently defined in
   *               housekeeping service.
   */
  public Map checkoutItem(
      Institution institution,
      ISettings settings,
      String requestId,
      String itemBarcode,
      String borrowerBarcode,
      INcipLogDetails ncipLogDetails
  );

  /**
   * Use a Host LMS API to look up the patron ID and return information about the patron back to Re:Share
   * @return a Map containing the following keys userid, givenName, surname, status
   */
  public Map lookupPatron(
      Institution institution,
      ISettings settings,
      String patron_id,
      INcipLogDetails ncipLogDetails
  );

  /**
   * Use whatever RTAC the LMS provides to try and determine the most appropriate available copy/location for the item identified
   * in the attached patron request.
   * @Return and ItemLocation structure
   */
  public ItemLocation determineBestLocation(
      ISettings settings,
      PatronRequest pr,
      IHoldingLogDetails holdingLogDetails
  );


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
  );

  public Map checkInItem(
      Institution institution,
      ISettings settings,
      String item_id,
      INcipLogDetails
      ncipLogDetails
  );
}

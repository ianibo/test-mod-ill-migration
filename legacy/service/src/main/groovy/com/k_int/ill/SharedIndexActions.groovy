package com.k_int.ill;

import com.k_int.ill.sharedindex.SharedIndexAvailabilityResult;
import com.k_int.ill.sharedindex.SharedIndexResult;
import com.k_int.institution.Institution;

import groovy.transform.CompileStatic

@CompileStatic
public interface SharedIndexActions {
  /**
   * findAppropriateCopies - Accept a map of name:value pairs that describe an instance and see if we can locate
   * any appropriate copies in the shared index.
   * @param institution The institution the call is for
   * @param description A Map of properies that describe the item. Currently understood properties:
   *                         systemInstanceIdentifier -
   *                         title                    - the title of the item
   * @return instance of SharedIndexAvailability which tells us where we can find the item.
   */
  public List<AvailabilityStatement> findAppropriateCopies(
      Institution institution,
      Map description
  );

  /**
   * fetch - Accept a map of name:value pairs that describe an instance and see if we can locate
   * any appropriate copies in the shared index. The returned string is OPAQUE to the application -
   * client UI elements should not assum any specific format (E.G. Json) or schema (E.G. FOLIO Inventory).
   * this is a text blob intended to aid problem solving and is specific to any shared index in use.
   *
   * In the future we MAY provide a canonical representation but it is important to preserve the raw record
   *
   * @param description See above.
   * @return List of records as returned by the shared index. TODO: standardise record format.
   */
  public SharedIndexResult fetchSharedIndexRecords(Map description);

  /**
   * Retrieves the availability for the shared index id
   * @param id The identifier for the record in the shared index
   * @return The availability for the shared index record
   */
  public SharedIndexAvailabilityResult availability(String id);

}

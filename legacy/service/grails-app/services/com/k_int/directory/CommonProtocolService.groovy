package com.k_int.directory

import grails.gorm.transactions.Transactional;

/**
 * The common protocol service accepts 2 directory entries and attempts to discover
 * a common protocol and naming authority which 2 distinct entities can use to talk
 * with eachother. Often supported symbols and protocols are defined in a hierarchical
 * structure. This service translates that structure into a simple response that
 * suggests the best possible protocol and naming authority symbols for talking
 * with a partner in a remote system.
 */
@Transactional
class CommonProtocolService {

  def findCommonProtocol(String local_party_id, String remote_party_id) {

    def result = [:]

    DirectoryEntry local_party = DirectoryEntry.get(local_party_id,);
    DirectoryEntry remote_party = DirectoryEntry.get(remote_party_id);

    // Step 1 - Find the preferred naming authority of the remote party. This may be defined
    // in any of the parent entries, so a branch library 5 steps down a hierarchy may be accepting
    // a default preferred naming authority from the root of an organisational description
    String preferred_naming_authority = getPreferredNamingAuthority(remote_party)

    return result;
  }

  private String getPreferredNamingAuthority(DirectoryEntry party) {
    String result
    return result;
  }
}

package com.k_int.ill.sharedindex.jiscdiscover;

import groovy.util.logging.Slf4j

/**
 * HttpBuilderNG returns groovy.util.slurpersupport.GPathResult from parsed XML response records
 */

@Slf4j
public class JiscDiscoverApiConnectionMock implements JiscDiscoverApiConnection {

  public Object getSru(Map description) {

    log.debug("JiscDiscoverApiConnectionMock::getSru(${description})");

    Object result = null;

    if ( description?.systemInstanceIdentifier == '2231751908' ) {


      InputStream is = this.getClass().getResourceAsStream("/sharedindex/jiscdiscover/jd_rec_id_2231751908.xml");
      result = new XmlSlurper().parse(is)

      // Json variant - not using this one
      // InputStream is = this.getClass().getResourceAsStream("/sharedindex/jiscdiscover/item_3568439.json")
      // result = new JsonSlurper().parse(is)

      log.debug("Returning ${result}");
    }
    else {
      log.debug("No matching mock systemInstanceIdentifier - return null");
    }

    return result;
  }

}


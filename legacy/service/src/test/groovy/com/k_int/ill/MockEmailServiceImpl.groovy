package com.k_int.ill;

import grails.gorm.multitenancy.Tenants
import com.k_int.ill.HostLMSLocation
import com.k_int.ill.PatronRequest
import com.k_int.ill.statemodel.AvailableAction
import com.k_int.directory.Symbol;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import com.k_int.okapi.OkapiClient
import groovy.util.logging.Slf4j
import groovy.json.JsonSlurper

import grails.core.GrailsApplication

/**
 * A mock email service that allows the integration tests to complete without sending any actual emails
 *
 */
@Slf4j
public class MockEmailServiceImpl implements EmailService {

  @Autowired
  GrailsApplication grailsApplication

  static boolean running = false;
  OkapiClient okapiClient

  public Map sendEmail(Map email_params) {
    log.debug("MockEmailServiceImpl::sendNotification(${email_params}) - grailsApplication is ${grailsApplication}");
    return [ status: 'OK' ]
  }
}

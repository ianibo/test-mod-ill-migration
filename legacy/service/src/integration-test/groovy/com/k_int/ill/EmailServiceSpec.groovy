package com.k_int.ill;

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*
import geb.spock.*
import groovy.util.logging.Slf4j
import spock.lang.Shared
import grails.gorm.multitenancy.Tenants
import org.springframework.beans.factory.annotation.Value
import com.k_int.ill.EmailService

@Slf4j
@Integration
@Stepwise
class EmailServiceSpec extends GebSpec {
  
  def grailsApplication
  EmailService emailService

  def setup() {
  }

  def cleanup() {
  }

  void "Test send email"() {
    when: "we send an email"
      def send_result = emailService.sendEmail([:]);

    then: "service returns [status:'OK']"
      send_result.status == 'OK'
  }
}

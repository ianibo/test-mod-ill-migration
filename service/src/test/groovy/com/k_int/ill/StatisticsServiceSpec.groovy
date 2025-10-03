package com.k_int.ill;

import grails.testing.services.ServiceUnitTest
import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * A mock email service that allows the integration tests to complete without sending any actual emails
 *
 */
@Slf4j
class StatisticsServiceSpec extends Specification implements ServiceUnitTest<StatisticsService> {

  def 'test ratio processing'() {
    when: 'We process an example set of ratio data'
      def result = service.processRatioInfo('{"asAt":"2021-09-27T18:54:50Z","current":[{"context":"/activeLoans","value":29,"description":"Current (Aggregate) Lending Level"},{"context":"/activeBorrowing","value":0,"description":"Current (Aggregate) Borrowing Level"}]}','1:3')

    then:
      log.info("got result: ${result}");

    expect:
      result.timestamp <= System.currentTimeMillis();
      result.libraryLoanRatio == 1
      result.libraryBorrowRatio == 3
      result.currentLoanLevel == 29
      result.currentBorrowingLevel == 0
  }
}

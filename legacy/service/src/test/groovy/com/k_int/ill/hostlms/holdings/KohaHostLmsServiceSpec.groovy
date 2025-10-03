package com.k_int.ill.hostlms.holdings;

import com.k_int.ill.logging.DoNothingHoldingLogDetails;

import grails.testing.services.ServiceUnitTest;
import groovy.json.JsonOutput;
import spock.lang.Specification;

class KohaHostLmsServiceSpec extends Specification implements ServiceUnitTest<KohaHoldingsHostLmsService> {
    def 'extractAvailableItemsFrom'() {
        setup:
        def parsedSample = new XmlSlurper().parseText(new File('src/test/resources/zresponsexml/koha-chatham.xml').text);

        when: 'We extract holdings'
        def result = service.extractAvailableItemsFrom(parsedSample, null, new DoNothingHoldingLogDetails());

        then:
        def resultJson = JsonOutput.toJson(result.first());
        result.size() == 1;
        resultJson == '{"temporaryShelvingLocation":null,"itemId":null,"temporaryLocation":null,"shelvingLocation":"CIRC2","callNumber":"580.1 G733v","reason":null,"shelvingPreference":null,"preference":null,"location":"CHATHAM","itemLoanPolicy":null}';
    }
}


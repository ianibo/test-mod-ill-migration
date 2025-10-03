package com.k_int.grails;

import grails.util.Holders;
import groovy.util.logging.Slf4j;

@Slf4j
public class GrailsUtils {

    static public Object getServiceBean(String service) {
        return(Holders.grailsApplication.mainContext.getBean(service));
    }
}

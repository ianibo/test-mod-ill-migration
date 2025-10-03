package com.k_int.ill;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * The definition of a Copright message to display when triggered
 */
@ExcludeFromGeneratedCoverageReport
public class CopyrightMessage implements MultiTenant<CopyrightMessage> {

    String id;

	/** The iso-3166 country code */
    String country;

	/** The code that represents the copyright message */
    String code;

	/** Abrief description about this copyright */
	String description;

	/** The message to be displayed to the user */
    String message;

	/** Hide from user selection */
	boolean hide;
	
    static constraints = {
               code (nullable : false, blank: false, unique: 'country')
            country (nullable : false, blank: false)
        description (nullable : false, blank: false)
            message (nullable : false, blank: false)
               hide (nullable : true)
    }

    static mapping = {
                 id column : 'cm_id', generator: 'uuid2', length:36
            version false
               code column : 'cm_code', length: 32
            country column : 'cm_country', length: 2
        description column : 'cm_description', length: 512
            message column : 'cm_value', type : 'text'
			   hide column : 'cm_hide', defaultValue: "false"
    }
}

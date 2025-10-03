package com.k_int.ill;

import com.k_int.ill.statemodel.ActionEvent;
import com.k_int.ill.templating.TemplateContainer;
import com.k_int.institution.Institution;
import com.k_int.web.toolkit.refdata.RefdataValue;
import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class IllSmtpMessage implements MultiTenant<IllSmtpMessage> {

	String id;
	String name;
	String description;
	boolean active;

	Date dateCreated;
	Date lastUpdated;

	/** The action / event that this message applies to */
	ActionEvent actionEvent;
	 
	/** The template that defines the contents of the email */
	TemplateContainer templateContainer;

	/** The service type this template is applicable for */
	RefdataValue serviceType;

	/** The institution the message belongs to */
	Institution institution;

	static constraints = {
		             name (nullable: false, blank : false)
		      description (nullable: false, blank : false)
		      dateCreated (nullable: true, bindable: false)
		      lastUpdated (nullable: true, bindable: false)
		      serviceType (nullable: true)
		      actionEvent (nullable: false)
		templateContainer (nullable: false)
		      institution (nullable: false)
	}

	static mapping = {
                       id column : 'ism_id', generator: 'uuid2', length:36
                  version column : 'ism_version'
		      dateCreated column : 'ism_date_created'
		      lastUpdated column : 'ism_last_updated'
                     name column : 'ism_name'
		      description column : 'ism_description'
                   active column : 'ism_active', defaultValue: 'true'
              serviceType column : 'ism_service_type'
              actionEvent column : 'ism_action_event'
        templateContainer column : 'ism_container_template'
		      institution column : 'ism_institution_id'
	}
}

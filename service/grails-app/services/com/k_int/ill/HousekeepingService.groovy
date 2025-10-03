package com.k_int.ill;

import com.k_int.ill.referenceData.ActionEventData;
import com.k_int.ill.referenceData.ActionEventResultData;
import com.k_int.ill.referenceData.CopyrightMessageData;
import com.k_int.ill.referenceData.CounterData;
import com.k_int.ill.referenceData.CustomTextProperties;
import com.k_int.ill.referenceData.NamingAuthorityData;
import com.k_int.ill.referenceData.ProtocolData;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.ill.referenceData.SearchData;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.referenceData.StateModelData;
import com.k_int.ill.referenceData.StatusData;
import com.k_int.ill.referenceData.TemplateData;
import com.k_int.ill.referenceData.TimerData;
import com.k_int.ill.statemodel.Status;
import com.k_int.okapi.OkapiTenantAdminService;

import grails.events.EventPublisher;
import grails.events.annotation.Subscriber;
import grails.gorm.multitenancy.Tenants;

/**
 * This service works at the module level, it's often called without a tenant context.
 */
public class HousekeepingService implements EventPublisher {

	OkapiTenantAdminService okapiTenantAdminService;

	/**
     * This is called by the eventing mechanism - There is no web request context
     * this method is called after the schema for a tenant is updated.
     */
	@Subscriber('okapi:schema_update')
	public void onSchemaUpdate(tn, tid) {
		log.debug("HousekeepingService::onSchemaUpdate(${tn},${tid})")
		setupData(tn, tid);
	}

	/**
	 * Put calls to estabish any required reference data in here. This method MUST be communtative - IE repeated calls must leave the
     * system in the same state. It will be called regularly throughout the lifecycle of a project. It is common to see calls to
     * lookupOrCreate, or "upsert" type functions in here."
     */
	private void setupData(tenantName, tenantId) {
		log.info("HousekeepingService::setupData(${tenantName},${tenantId})");

		// Establish a database session in the context of the activated tenant. You can use GORM domain classes inside the closure
		Tenants.withId(tenantId) {
			try {
				setupReferenceData();
			} catch (Exception e) {
				log.error("Exception thrown while setting up the reference data", e);
			}
		}

		log.debug("Issue REFERENCE_DATA_LOADED event for ${tenantId}");
		// Send an event to say that we have completed loading the reference data
		notify(GrailsEventIdentifier.REFERENCE_DATA_LOADED, tenantId);
	}

	private void setupReferenceData() {
		Status.withNewTransaction { status ->
			// Load the Custom text properties
			CustomTextProperties.loadAll();

			// loads the copyright messages
			CopyrightMessageData.loadAll();

			// Load the reference data (needs to be done before settings
			RefdataValueData.loadAll();
	
			// Add the Settings
			SettingsData.loadAll();
			
			// Add the naming authorities
			NamingAuthorityData.loadAll();
			
			// The status data
			StatusData.loadAll();
			
			// Load the action event results data, must be loaded after the Status data
			ActionEventResultData.loadAll();
			
			// The ActionEvent data, must be loaded after ActionEventResultData
			ActionEventData.loadAll();
			
			// Available actions is now loaded as part of the state model
			StateModelData.loadAll();
			
			// Load the counter data, needs to be after the institution data
			CounterData.loadAll();
			
			// load the protocol data
			ProtocolData.loadAll();
			
			// Load the counter data
			TimerData.loadAll();
			
			// Load the search data
			SearchData.loadAll();
			
			// The predefined templates
			TemplateData.loadAll();
		}
	}

	/**
     *  Mod-ill needs some shared data to be able to route incoming messages to the appropriate tenant.
     *  This funcion creates a special shared schema that all tenants have access to. It is the place
     *  we register symbol -> tenant mappings.
     */
	public synchronized void ensureSharedSchema() {
		log.debug("make sure __global tenant is present");
		okapiTenantAdminService.enableTenant('__global',[:])
		log.debug("ensureSharedSchema completed");
	}

	public void ensureSharedConfig() {
	}
}

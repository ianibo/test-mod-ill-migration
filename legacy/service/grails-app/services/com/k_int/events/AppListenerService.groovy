package com.k_int.events;

import java.text.SimpleDateFormat

import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent;
import org.grails.datastore.mapping.engine.event.PostInsertEvent;
import org.grails.datastore.mapping.engine.event.PostUpdateEvent;
import org.grails.datastore.mapping.engine.event.PreInsertEvent;
import org.grails.datastore.mapping.engine.event.SaveOrUpdateEvent;
import org.springframework.context.ApplicationListener;

import com.k_int.directory.DirectoryEntry;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.Events;
import com.k_int.web.toolkit.custprops.types.CustomPropertyContainer;

import grails.events.EventPublisher;
import grails.gorm.multitenancy.Tenants;

/**
 * This class listens for asynchronous domain class events and fires of any needed indications
 * This is the grails async framework in action - the notifications are in a separate thread to
 * the actual save or update of the domain class instance. Handlers should be short lived and if
 * work is needed, spawn a worker task.
 */
public class AppListenerService implements ApplicationListener, EventPublisher {

    /**
     * It's not really enough to do this afterInsert - we actually want this event to fire after the transaction
     * has committed. Be aware that the event may arrive back before the transaction has committed.
     */
    void afterInsert(PostInsertEvent event) {
        if ( event.entityObject instanceof PatronRequest ) {
            PatronRequest pr = (PatronRequest) event.entityObject;
            String tenant = Tenants.currentId(event.source);
            log.debug("afterInsert ${event} ${event?.entityObject?.class?.name} dirtyProps:${event?.entityObject?.dirtyPropertyNames}");

			log.debug("afterInsert ${event} ${event?.entityObject?.class?.name} (${pr.class.name}:${pr.id})");
			String eventName = (pr.isRequester ? Events.EVENT_REQUESTER_NEW_PATRON_REQUEST_INDICATION : Events.EVENT_RESPONDER_NEW_PATRON_REQUEST_INDICATION);
			notify('PREventIndication',
				[
					event: eventName,
					tenant: tenant,
					oid: 'com.k_int.ill.PatronRequest:' + pr.id,
					payload: [
						id: pr.id,
						title: pr.title
					]
				]
			);
			log.debug("AppListenerService::afterInsert event published");
		} else if ( event.entityObject instanceof DirectoryEntry ) {
			DirectoryEntry de = (DirectoryEntry)event.entityObject;
			log.debug("DirectoryEntry inserted ${de}");
			String tenantId = Tenants.currentId(event.source);

			// Let anyone interested know that the user edited a directory entry
			log.debug("notify UserEditedDirectory (insert) event ${tenantId}");
			notify('UserEditedDirectory', tenantId)
		}
	}

	// https://www.codota.com/code/java/methods/org.hibernate.event.spi.PostUpdateEvent/getPersister
	void afterUpdate(PostUpdateEvent event) {
		if (event.entityObject instanceof PatronRequest) {
			PatronRequest pr = (PatronRequest)event.entityObject;
			String tenant = Tenants.currentId(event.source);
			if ( pr.stateHasChanged==true && !pr.manuallyClosed) {
				log.debug("PatronRequest State has changed - issue an indication event so we can react accordingly");
				notify('PREventIndication',
					[
						event: Events.STATUS_EVENT_PREFIX + pr.state.code + Events.STATUS_EVENT_POSTFIX,
						tenant: tenant,
						oid: 'com.k_int.ill.PatronRequest:' + pr.id,
						payload: [
							id: pr.id,
							title: pr.title,
							state: pr.state.code,
							dueDate: pr.dueDateRS
						]
					]
				);
			} else {
				log.warn("PatronRequest ${pr?.id} updated but no state change detected");
			}
		} else if ( event.entityObject instanceof DirectoryEntry ) {
			DirectoryEntry de = (DirectoryEntry)event.entityObject;
			log.debug("Directory entry updated ${de}");
			String tenantId = Tenants.currentId(event.source);
			// Let anyone interested know that the user edited a directory entry
			log.debug("notify UserEditedDirectory (update) event ${tenantId}");
			notify('UserEditedDirectory', tenantId)
		}
	}

	void beforeInsert(PreInsertEvent event) {
		if ( event.entityObject instanceof PatronRequest ) {
			log.debug("beforeInsert ${event} ${event?.entityObject?.class?.name}");
			// Stuff to do before insert of a patron request which need access
			// to the spring boot infrastructure
			// log.debug("beforeInsert of PatronRequest");
		}
	}

	void onSaveOrUpdate(SaveOrUpdateEvent event) {
    	// log.debug("onSaveOrUpdate ${event} ${event?.entityObject?.class?.name}");
		// I don't think we need this method as afterUpdate is triggered
	}

	public void onApplicationEvent(org.springframework.context.ApplicationEvent event){
		// log.debug("--> ${event?.class.name} ${event}");
		if ( event instanceof AbstractPersistenceEvent ) {
			if ( event instanceof PostUpdateEvent ) {
				afterUpdate(event);
			} else if ( event instanceof PreInsertEvent ) {
				beforeInsert(event);
			} else if ( event instanceof PostInsertEvent ) {
				afterInsert(event);
			} else if ( event instanceof SaveOrUpdateEvent ) {
				// On save the record will not have an ID, but it appears that a subsequent event gets triggered
				// once the id has been allocated
				onSaveOrUpdate(event);
			} else {
				// log.debug("No special handling for appliaction event of class ${event}");
			}
		} else {
			// log.debug("Event is not a persistence event: ${event}");
		}
	}

	private Map getCustprops(CustomPropertyContainer svc, boolean include_private_custprops=false) {
		Map result = [ : ];
		svc.value.each { cp ->
			// If we have not already mapped a value for this key, create an array in the response
			if ((include_private_custprops == true) ||
				((include_private_custprops == false) &&
				 (cp.definition.defaultInternal == false))) {
			  	if (result[cp.definition.name] == null) {
					result[cp.definition.name] = [ getCPValue(cp.value) ];
				} else {
					// otherwise, add this value to the existing array
					result[cp.definition.name].add(getCPValue(cp.value));
				}
			}
		}
		log.debug("Adding service account custom properties: ${result}");
		return result;
	}

	private String getCPValue(Object o) {
		String result = null;
		if ( o != null ) {
			if ( o instanceof com.k_int.web.toolkit.refdata.RefdataValue ) {
				result = ((com.k_int.web.toolkit.refdata.RefdataValue)o).value;
			} else {
				result = o.toString();
			}
		}
		return result;
	}

	public Map makeDirentJSON(
		DirectoryEntry de,
        boolean include_units=false,
        boolean include_private_custprops = false,
        boolean use_public_profile=false
	) {
		String last_modified_str = null;
		if ((de.pubLastUpdate != null) &&
			(de.pubLastUpdate > 0)) {
			Date d = new Date(de.pubLastUpdate);
			def sdf = new SimpleDateFormat('yyyy-MM-dd\'T\'HH:mm:ssX');
			last_modified_str = sdf.format(d);
		}

		Map entry_data = [
			id: de.id,  // We are using assigned identifiers now!
			lastModified: last_modified_str,
			name: de.name,
			slug: de.slug,
			foafUrl: de.foafUrl,
			brandingUrl: de.brandingUrl,
			services:[],
			symbols:[],
			description: de.description,
			entryUrl: de.entryUrl,
			phoneNumber: de.phoneNumber,
			emailAddress: de.emailAddress,
			contactName: de.contactName,
			lmsLocationCode: de.lmsLocationCode,
			tags: de.tags?.collect {it?.value},
			type: de.type?.value,
			customProperties: getCustprops(de.customProperties, include_private_custprops),
			members:[],
		];

		if ( use_public_profile ) {
			// We omit several properties for the public interface
		} else {
			entry_data.status = de.status?.value;
		}

		de.services.each { svc ->
			entry_data.services.add(
				[
					slug: svc.slug,
					service: [
						name: svc.service.name,
						address: svc.service.address,
						type: svc.service.type?.value,
						businessFunction: svc.service.businessFunction?.value,
					],
					customProperties: getCustprops(svc.customProperties, include_private_custprops)
				]
			);
		}

		de.symbols.each { sym ->
			entry_data.symbols.add (
				[
					authority: sym.authority.symbol,
					symbol: sym.symbol,
					priority: sym.priority
				]
			);
		};

		de.members.each { mem ->
			if ( mem?.memberOrg?.slug ) {
				entry_data.members.add(['memberOrg': mem?.memberOrg?.slug]);
			}
		}

		if ( de.parent != null ) {
			entry_data.parent = [
				id: de.parent.id,
				slug: de.parent.slug,
				name: de.parent.name
			];
		}

		if ( include_units ) {
			entry_data.units = [];
			de.units.each { unit ->
				entry_data.units.add(makeDirentJSON(unit, true, include_private_custprops, use_public_profile));
			}
		}

		return entry_data;
	}
}

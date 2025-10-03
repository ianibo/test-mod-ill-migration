package com.k_int.ill.statemodel.events;

import com.k_int.directory.Symbol;
import com.k_int.ill.HostLmsService;
import com.k_int.ill.IllActionService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.RefdataValueService;
import com.k_int.ill.SharedIndexService;
import com.k_int.ill.constants.ServiceType;
import com.k_int.ill.patronRequest.PickupLocationService;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.sharedindex.SharedIndexResult;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.EventFetchRequestMethod;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.ill.statemodel.Events;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService

import groovy.sql.Sql;

/**
 * This event service takes a new requester patron request and validates it and tries to determine the rota
 * @author Chas
 */
public class EventReqNewPatronRequestIndService extends AbstractEvent {

    HostLmsService hostLmsService;
    PickupLocationService pickupLocationService;
    IllActionService illActionService;
    InstitutionSettingsService institutionSettingsService;
	RefdataValueService refdataValueService;
    SharedIndexService sharedIndexService;

    @Override
    String name() {
        return(Events.EVENT_REQUESTER_NEW_PATRON_REQUEST_INDICATION);
    }

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    // Notify us of a new requester patron request in the database
    //
    // Requests are created with a STATE of IDLE, this handler validates the request and sets the state to VALIDATED, or ERROR
    // Called when a new patron request indication happens - usually
    // New patron requests must have a  request.requestingInstitutionSymbol
    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        if (request != null) {
            // Generate a human readabe ID to use
            request.hrid = generateHrid(request.institution)
            log.debug("set request.hrid to ${request.hrid}");

            // if we do not have a service type set it to loan
            if (request.serviceType == null) {
                request.serviceType = refdataValueService.lookupServiceType(ServiceType.LOAN);
            }

            // If we were supplied a pickup location, attempt to resolve it here
            pickupLocationService.check(request);

            if (request.requestingInstitutionSymbol != null) {
                // We need to validate the requsting location - and check that we can act as requester for that symbol
                Symbol s = illApplicationEventHandlerService.resolveCombinedSymbol(request.requestingInstitutionSymbol);
                if (s != null) {
                    // We do this separately so that an invalid patron does not stop information being appended to the request
                    request.resolvedRequester = s;
                }

                Map lookupPatron = illActionService.lookupPatron(request, null);
                if (lookupPatron.callSuccess) {
                    boolean patronValid = lookupPatron.patronValid;

                    // If s != null and patronValid == true then the request has passed validation
                    if (s != null && patronValid) {
                        log.debug("Got request ${request}");
                    } else if (s == null) {
                        // An unknown requesting institution symbol is a bigger deal than an invalid patron
                        request.needsAttention = true;
                        log.warn("Unkown requesting institution symbol : ${request.requestingInstitutionSymbol}");
                        eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_NO_INSTITUTION_SYMBOL;
                        eventResultDetails.auditMessage = 'Unknown Requesting Institution Symbol: ' + request.requestingInstitutionSymbol;
                    } else {
                        // If we're here then the requesting institution symbol was fine but the patron is invalid
                        eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_INVALID_PATRON;
                        String errors = (lookupPatron?.problems == null) ? '' : (' (Errors: ' + lookupPatron.problems + ')');
                        String status = lookupPatron?.status == null ? '' : (' (Patron state = ' + lookupPatron.status + ')');
                        eventResultDetails.auditMessage = "Failed to validate patron with id: \"${request.patronIdentifier}\".${status}${errors}".toString();
                        request.needsAttention = true;
                    }
                } else {
                    // unexpected error in Host LMS call
                    request.needsAttention = true;
                    eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_HOST_LMS_CALL_FAILED;
                    eventResultDetails.auditMessage = 'Host LMS integration: lookupPatron call failed. Review configuration and try again or deconfigure host LMS integration in settings. ' + lookupPatron?.problems;
                }
            } else {
                eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_NO_INSTITUTION_SYMBOL;
                request.needsAttention = true;
                eventResultDetails.auditMessage = 'No Requesting Institution Symbol';
            }

            // This is a bit dirty - some clients continue to send request.systemInstanceIdentifier rather than request.bibliographicRecordId
            // If we find we are missing a bib record id but do have a system instance identifier, copy it over. Needs sorting properly post PALCI go live
            if ((request.bibliographicRecordId == null) && (request.systemInstanceIdentifier != null)) {
                request.bibliographicRecordId = request.systemInstanceIdentifier
            }

            if ((request.bibliographicRecordId != null) && (request.bibliographicRecordId.length() > 0)) {
                log.debug('calling fetchSharedIndexRecords');
                SharedIndexResult sharedIndexResult = sharedIndexService.getSharedIndexActions().fetchSharedIndexRecords([systemInstanceIdentifier: request.bibliographicRecordId]);
                if (sharedIndexResult?.totalRecords == 1) {
                    request.oclcNumber = sharedIndexResult.results[0].getOclcNumber();
                }
            } else {
                log.debug("No request.bibliographicRecordId : ${request.bibliographicRecordId}");
            }
        } else {
            log.warn("Unable to locate request for ID ${eventData.payload.id} isRequester=${request?.isRequester}");
        }

        return(eventResultDetails);
    }

    private String generateHrid(Institution institution) {
        String result = null;

        // Get hold of the hrid prefix
        String hridPrefix = institutionSettingsService.getSettingValue(
            institution,
            SettingsData.SETTING_REQUEST_ID_PREFIX
        );
        if (hridPrefix == null) {
            hridPrefix = '';
        }

        // Use this to make sessionFactory.currentSession work as expected
        PatronRequest.withSession { session ->
            log.debug('Generate hrid');
            Sql sql = new Sql(session.connection())
            List queryResult  = sql.rows("select nextval('pr_hrid_seq')");
            log.debug("Query result: ${queryResult }");
            result = hridPrefix + queryResult [0].get('nextval')?.toString();
        }
        return(result);
    }
}

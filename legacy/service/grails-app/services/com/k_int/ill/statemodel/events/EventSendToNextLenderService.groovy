package com.k_int.ill.statemodel.events;

import com.k_int.directory.Symbol;
import com.k_int.ill.HostLmsService;
import com.k_int.ill.NetworkStatus;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestRota;
import com.k_int.ill.PatronRequestService;
import com.k_int.ill.Protocol;
import com.k_int.ill.ProtocolType;
import com.k_int.ill.ReferenceDataService;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.protocol.ProtocolService;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.routing.RankedSupplier;
import com.k_int.ill.routing.Z3950RouterService;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.EventFetchRequestMethod;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.settings.InstitutionSettingsService
import com.k_int.web.toolkit.refdata.RefdataValue;

/**
 * This service eveent is abstract as multiple actions can lead to ending the conversation with a supplier so therefore you have multiple events where you want to move onto the next lender
 * @author Chas
 *
 */
public abstract class EventSendToNextLenderService extends AbstractEvent {

    HostLmsService hostLmsService;
    InstitutionSettingsService institutionSettingsService;
	PatronRequestService patronRequestService;
	ProtocolService protocolService;
    ReferenceDataService referenceDataService;
	Z3950RouterService z3950RouterService;

	EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        log.debug("Got request (HRID Is ${request.hrid}) (Status code is ${request.state?.code})");

        // Set the network status to Idle, just in case we do not attempt to send the message, to avoid confusion
        request.networkStatus = NetworkStatus.Idle;

        if (request.rota.size() > 0) {
            boolean messageTried  = false;
            boolean lookAtNextResponder = true;

            // There may be problems with entries in the lending string, so we loop through the rota
            // until we reach the end, or we find a potential lender we can talk to. The request must
            // also explicitly state a requestingInstitutionSymbol
            while (lookAtNextResponder &&
                   (request.rota.size() > 0) &&
                   ((request.rotaPosition ?: -1) < request.rota.size()) &&
                   (request.requestingInstitutionSymbol != null)) {
                // We have rota entries left, work out the next one
                request.rotaPosition = (request.rotaPosition != null ? request.rotaPosition + 1 : 0);

                // get the responder
                PatronRequestRota prr = request.rota.find({ rotaEntry -> rotaEntry.rotaPosition == request.rotaPosition });
                if (prr != null) {
                    String nextResponder = prr.directoryId

                    log.debug("Attempt to resolve symbol \"${nextResponder}\"");
                    Symbol s = (nextResponder != null) ? illApplicationEventHandlerService.resolveCombinedSymbol(nextResponder) : null;
                    log.debug("Resolved nextResponder to ${s} with status ${s?.owner?.status?.value}");
                    String ownerStatus = s.owner?.status?.value;

                    // Do we perform local availability checks
                    RefdataValue yesNoYes = referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_YES);
                    if (institutionSettingsService.hasSettingValue(
                        request.institution,
                        SettingsData.SETTING_ENABLE_LOCAL_AVAILABILITY_CHECK, yesNoYes.value)
                    ) {
                        // We do
                        if (ownerStatus == 'Managed' || ownerStatus == 'managed') {
                            log.debug('Responder is local') //, going to review state");
                            boolean doLocalReview  = true;
                            //Check to see if we're going to try to automatically check for local
                            //copies
                            String localAutoRespond = institutionSettingsService.getSettingValue(
                                request.institution,
                                SettingsData.SETTING_AUTO_RESPONDER_LOCAL
                            );
                            if (localAutoRespond?.toLowerCase()?.startsWith('on')) {
                                boolean hasLocalCopy = checkForLocalCopy(request);
                                if (hasLocalCopy) {
                                    illApplicationEventHandlerService.auditEntry(request, request.state, request.state, 'Local auto-responder located a local copy - requires review', null);
                                } else {
                                    doLocalReview  = false;
                                    illApplicationEventHandlerService.auditEntry(request, request.state, request.state, 'Local auto-responder did not locate a local copy - sent to next lender', null);
                                }
                            } else {
                                illApplicationEventHandlerService.auditEntry(request, request.state, request.state, 'Local auto-responder off - requires manual checking', null);
                            }

                            if (doLocalReview) {
                                eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_LOCAL_REVIEW;
                                eventResultDetails.auditMessage = 'Sent to local review';
                                return(eventResultDetails);  //Nothing more to do here
                            } else {
                                log.debug('Cannot fill locally, skipping');
                                continue;
                            }
                        }
                    }

                    // Fill out the directory entry reference if it's not currently set, and try to send.
                    if ((nextResponder != null) &&
                        (s != null) &&
                        (prr.peerSymbol == null)) {

						Protocol protocol = protocolService.determineProtocol(s);
						if (protocol == null) {
	                        prr.note = "Unable to determine protocol for ${nextResponder} at position ${request.rotaPosition} so skipping";
	                        log.warn(prr.note);
						} else {
	                        request.resolvedSupplier = s;
							request.currentProtocol = protocol;
							prr.protocol = protocol;

	                        log.debug("Built request message request");
	                        log.debug("LOCKING: PatronRequestRota[${prr.id}] - REQUEST");
	                        prr.lock();
	                        log.debug("LOCKING: PatronRequestRota[${prr.id}] - OBTAINED");
	                        prr.peerSymbol = s;
	                        prr.save(flush:true, failOnError:true);
	
	                        // No longer need to look at next responder
	                        lookAtNextResponder = false;
	
	                        // Probably need a lender_is_valid check here
	                        protocolService.sendMessage(
								request,
								name(),
								null,
								null
							);
                            messageTried = true;
						}
                    } else {
                        log.warn("Lender at position ${request.rotaPosition} invalid, skipping");
                        prr.note = "Send not attempted: Unable to resolve symbol for : ${nextResponder}";
                    }

                    prr.save(flush:true, failOnError:true);
                } else {
                    // Try to find another supplier
					if (findAnotherSupplier(request)) {
						// We found one, so force it to process this new supplier
						request.rotaPosition = request.rotaPosition - 1;
					}
                }
            }

            // Did we send a request?
            if (request.networkStatus == NetworkStatus.Sent) {
                log.debug('sendToNextLender sent to next lender.....');
                eventResultDetails.auditMessage = 'Sent to next lender';
            } else if (messageTried) {
                // We will not set the state yet, just the audit message
                eventResultDetails.auditMessage = 'Problem sending to supplier, will reattempt';
            } else {
                // END OF ROTA
                log.warn('sendToNextLender reached the end of the lending string.....');
                eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_END_OF_ROTA;
                eventResultDetails.auditMessage = 'End of rota';
            }
        } else {
            log.warn('Cannot send to next lender - rota is empty');
            eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_END_OF_ROTA;
            eventResultDetails.auditMessage = 'End of rota';
        }

        return(eventResultDetails);
    }

    //Check to see if we can find a local copy of the item. If yes, then we go
    //ahead and transitition to local review. If not, transitition to send-to-next-lender
    private boolean checkForLocalCopy(PatronRequest request) {
        log.debug('Checking to see if we have a local copy available');

        //Let's still go ahead and try to call the LMS Adapter to find a copy of the request
        ItemLocation location = hostLmsService.determineBestLocation(request, ProtocolType.Z3950_REQUESTER);
        log.debug("Got ${location} as a result of local host lms lookup");

        return(location != null);
    }

	/**
	 * Use the Z3950 router service to try and find another supplier	
	 * @param request the request that requires us to find another supplier
	 * @return true if we added another supplier otherwise false
	 */
	private boolean findAnotherSupplier(PatronRequest request) {
		boolean addedNewSupplier = false;
		
		List<RankedSupplier> rankedSuppliers = z3950RouterService.findMoreSuppliers(request);
		if (!rankedSuppliers.isEmpty()) {
			// We have found another supplier, so add it to the rota
			// We loop through the list, until we have successfully added one
			for (int i = 0; (i < rankedSuppliers.size()) && !addedNewSupplier; i++) {
				if (patronRequestService.addRankedSupplierToRota(request, rankedSuppliers[i]) >= 0) {
					// We have added a supplier
					addedNewSupplier = true;
				}
			}
		}
		
		// Let the caller know if we added a new supplier
		return(addedNewSupplier);
	}
}

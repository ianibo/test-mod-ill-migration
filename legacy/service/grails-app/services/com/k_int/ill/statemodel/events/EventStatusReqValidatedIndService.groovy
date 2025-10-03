package com.k_int.ill.statemodel.events;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestService;
import com.k_int.ill.RequestRouterService;
import com.k_int.ill.routing.RankedSupplier;
import com.k_int.ill.routing.RequestRouter;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.EventFetchRequestMethod;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.ill.statemodel.Events;

/**
 * Event triggered when the request requires validation
 * @author Chas
 *
 */
public class EventStatusReqValidatedIndService extends AbstractEvent {

	PatronRequestService patronRequestService;
    RequestRouterService requestRouterService;

    @Override
    String name() {
        return(Events.EVENT_STATUS_REQ_VALIDATED_INDICATION);
    }

    @Override
    EventFetchRequestMethod fetchRequestMethod() {
        return(EventFetchRequestMethod.PAYLOAD_ID);
    }

    // This takes a request with the state of VALIDATED and changes the state to REQ_SOURCING_ITEM,
    // and then on to REQ_SUPPLIER_IDENTIFIED if a rota could be established
    @Override
    EventResultDetails processEvent(PatronRequest request, Map eventData, EventResultDetails eventResultDetails) {
        eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_SOURCING;
        eventResultDetails.auditMessage = 'Sourcing potential items';

        if (request.rota?.size() != 0) {
            eventResultDetails.qualifier = null;
            eventResultDetails.auditMessage = 'Request supplied with Lending String';
        } else {
            // We will shortly refactor this block to use requestRouterService to get the next block of requests
            RequestRouter selectedRouter = requestRouterService.getRequestRouter(request.institution);

            if (selectedRouter == null) {
                throw new RuntimeException('Unable to locate router');
            }

            List<RankedSupplier> possibleSuppliers = selectedRouter.findMoreSuppliers(
                request
            );

            log.debug("Created ranked rota: ${possibleSuppliers}");

            if (possibleSuppliers.size() > 0) {
				// Process the list of possible suppliers
                possibleSuppliers?.each { RankedSupplier rankedSupplier  ->
					// Add it to the rota
					patronRequestService.addRankedSupplierToRota(request, rankedSupplier);
                }

                // Procesing
                eventResultDetails.qualifier = null;
                eventResultDetails.auditMessage = 'Ratio-Ranked lending string calculated by ' + selectedRouter.getRouterInfo()?.toString();
            } else {
                // ToDo: Ethan: if LastResort app setting is set, add lenders to the request.
                log.error("Unable to identify any suppliers for patron request ID ${eventData.payload.id}")
                eventResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_END_OF_ROTA;
                eventResultDetails.auditMessage =  'Unable to locate lenders';
            }
        }
		
		// Let the caller know the result details
        return(eventResultDetails);
    }
}

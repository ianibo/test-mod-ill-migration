package com.k_int.ill;

import com.k_int.directory.DirectoryEntry;
import com.k_int.directory.DirectoryEntryService;
import com.k_int.ill.constants.ServiceType;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.routing.RankedSupplier;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService;

import groovy.time.Duration;

/**
 * This service handles various things that are specific to a patron request
 * Note: at the moment they are scattered around here there and everywhere, hopefully they will migrate to this service
 */
public class PatronRequestService {

	CopyrightMessageService copyrightMessageService;
	DirectoryEntryService directoryEntryService;
	IllApplicationEventHandlerService illApplicationEventHandlerService;
	InstitutionSettingsService institutionSettingsService;
	RefdataValueService refdataValueService;

	/**
	 * Checks whether the request can be treated as a copy
	 * @param patronRequest the patron request to be checked
	 * @return true if it is to be regarded as a copy otherwise false
	 */
	public boolean isCopy(PatronRequest patronRequest) {
		return(ServiceType.COPY == patronRequest?.serviceType?.label);
	}
	
	/**
	 * Adds a ranked supplier to the rota, if it has a symbol  and is marked as lendable
	 * @param patronRequest the request this ranked supplier is to be added to
	 * @param rankedSupplier the ranked supplier to be added to the rota
	 * @return the rota position the supplier was added at or -1 if there was a failure
	 */
	public int addRankedSupplierToRota(
		PatronRequest patronRequest,
		RankedSupplier rankedSupplier
	) {
		int newRotaPosition = -1;

		// supplier symbol cannot be null		
        if (rankedSupplier.supplier_symbol != null) {
			// ill policy must be null or "Will lend"
            if ((rankedSupplier.ill_policy == null) || (rankedSupplier.ill_policy == 'Will lend')) {
				log.debug("Adding to rota: ${rankedSupplier}");

				// Set the rota_position to the current size of the rota
				newRotaPosition = patronRequest.rota.size();
				
				// Add the ranked supplier to the rota
				patronRequest.addToRota(new PatronRequestRota(
					patronRequest : patronRequest,
					rotaPosition : newRotaPosition,
					directoryId : rankedSupplier.supplier_symbol,
					instanceIdentifier : rankedSupplier.instance_identifier,
					copyIdentifier : rankedSupplier.copy_identifier,
					loadBalancingScore : rankedSupplier.rank,
					loadBalancingReason : rankedSupplier.rankReason
				));
			} else {
				log.warn('ILL Policy was not Will lend');
            }
        } else {
            log.warn('requestRouterService returned an entry without a supplier symbol');
        }

		// Let them know the new rota position		
		return(newRotaPosition);
	}
	
	/**
	 * Sets the network status for the request, along with the next processing time if required
	 * @param request The request that needs to be updated
	 * @param networkStatus The network status to set it to
	 * @param eventData The event data used to generate the protocol message
	 * @param retry Whether we are going to retry or not
	 */
	public void setNetworkStatus(
		PatronRequest request,
		NetworkStatus networkStatus,
		String messageSent,
		boolean retry
	) {
		// Set the network status
		request.networkStatus = networkStatus;

		// Set when we retry
		if (retry) {
			// Retry required, so we need to increment the number of send attempts
			if (request.numberOfSendAttempts == null) {
				request.numberOfSendAttempts = 1;
			} else {
				request.numberOfSendAttempts++;
			}

			// Have we reached the maximum number of retries
			int maxSendAttempts = institutionSettingsService.getSettingAsInt(
				request.institution,
				SettingsData.SETTING_NETWORK_MAXIMUM_SEND_ATEMPTS,
				0,
				false
			);

			// Have we reached our maximum number
			if ((maxSendAttempts > 0) && (request.numberOfSendAttempts > maxSendAttempts)) {
				// We have so decrement our number of attempts as we have already incremented it
				request.numberOfSendAttempts--;

				// set the retry period to null
				request.nextSendAttempt = null;

				// Set the network status to error
				request.networkStatus = NetworkStatus.Error;

				// TODO: Should we set the status at this point to something like network error and introduce a new action ReSend to allow the user to recover ...

				// Finally add an audit record to say what we have done
				illApplicationEventHandlerService.auditEntry(
					request,
					request.state,
					request.state,
					'Maximum number of send attempts reached, setting network status to Error',
					null);
			} else {
				// We want to retry, get hold of the retry period
				int retryMinutes = institutionSettingsService.getSettingAsInt(
					request.institution,
					SettingsData.SETTING_NETWORK_RETRY_PERIOD,
					10,
					false
				);

				// We have a multiplier to the retry minutes based on the number of times we have attempted to send it
				// At a minimum the number of send attempts should be 1 (only includes those we have attempted and not the one we are about to do)
				int retryMultiplier =  (int)((request.numberOfSendAttempts + 5) / 6);

				// Now set when the next attempt will be
				Duration retryDuration = new Duration(0, 0, retryMinutes * retryMultiplier, 0, 0);
				request.nextSendAttempt = retryDuration.plus(new Date());
			}
		} else {
			// No retry required
			request.nextSendAttempt = null;
		}

		// Set the last protocol data
		if (messageSent == null) {
			// Just set it to null as we have none
			request.lastProtocolData = null;
		} else {
			// Save the sent message
			request.lastProtocolData = messageSent;
		}
	}

	/**
	 * Obtains the details required to create a new request
	 * @param institution the institution the request is for
	 * @return a NewRequestDetail object containing the details required to create a new request
	 */
	public NewRequestDetail createDetails(Institution institution) {
		NewRequestDetail newRequestDetails = new NewRequestDetail();
		List<DirectoryEntry> requesterInstitutions = directoryEntryService.requesterInstitutions(institution);
		if (requesterInstitutions) {
			newRequestDetails.addRequesterInstitutions(requesterInstitutions);
			newRequestDetails.addPickupLocations(directoryEntryService.pickupLocations(requesterInstitutions));
		}

		// The copyright messages
		newRequestDetails.addCopyrightMessages(copyrightMessageService.getSelectable());

		// The service types
		newRequestDetails.addServiceTypes(refdataValueService.getServiceTypes());

		// Return the new request details to the caller		
		return(newRequestDetails);
	} 	
}

package com.k_int.ill.statemodel.actions;

import com.k_int.directory.DirectoryEntryService;
import com.k_int.ill.HostLmsService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestAudit;
import com.k_int.ill.PatronRequestService
import com.k_int.ill.RequestVolume;
import com.k_int.ill.constants.Counter;
import com.k_int.ill.constants.Directory;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;
import com.k_int.settings.InstitutionSettingsService
import com.k_int.web.toolkit.custprops.CustomProperty;
import com.k_int.web.toolkit.refdata.RefdataValue;

/**
 * Action that occurs when the responder checjs the item into ill from the LMS
 * @author Chas
 *
 */
public class ActionResponderSupplierCheckInToIllService extends AbstractAction {

    private static final String VOLUME_STATUS_AWAITING_LMS_CHECK_OUT = 'awaiting_lms_check_out';

    private static final String REASON_SPOOFED = 'spoofed';

    ActionResponderSupplierCheckOutOfIllService actionResponderSupplierCheckOutOfIllService;
    DirectoryEntryService directoryEntryService;
    HostLmsService hostLmsService;
    InstitutionSettingsService institutionSettingsService;
	PatronRequestService patronRequestService;
	
    @Override
    public String name() {
        return(Actions.ACTION_RESPONDER_SUPPLIER_CHECK_INTO_ILL);
    }

    @Override
    public ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
		// Not a lot to do if this is regarded as a copy
		if (!patronRequestService.isCopy(request)) {
			// It is not to be treated as a copy request
			checkLoanIntoIll(request, parameters, actionResultDetails);
		}

		// Return the result to the caller
		return(actionResultDetails);
    }
	
    private void checkLoanIntoIll(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        boolean result = false;

        if (parameters?.itemBarcodes.size() != 0) {
            // TODO For now we still use this, so just set to first item in array for now. Should be removed though
            request.selectedItemBarcode = parameters?.itemBarcodes[0]?.itemId;

            // We now want to update the patron request's "volumes" field to reflect the incoming params
            // In order to then use the updated list later, we mimic those actions on a dummy list,
            parameters?.itemBarcodes.each { ib ->
                RequestVolume rv = request.volumes.find { rv -> rv.itemId == ib.itemId };

                // If there's no rv and the delete is true then just skip creation
                if (!rv && !ib._delete) {
                    rv = new RequestVolume(
                        name: ib.name ?: request.volume ?: ib.itemId,
                        itemId: ib.itemId,
                        status: RequestVolume.lookupStatus(VOLUME_STATUS_AWAITING_LMS_CHECK_OUT)
                    );
                    request.addToVolumes(rv);
                }

                if (rv) {
                    if (ib._delete && rv.status.value == VOLUME_STATUS_AWAITING_LMS_CHECK_OUT) {
                        // Remove if deleted by incoming call and NCIP call hasn't succeeded yet
                        request.removeFromVolumes(rv);
                    } else if (ib.name && rv.name != ib.name) {
                        // Allow changing of label up to shipping
                        rv.name = ib.name;
                    }
                }

                // Why do we save at this point ?
                request.save(failOnError: true)
            }

            // At this point we should have an accurate list of the calls that need to run/have succeeded
            RequestVolume[] volumesNotCheckedIn = request.volumes.findAll { rv ->
                rv.status.value == VOLUME_STATUS_AWAITING_LMS_CHECK_OUT
            }

            if (volumesNotCheckedIn.size() > 0) {
                // Call the host lms to check the item out of the host system and in to ill

                /*
                * The supplier shouldn't be attempting to check out of their host LMS with the requester's side patronID.
                * Instead use institutionalPatronID saved on DirEnt or default from settings.
                */

                /*
                * This takes the resolvedRequester symbol, then looks at its owner, which is a DirectoryEntry
                * We then feed that into extractCustomPropertyFromDirectoryEntry to get a CustomProperty.
                * Finally we can extract the value from that custprop.
                * Here that value is a string, but in the refdata case we'd need value?.value
                */
                CustomProperty institutionalPatronId = directoryEntryService.extractCustomPropertyFromDirectoryEntry(request.resolvedRequester?.owner, Directory.KEY_LOCAL_INSTITUTION_PATRON_ID);
                String institutionalPatronIdValue = institutionalPatronId?.value
                if (!institutionalPatronIdValue) {
                    // If nothing on the Directory Entry then fallback to the default in settings
                    institutionalPatronIdValue = institutionSettingsService.getSettingValue(
                        request.institution,
                        SettingsData.SETTING_DEFAULT_INSTITUTIONAL_PATRON_ID
                    );
                }

                // At this point we have a list of NCIP calls to make.
                // We should make those calls and track which succeeded/failed

                // Store a string and a Date to save onto the request at the end
                Date parsedDate
                String stringDate

                // Iterate over volumes not yet checked in in for loop so we can break out if we need to
                for (def vol : volumesNotCheckedIn) {
                    /*
                     * Be aware that institutionalPatronIdValue here may well be blank or null.
                     * In the case that host_lms == ManualHostLmsService we don't care, we're just spoofing a positive result,
                     * so we delegate responsibility for checking this to the hostLmsService itself, with errors arising in the 'problems' block
                     */
                    Map checkoutResult = hostLmsService.checkoutItem(request, vol.itemId, institutionalPatronIdValue);

                    // Otherwise, if the checkout succeeded or failed, set appropriately
                    if (checkoutResult.result == true) {
                        RefdataValue volStatus = checkoutResult.reason == REASON_SPOOFED ? vol.lookupStatus('lms_check_out_(no_integration)') : vol.lookupStatus('lms_check_out_complete');
                        if (volStatus) {
                            vol.status = volStatus;
                        }
                        vol.save(failOnError: true);
                        illApplicationEventHandlerService.auditEntry(request, request.state, request.state, "Check in to ILL completed for itemId: ${vol.itemId}. ${checkoutResult.reason == REASON_SPOOFED ? '(No host LMS integration configured for check out item call)' : 'Host LMS integration: CheckoutItem call succeeded.'}", null);

                        // Attempt to store any dueDate coming in from LMS iff it is earlier than what we have stored
                        String dateFormatSetting = institutionSettingsService.getSettingValue(
                            request.institution,
                            SettingsData.SETTING_NCIP_DUE_DATE_FORMAT
                        );
                        try {
                            Date tempParsedDate = illActionService.parseDateString(checkoutResult?.dueDate, dateFormatSetting);
                            if (!request.parsedDueDateFromLMS || parsedDate.before(request.parsedDueDateFromLMS)) {
                                parsedDate = tempParsedDate;
                                stringDate = checkoutResult?.dueDate;
                            }
                        } catch (Exception e) {
                            log.warn("Unable to parse ${checkoutResult?.dueDate} to date with format string ${dateFormatSetting}: ${e.getMessage()}");
                        }
                    } else {
                        illApplicationEventHandlerService.auditEntry(request, request.state, request.state, "Host LMS integration: NCIP CheckoutItem call failed for itemId: ${vol.itemId}. Review configuration and try again or deconfigure host LMS integration in settings. " + checkoutResult.problems?.toString(), null);
                    }
                }

                // Save the earliest Date we found as the dueDate
                request.dueDateFromLMS = stringDate;
                request.parsedDueDateFromLMS = parsedDate;
                request.save(flush:true, failOnError:true);

                // At this point we should have all volumes checked out. Check that again
                volumesNotCheckedIn = request.volumes.findAll { rv ->
                    rv.status.value == VOLUME_STATUS_AWAITING_LMS_CHECK_OUT;
                }

                if (volumesNotCheckedIn.size() == 0) {
                    counterService.incrementCounter(request.institution, Counter.COUNTER_ACTIVE_LOANS);
                    request.activeLoan = true;
                    request.needsAttention = false;
                    if (!institutionSettingsService.hasSettingValue(
                        request.institution,
                        SettingsData.SETTING_NCIP_USE_DUE_DATE,
                        'off'
                    )) {
                        request.dueDateRS = request.dueDateFromLMS;
                    }

                    try {
                        request.parsedDueDateRS = illActionService.parseDateString(request.dueDateRS);
                    } catch (Exception e) {
                        log.warn("Unable to parse ${request.dueDateRS} to date: ${e.getMessage()}");
                    }

                    request.overdue = false;
                    actionResultDetails.auditMessage = 'Items successfully checked in to ILL';
                    result = true;
                } else {
                    actionResultDetails.auditMessage = 'One or more items failed to be checked into ILL. Review configuration and try again or deconfigure host LMS integration in settings.';
                    request.needsAttention = true;
                }
            } else {
                // If we have deleted all failing requests, we can move to next state
                actionResultDetails.auditMessage = 'Fill request completed.';

                // Result is successful
                result = true
                log.info('No item ids remain not checked into ILL, return true');
            }
        }

        if (result == false) {
            actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
            actionResultDetails.responseResult.code = -3; // NCIP action failed

            // Ensure we have a message
            if (actionResultDetails.responseResult.message == null) {
                actionResultDetails.responseResult.message = 'NCIP CheckoutItem call failed.';
            }
        }
    }

    @Override
    public ActionResultDetails undo(PatronRequest request, PatronRequestAudit audit, ActionResultDetails actionResultDetails) {

        // Call the checkout of ill action
        actionResultDetails = actionResponderSupplierCheckOutOfIllService.performAction(request, [ undo : true ], actionResultDetails);

        // If we were successful, remove the volumes from the request
        if (actionResultDetails.result == ActionResult.SUCCESS) {
            request.volumes.collect().each { volume ->
                request.removeFromVolumes(volume);
            }
        }

        // Remove any LMS due date
        request.dueDateFromLMS = null;
        request.parsedDueDateFromLMS = null;

        // Remove the RS due date too if that was likely set from the LMS one,
        if (!institutionSettingsService.hasSettingValue(
            request.institution,
            SettingsData.SETTING_NCIP_USE_DUE_DATE,
            'off'
        )) {
            request.dueDateRS = null;
            request.parsedDueDateRS = null;
        }

        // Let the caller know the result
        return(actionResultDetails);
    }
}

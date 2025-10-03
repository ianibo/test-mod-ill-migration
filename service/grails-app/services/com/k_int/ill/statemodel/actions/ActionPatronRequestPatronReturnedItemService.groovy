package com.k_int.ill.statemodel.actions;

import com.k_int.ill.HostLmsService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.Actions;
import com.k_int.settings.InstitutionSettingsService

/**
 * Action that performs the returned item action for the requester
 * @author Chas
 *
 */
public class ActionPatronRequestPatronReturnedItemService extends AbstractAction {

    HostLmsService hostLmsService;
    InstitutionSettingsService institutionSettingsService;

    @Override
    String name() {
        return(Actions.ACTION_REQUESTER_PATRON_RETURNED_ITEM);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // Just set the status
        actionResultDetails.responseResult.status = true;

        String checkInOnReturn = institutionSettingsService.getSettingValue(
            request.institution,
            SettingsData.SETTING_CHECK_IN_ON_RETURN
        );

        if (checkInOnReturn != 'off') {
            log.debug("Attempting NCIP CheckInItem after setting item returned for volumes for request {$request?.id}");
            Map resultMap = [:];
            try {
                resultMap = hostLmsService.checkInRequestVolumes(request);
            } catch (Exception e) {
                log.error("Error attempting NCIP CheckinItem for request {$request.id}: {$e}");
                resultMap.result = false;
            }
            if (resultMap.result) {
                log.debug("Successfully checked in volumes for request {$request.id}");
            } else {
                log.debug("Failed to check in volumes for request {$request.id}");
            }
        } else {
            log.debug("NOT Attempting NCIP CheckInItem after setting item returned for volumes for request {$request?.id}");
        }

        return(actionResultDetails);
    }
}

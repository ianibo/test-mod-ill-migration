package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.lms.ItemLocation;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Abstract action service that contains methods used by the responder actions
 * @author Chas
 *
 */
public abstract class ActionResponderService extends AbstractAction {

    /**
     * Performs check that the pick location has been supplied and routes it to this location
     * @param request The request the action is being performed against
     * @param parameters Any parameters passed into the action
     * @param actionResultDetails If successful there is no change
     * @return The actionResultDetails
     */
    protected ActionResultDetails validatePickupLocationAndRoute(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {
        // were we supplied with the location details
        if (parameters?.pickLocation != null) {
            // We have been supplied the item location details
            ItemLocation location = new ItemLocation(location: parameters.pickLocation,
                                                     shelvingLocation: parameters.pickShelvingLocation,
                                                     callNumber: parameters.callnumber);

            if (!illApplicationEventHandlerService.routeRequestToLocation(request, location)) {
                actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
                actionResultDetails.auditMessage = 'Failed to route request to given location';
                actionResultDetails.responseResult.code = -2; // No location specified
                actionResultDetails.responseResult.message = actionResultDetails.auditMessage;
            }
        } else {
            actionResultDetails.result = ActionResult.INVALID_PARAMETERS;
            actionResultDetails.auditMessage = 'No pick location specified. Unable to continue';
            actionResultDetails.responseResult.code = -1; // No location specified
            actionResultDetails.responseResult.message = actionResultDetails.auditMessage;
        }

        return(actionResultDetails);
    }
}

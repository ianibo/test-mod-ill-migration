package com.k_int.ill.statemodel;

import com.k_int.ill.CounterService;
import com.k_int.ill.IllActionService;
import com.k_int.ill.IllApplicationEventHandlerService;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestAudit;

/**
 * The base class for all the actions
 * @author Chas
 *
 */
public abstract class AbstractAction {

    // We automatically inject these 3 services as some if not all actions use them
    CounterService counterService;
    IllActionService illActionService;
    IllApplicationEventHandlerService illApplicationEventHandlerService;

    /**
     * Method that all classes derive from this one that actually performs the action
     * @param request The request the action is being performed against
     * @param parameters Any parameters required for the action
     * @param actionResultDetails The result of performing the action
     * @return The actionResultDetails
     */
    abstract ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails);

    /**
     * If an action is capable of being undone, then this method will be overridden to perform the undo
     * @param request The request the action is being performed against
     * @param audit The audit record that holds the details of what was performed in the first place
     * @param actionResultDetails The result of performing the action
     * @return The actionResultDetails
     */
    ActionResultDetails undo(PatronRequest request, PatronRequestAudit audit, ActionResultDetails actionResultDetails) {
        actionResultDetails.result = ActionResult.ERROR;
        actionResultDetails.auditMessage = 'Not Implemented';
        return(actionResultDetails);
    }

    /**
     * The name of the action
     * @return the action name
     */
    abstract String name();
}

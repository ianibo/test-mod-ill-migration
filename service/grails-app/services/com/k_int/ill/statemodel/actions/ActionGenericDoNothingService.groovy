package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * A generic action service that does nothing
 * @author Chas
 *
 */
public class ActionGenericDoNothingService extends AbstractAction {

    @Override
    String name() {
        // Could be hooked to from multiple actions, so we just call it GenericDoNothing
        return("GenericDoNothing");
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {

        // Just return the action result details as supplied
        return(actionResultDetails);
    }
}

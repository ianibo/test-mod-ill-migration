package com.k_int.ill.statemodel.actions;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.statemodel.AbstractAction;
import com.k_int.ill.statemodel.AbstractEvent;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.ActionService;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.EventResultDetails;

/**
 * A services that attempts to undo the last action
 * @author Chas
 *
 */
public class ActionUndoService extends AbstractAction {

    ActionService actionService;

    @Override
    String name() {
        return(Actions.ACTION_UNDO);
    }

    @Override
    ActionResultDetails performAction(PatronRequest request, Map<String, Object> parameters, ActionResultDetails actionResultDetails) {

        // Get hold of the audit record records that need undoing
        List undoAudits = actionService.buildUndoAudits(request);
        if (undoAudits == null) {
            // No action to undo
            actionResultDetails.result = ActionResult.ERROR;
            actionResultDetails.auditMessage = 'The last action is not possible to undo';
        } else {
            // It is possible to undo the action, so loop through all the audit records that need undoing
            // They should be in the correct order
            undoAudits.each { audit ->
                // Only perform the undo, if the previous undo we successful
                if (actionResultDetails.result == ActionResult.SUCCESS) {
                    EventResultDetails undoResultDetails = new ActionResultDetails();
                    undoResultDetails.result = ActionResult.SUCCESS;
                    if (audit.actionEvent.isAction) {
                        // Get hold of the class that will action the undo
                        AbstractAction actionProcessor = actionService.getServiceAction(audit.actionEvent.code, request.isRequester);

                        // Now perform the undo
                        undoResultDetails = actionProcessor.undo(request, audit, undoResultDetails);
                    } else {
                        // It is an event
                        AbstractEvent eventProcessor = illApplicationEventHandlerService.getEventProcessor(audit.actionEvent.code);

                        // Now perform the undo
                        undoResultDetails = eventProcessor.undo(request, audit, undoResultDetails);
                    }

                    // Set the override status to being the status the audit record originally came from
                    // Check this one as to what happens, may need to do something different
                    actionResultDetails.overrideStatus = audit.fromStatus;
                    actionResultDetails.result = undoResultDetails.result;

                    // If it was not successful, then we beed to try and pass back as much information as possible
                    if (undoResultDetails.result == ActionResult.SUCCESS) {
                        // We were successful, so mark the audit record as being undone and save it
                        audit.undoPerformed = true;
                        audit.save(flush:true, failOnError:true);
                    } else {
                        // We were not successful, so we need to try and pass back as much information as possible
                        actionResultDetails.responseResult = audit.responseResult;
                        actionResultDetails.auditMessage = audit.auditMessage;
                    }
                }
            }
        }

        return(actionResultDetails);
    }
}

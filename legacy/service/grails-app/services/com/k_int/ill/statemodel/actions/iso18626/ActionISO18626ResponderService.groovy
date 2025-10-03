
package com.k_int.ill.statemodel.actions.iso18626;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.RequestingAgencyMessage;
import com.k_int.ill.statemodel.ActionResultDetails;

/**
 * Action that deals with interpreting ISO18626 on the responder side
 * @author Chas
 *
 */
public abstract class ActionISO18626ResponderService extends ActionISO18626Service {

    /**
     * This method allows a caller to preprocess the note as we effectively use it for extensions at the moment
     * @param request The request that needs to be manipulated
     * @param parameters The parameters we use to manipulate the request
     * @param note The note from the parameters that may have been manipulated
     * @param actionResultDetails Any details that influence perdorming this action
     * @return The action result details
     */
    protected ActionResultDetails processNote(PatronRequest request, RequestingAgencyMessage requestingAgencyMessage, String note, ActionResultDetails actionResultDetails) {
        // Extract the sequence from the note
        Map sequenceResult = iso18626NotesService.extractSequenceFromNote(note);

        // Now we deal with the note without the sequence in it
        note = sequenceResult.note;
        request.lastSequenceReceived = sequenceResult.sequence;

        // If there is a note, create notification entry
        if (note) {
            incomingNotificationEntry(
                request,
                requestingAgencyMessage,
                note
            );
        }

        return(actionResultDetails);
    }
}

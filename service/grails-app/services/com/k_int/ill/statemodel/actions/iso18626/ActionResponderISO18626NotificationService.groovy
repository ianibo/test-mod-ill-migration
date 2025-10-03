package com.k_int.ill.statemodel.actions.iso18626;

import java.time.LocalDate;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.iso18626.ExtractedNoteFieldResult;
import com.k_int.ill.iso18626.NoteSpecials;
import com.k_int.ill.iso18626.RequestingAgencyMessage;
import com.k_int.ill.iso18626.codes.closed.Action;
import com.k_int.ill.statemodel.ActionEventResultQualifier;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionResultDetails;
import com.k_int.ill.statemodel.StatusStage;

/**
 * Action that deals with the ISO18626 Notification message
 * @author Chas
 *
 */
public class ActionResponderISO18626NotificationService extends ActionISO18626ResponderService {

    // These are all the fields that can be updated through the note
    private static final List updateAbleFields = [
        [ field: "author", notePrefix: NoteSpecials.UPDATED_FIELD_AUTHOR_PREFIX, isDate: false ],
        [ field: "edition", notePrefix: NoteSpecials.UPDATED_FIELD_EDITION_PREFIX, isDate: false ],
        [ field: "isbn", notePrefix: NoteSpecials.UPDATED_FIELD_ISBN_PREFIX, isDate: false ],
        [ field: "issn", notePrefix: NoteSpecials.UPDATED_FIELD_ISSN_PREFIX, isDate: false ],
        [ field: "neededBy", notePrefix: NoteSpecials.UPDATED_FIELD_NEEDED_BY_PREFIX, isDate: true ],
        [ field: "oclcNumber", notePrefix: NoteSpecials.UPDATED_FIELD_OCLC_NUMBER_PREFIX, isDate: false ],
        [ field: "patronNote", notePrefix: NoteSpecials.UPDATED_FIELD_PATRON_NOTE_PREFIX, isDate: false ],
        [ field: "pickupLocation", notePrefix: NoteSpecials.UPDATED_FIELD_PICKUP_LOCATION_PREFIX, isDate: false],
        [ field: "placeOfPublication", notePrefix: NoteSpecials.UPDATED_FIELD_PLACE_OF_PUBLICATION_PREFIX, isDate: false ],
        [ field: "publicationDate", notePrefix: NoteSpecials.UPDATED_FIELD_PUBLICATION_DATE_PREFIX, isDate: false ],
        [ field: "publisher", notePrefix: NoteSpecials.UPDATED_FIELD_PUBLISHER_PREFIX, isDate: false ],
        [ field: "systemInstanceIdentifier", notePrefix: NoteSpecials.UPDATED_FIELD_SYSTEM_INSTANCE_IDENTIFIER_PREFIX, isDate: false ],
        [ field: "title", notePrefix: NoteSpecials.UPDATED_FIELD_TITLE_PREFIX, isDate: false ],
        [ field: "volume", notePrefix: NoteSpecials.UPDATED_FIELD_VOLUME_PREFIX, isDate: false ]
    ];

    @Override
    String name() {
        return(Action.NOTIFICATION);
    }

    ActionResultDetails performAction(
        PatronRequest request,
        RequestingAgencyMessage requestingAgencyMessage,
        ActionResultDetails actionResultDetails
    ) {
        /* If the message is preceded by #IllLoanConditionAgreeResponse#
         * then we'll need to check whether or not we need to change state.
         */
        String note = requestingAgencyMessage.findNote();
        if (note != null) {
            // Check for the ill special of loan conditions agreed
            if (note.startsWith(NoteSpecials.AGREE_LOAN_CONDITION)) {
                // First check we're in the state where we need to change states, otherwise we just ignore this and treat as a regular message, albeit with warning
                if (request.state.stage == StatusStage.ACTIVE_PENDING_CONDITIONAL_ANSWER) {
                    // We need to change the state to the saved state
                    actionResultDetails.qualifier = ActionEventResultQualifier.QUALIFIER_CONDITIONS_AGREED;
                    actionResultDetails.auditMessage = 'Requester agreed to loan conditions, moving request forward';

                    // Make all conditions agreed
                    illApplicationEventHandlerService.markAllLoanConditionsAccepted(request);
                } else {
                    // Loan conditions were already marked as agreed
                    actionResultDetails.auditMessage = 'Requester agreed to loan conditions, no action required on supplier side';
                }

                // Remove the keyword
                note = note.replace(NoteSpecials.AGREE_LOAN_CONDITION, "");
            } else {
                // Do we have any fields that need updating
                StringBuffer auditMessage = new StringBuffer();
                updateAbleFields.each() { fieldDetails ->
                    ExtractedNoteFieldResult extractedFieldResult = iso18626NotesService.extractFieldFromNote(note, fieldDetails.notePrefix);
                    if (extractedFieldResult.data != null) {
                        boolean validValue = true;
                        Object value = extractedFieldResult.data;

                        // Are we dealing with a date
                        if (fieldDetails.isDate) {
                            // Will need converting to a string
                            try {
                                // Convert the value
                                value = LocalDate.parse(extractedFieldResult.data);
                            } catch (Exception e) {
                                log.error("Failed to parse date field ${fieldDetails.field} with value ${extractedFieldResult.data}", e);
                                validValue = false;
                            }
                        }

                        // Can we update the field
                        if (validValue) {
                            request[fieldDetails.field] = value;
                            auditMessage.append("${fieldDetails.field} updated to \"" + extractedFieldResult.data + "\".")
                        }

                        // Reset the message note
                        note = extractedFieldResult.note;
                    }
                }

                // Did we update at least 1 field
                if (auditMessage.length() > 0) {
                    // Set the audit message to what we have updated
                    actionResultDetails.auditMessage = auditMessage.toString();
                } else {
                    // Nothing updated so we just trat it as a message
                    actionResultDetails.auditMessage = "Notification message received from requesting agency: ${note}";
                }
            }
        }

        // If we were successful, call the base class
        if (actionResultDetails.result == ActionResult.SUCCESS) {
            // We call process note now as we may have manipulated the note
            // Unfortunately we cannot just replace the note field on parameters.activeSection
            actionResultDetails = processNote(request, requestingAgencyMessage, note, actionResultDetails);
        }

        // Now return the result to the caller
        return(actionResultDetails);
    }
}

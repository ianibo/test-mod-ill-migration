package com.k_int.ill.protocols.iso18626;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.k_int.ill.iso18626.ExtractedNoteFieldResult;
import com.k_int.ill.iso18626.NoteSpecials;

/**
 * Caters for all the special handling, we have for the note field, since there are no extensions
 */
public class Iso18626NotesService {

    private static final String ALL_REGEX           = '(.*)';
    private static final String NUMBER_REGEX        = '(\\d+)';
    private static final String END_OF_STRING_REGEX = '$'
    private static final String SEQUENCE_REGEX      = ALL_REGEX + NoteSpecials.SEQUENCE_PREFIX + NUMBER_REGEX + NoteSpecials.SPECIAL_WRAPPER + END_OF_STRING_REGEX;
    private static final String LAST_SEQUENCE_REGEX = ALL_REGEX + NoteSpecials.LAST_SEQUENCE_PREFIX + NUMBER_REGEX + NoteSpecials.SPECIAL_WRAPPER + END_OF_STRING_REGEX;

    /**
     * Builds the sequence string for our hack to determine if the message was received or not
     * @param sequence the sequence we are to send
     * @return The sequence wrapped in the appropriate format to be appended in the note field
     */
    public String buildSequence(int sequence) {
		return(NoteSpecials.SEQUENCE_PREFIX + sequence.toString() + NoteSpecials.SPECIAL_WRAPPER);
	}

    /**
     * Extracts the last sequence number from the note field
     * @param note The note that may contain the last sequence
     * @return A map containing the following fields
     *    1. note without the sequence
     *    2. sequence the found sequence
     * if no sequence is found then the sequence will be null
     */
    public Map extractLastSequenceFromNote(String note) {
        return(extractSequence(note, LAST_SEQUENCE_REGEX))
    }

    /**
     * Extracts the sequence number from the note field
     * @param note The note that may contain the last sequence
     * @return A map containing the following fields
     *    1. note without the sequence
     *    2. sequence the found sequence
     * if no sequence is found then the sequence will be null
     */
    public Map extractSequenceFromNote(String note) {
        return(extractSequence(note, SEQUENCE_REGEX))
    }

    /**
     * Builds the last sequence string for our hack to determine if the message was received or not
     * @param lastSequence the last sequence we sent
     * @return The last sequence sent wrapped in the appropriate format to be appended in the note field
     */
    public String buildLastSequence(Integer lastSequence) {
        String lastSequenceSent = lastSequence == null ? "-1" : lastSequence.toString();
        return(NoteSpecials.LAST_SEQUENCE_PREFIX + lastSequenceSent + NoteSpecials.SPECIAL_WRAPPER);
    }

    /**
     * Extracts the sequence number from the note field
     * @param note The note that may contain the last sequence
     * @param sequenceRegex The regex used to obtain the sequence (group 2) and the note (group 1)
     * @return A map containing the following fields
     *    1. note without the sequence
     *    2. sequence the found sequence
     * if no sequence is found then the sequence will be null
     */
    public Map extractSequence(String note, String sequenceRegex) {
        Map result = [ note: note];

        // If we havn't been supplied a note then there is nothing to extract
        if (note != null) {
            // We use Pattern.DOTALL in case there are newlines in the string
            Pattern pattern = Pattern.compile(sequenceRegex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(note);
            if (matcher.find())
            {
                try {
                    // The sequence matches on the 2nd group
                    String sequenceAsString = matcher.group(2);
                    if (sequenceAsString != null) {
                        // Convert to an integer
                        result.sequence = sequenceAsString.toInteger();

                        // Grab the actual note from the first group as the sequence is always at the end of the note
                        result.note = matcher.group(1);

                        // Need to ensure the note is not blank
                        if (result.note.length() == 0) {
                            // We need to make it null
                            result.note = null;
                        }
                    }
                } catch (Exception ) {
                    // We ignore any exception thrown, as it means it wasn't what we were expecting
                }
            }
        }

        // Return the note and sequence to the caller
        return(result);
    }

    /**
     * Extracts a field from the note
     * @param note The note that has been sent
     * @param field The field to be extracted
     * @return A map containing the extracted string and the note without this special field
     */
    public ExtractedNoteFieldResult extractFieldFromNote(String note, String fieldPrefix) {
        ExtractedNoteFieldResult result = new ExtractedNoteFieldResult(note);

        if (note != null) {
            // Lets see if we can find this field
            int fieldStart = note.indexOf(fieldPrefix);
            if (fieldStart > -1) {
                // We have found this field, so move the start to the end
                int dataStart = fieldStart + fieldPrefix.length();

                // Let us find the end of the data
                int fieldEnd = note.indexOf(NoteSpecials.SPECIAL_WRAPPER, dataStart);

                // Have we found a field end
                if (fieldEnd > -1) {
                    // We have, do not forget to exclude the terminator
                    result.data = note.substring(dataStart, fieldEnd);

                    // Now need to replace this section of the note
                    StringBuilder updatedNote = new StringBuilder(note);
                    updatedNote.delete(fieldStart, fieldEnd + 1);
                    result.note = updatedNote.toString();
                }
            }
        }

        // Return the result to the caller
        return(result);
    }
}

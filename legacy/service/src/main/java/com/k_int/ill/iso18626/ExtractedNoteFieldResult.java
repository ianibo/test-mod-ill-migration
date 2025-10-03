package com.k_int.ill.iso18626;

public class ExtractedNoteFieldResult {

    /** The data that has been extracted from the field */
    public String data;

    /** The resulting note without the extracted field */
    public String note;

	public ExtractedNoteFieldResult(String note) {
        // By default we will assume the note ends up being the same as the original
        this.note = note;
	}
}

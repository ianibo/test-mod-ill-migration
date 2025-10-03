package com.k_int.ill.iso18626.codes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Code {
	/**
	 * Holds the list of all the codes for the derived class
	 */
	public static final List<String> ALL = new ArrayList<String>();

	/** The value that represents this code */
	@JacksonXmlText
	public String code;

	public Code() {
	}

	public Code(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return(code);
	}

	/**
	 * Adds a code to list of valid codes, if the code is null or already exists it is not added
	 * @param code The code to tbe added
	 */
	public static void add(String code) {
		// Have we been supplied a code
		if (code != null) {
			// Have we previously added it
			if (!ALL.contains(code)) {
				// We havn't so add it
				ALL.add(code);
			}
		}
	}

	/**
	 * Checks to see if the supplied string is a valid request type
	 * @param requestSubType
	 * @return true if it is valid otherwise false
	 */
	public static boolean isValid(String requestSubType) {
		boolean result = false;
		if (requestSubType != null) {
			result = ALL.contains(requestSubType);
		}
		return(result);
	}
}

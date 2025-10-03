package com.k_int;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.k_int.ill.statemodel.ActionResult;

/**
 * Holds the outcome of some processing that went on in a service, that the controller can pass back to the caller
 */
public class GenericResult {
	/** The result of performing what was requested */
	public ActionResult result = ActionResult.SUCCESS;

	/** The id that was acted upon or the result of any processing, if any */
	public String id;

	/** If we were unsuccessful, the error code to be returned (not HTTP) */
	public String errorCode;

	/** Any messages that will be returned to the caller */
	public List<String> messages = new ArrayList<String>();

	public GenericResult() {
	}

	public GenericResult(String id) {
		this.id =id;
	}

	/**
	 * Adds a message to the result
	 * @param message The message to be added
	 */
	public void addMessage(String message) {
		// If we have a message then add it to the messages list
		if (StringUtils.isNotBlank(message)) {
			messages.add(message);
		}
	}

	/**
	 *Marks the result as being in error
	 * @param message A message that will be passed back to the user, that explains the error
	 */
	public void error(String message) {
		error(message, null, null);
	}
	
	/**
	 *Marks the result as being in error
	 * @param message A message that will be passed back to the user, that explains the error
	 * @param errorCode An informative error code that gives more information about the error
	 */
	public void error(String message, String errorCode) {
		error(message, errorCode, null);
	}
	
	/**
	 *Marks the result as being in error
	 * @param message A message that will be passed back to the user, that explains the error
	 * @param errorCode An informative error code that gives more information about the error
	 * @param exception The exception that triggered the error or null if an exception did not occur
	 */
	public void error(String message, String errorCode, Exception exception) {
		// As we had an error, set the result to reflect this
		result = ActionResult.ERROR;

		// Add the message
		addMessage(message);

		// If we have been passed an exception, extract the stacktrace and add it to the messages
		if (exception != null) {
			addMessage(exception.getMessage());
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter, true);
			exception.printStackTrace(printWriter);
			addMessage(stringWriter.toString());
		}

		// Finally set the error code if we have been supplied one
		if (StringUtils.isNotBlank(errorCode)) {
			this.errorCode = errorCode;
		}
	}
}

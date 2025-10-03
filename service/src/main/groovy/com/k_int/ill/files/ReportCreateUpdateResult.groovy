package com.k_int.ill.files;

import com.k_int.ill.reporting.Report;

/**
 * Holds the outcome from attempting to create a file
 */
public class ReportCreateUpdateResult {
	/** Report record that was created */
	public Report report;

	/** The error that occurred if any */
	public String error;
}

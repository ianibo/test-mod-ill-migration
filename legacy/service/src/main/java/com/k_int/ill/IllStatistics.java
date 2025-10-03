package com.k_int.ill;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The statistics for the requests within the system
 */
public class IllStatistics {
    /** The date / time the statistics were generated */
    public Date asAt;

    /** The counter statistics */
    public List<IllStatisticCounter> current = new ArrayList<IllStatisticCounter>();

    /** The breakdown by state */
    public Map<String, Long> requestsByState = new HashMap<String, Long>();

    /** The breakdown by tag */
    public Map<String, Long> requestsByTag = new HashMap<String, Long>();

    /** Holds any error that occurred while generating the statistics */
    public String error;

    public IllStatistics() {
        asAt = new Date();
    }

	public IllStatisticCounter addCurrent(
		String context,
		long value,
		String description
	) {
		IllStatisticCounter illStatisticCounter = new IllStatisticCounter(context, value, description);
		current.add(illStatisticCounter);
		return(illStatisticCounter);
	}

	public void addState(String state, long value) {
		requestsByState.put(state, Long.valueOf(value));
	}

	public void addTag(String tag, long value) {
		requestsByTag.put(tag, Long.valueOf(value));
	}
	
	public void setError(String error) {
		this.error = error;
	}
}

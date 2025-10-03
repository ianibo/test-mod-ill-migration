package com.k_int.ill;

/**
 * Defines a counter statistic
 */
public class IllStatisticCounter {
    /** The context of the counter statistic */
    public String context;

    /** The value associated with this statistic */
    public long value;

    /** The description for this statistic */
    public String description;

    public IllStatisticCounter(
        String context,
        long value,
        String description
    ) {
        this.context = context;
        this.value = value;
        this.description = description;
    }

    public String toString() {
    	return("Context: " + context + ", value: " + value + ", description: " + description);
    }
}

package com.k_int;

public class LabelValue {

    public String label;
    public String value;

    public LabelValue(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public boolean valid() {
        return((label != null) && (value != null) && !label.isEmpty() && !value.isEmpty());
    }
}

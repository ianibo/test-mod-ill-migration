package com.k_int.ill;

import com.k_int.directory.DirectoryEntry;

import groovy.transform.CompileStatic;

@CompileStatic
public class NewRequestPickupLocation {

    public String label;
    public String value;

    public NewRequestPickupLocation(DirectoryEntry directoryEntry) {
        label = directoryEntry.name;
        value = directoryEntry.slug;
    }

    public boolean valid() {
        return(label && value);
    }
}

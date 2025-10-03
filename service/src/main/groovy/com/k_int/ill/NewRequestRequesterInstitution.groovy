package com.k_int.ill;

import com.k_int.directory.DirectoryEntry;

import groovy.transform.CompileStatic;

@CompileStatic
public class NewRequestRequesterInstitution {

    public String label;
    public String value;

    public NewRequestRequesterInstitution(DirectoryEntry directoryEntry) {
        label = directoryEntry.name;
        value = directoryEntry.getFirstSymbol();
    }

    public boolean valid() {
        return(label && value);
    }
}

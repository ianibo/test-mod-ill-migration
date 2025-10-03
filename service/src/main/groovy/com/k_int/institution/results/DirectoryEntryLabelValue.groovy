package com.k_int.institution.results;

import com.k_int.LabelValue;
import com.k_int.directory.DirectoryEntry;

import groovy.transform.CompileStatic;

@CompileStatic
public class DirectoryEntryLabelValue extends LabelValue{

    public DirectoryEntryLabelValue(DirectoryEntry directoryEntry) {
        super(directoryEntry.name, directoryEntry.id);
    }
}

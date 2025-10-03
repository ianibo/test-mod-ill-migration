package com.k_int.ill.sharedindex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedIndexBibRecord {

    private static final String OCLC_NAMESPACE = "OCoLC";

    private static final Pattern OCLC_IDENTIFIER_PREFIX_PATTERN = ~/^(ocn|ocm|on)(\d+)/;

    public String bibClusterId;

    public List<SharedIndexBibRecordAgent> agents;
    public List<String> bibNotes;
    public List<String> contents;
    public List<String> contentTypes;
    public String dateOfPublication;
    public String derivedType;
    public String edition;
    public List<SharedIndexBibRecordIdentifier> identifiers;
    public List<String> language;
    public String isbn;
    public String issn;
    public List<String> mediaTypes;
    public List<String> notes;
    public List<String> physicalDescriptions;
    public String placeOfPublication;
    public String primaryAuthor;
    public String publisher;
    public List<String> series;
    public List<SharedIndexBibRecordSubject> subjects;
    public List<String> summary;
    public String title;
    public String yearOfPublication;

    public OpenRsClusterRecord() {
    }

    public String getOclcNumber() {
        String oclcIdentifier = null;

        // Loop through all the identifiers, trying to identify it as one from oclc
        if (identifiers != null) {
            identifiers.each{ SharedIndexBibRecordIdentifier sharedIndexBibRecordIdentifier ->
                if (OCLC_NAMESPACE.equals(sharedIndexBibRecordIdentifier.namespace)) {
                    // We have an oclc identifier
                    oclcIdentifier = sharedIndexBibRecordIdentifier.value;
                } else {
                    // Look to see if the identifier has an oclc specific prefix
                    Matcher matcher = sharedIndexBibRecordIdentifier.value =~ OCLC_IDENTIFIER_PREFIX_PATTERN;
                    if (matcher.find()) {
                        oclcIdentifier = matcher.group(2);
                    }
                }
            }
        }

        // If we reached here it means we did not find an oclc number
        return(oclcIdentifier);
    }
}

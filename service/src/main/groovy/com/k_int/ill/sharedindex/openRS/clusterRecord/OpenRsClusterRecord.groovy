package com.k_int.ill.sharedindex.openRS.clusterRecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.k_int.ill.sharedindex.SharedIndexBibRecord;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenRsClusterRecord {

    public String bibClusterId;
    public String dateOfPublication;
    public String derivedType;
    public String isbn;
    public String issn;
    public List<OpenRsClusterRecordMember> members;
    public OpenRsClusterRecordMetadata metadata;
    public String placeOfPublication;
    public String primaryAuthor;
    public String publisher;
    public String title;
    public String yearOfPublication;

    public OpenRsClusterRecord() {
    }

    public SharedIndexBibRecord toSharedIndexBibRecord() {
        SharedIndexBibRecord sharedIndexBibRecord = new SharedIndexBibRecord();
        sharedIndexBibRecord.bibClusterId = bibClusterId;
        sharedIndexBibRecord.dateOfPublication = dateOfPublication;
        sharedIndexBibRecord.derivedType = derivedType;
        sharedIndexBibRecord.isbn = isbn;
        sharedIndexBibRecord.issn = issn;
        sharedIndexBibRecord.placeOfPublication = placeOfPublication;
        sharedIndexBibRecord.primaryAuthor = primaryAuthor;
        sharedIndexBibRecord.publisher = publisher;
        sharedIndexBibRecord.title = title;
        sharedIndexBibRecord.yearOfPublication = yearOfPublication;

        // Finally the metadata
        if (metadata != null) {
            metadata.toSharedIndexBibRecord(sharedIndexBibRecord);
        }

        // Return ths bib record to the caller
        return(sharedIndexBibRecord);
    }
}

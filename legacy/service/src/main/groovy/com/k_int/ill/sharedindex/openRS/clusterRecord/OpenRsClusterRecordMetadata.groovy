package com.k_int.ill.sharedindex.openRS.clusterRecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.k_int.ill.sharedindex.SharedIndexBibRecord;
import com.k_int.ill.sharedindex.SharedIndexBibRecordAgent;
import com.k_int.ill.sharedindex.SharedIndexBibRecordIdentifier;
import com.k_int.ill.sharedindex.SharedIndexBibRecordSubject;

import groovy.transform.CompileStatic;

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenRsClusterRecordMetadata {

    public List<OpenRsClusterRecordAgent> agents;
    public List<String> bibNotes;

    @JsonProperty("content-type")
    public List<OpenRsClusterRecordContentType> contentTypes;
    public List<String> Agents;
    public List<String> contents;
    public String dateOfPublication;
    public String derivedType;
    public String edition;
    public List<OpenRsClusterRecordIdentifier> identifiers;
    public List<String> language;
    @JsonProperty("media-type")
    public List<OpenRsClusterRecordMediaType> mediaTypes;
    public List<String> notes;
    @JsonProperty("physical-description")
    public List<OpenRsClusterRecordPhysicalDescription> physicalDescriptions;
    public String placeOfPublication;
    public String publisher;
    public String recordStatus;
    public List<String> series;
    public List<OpenRsClusterRecordSubject> subjects;
    public List<String> summary;
    public String title;

    public OpenRsClusterRecordMetadata() {
    }

    public void toSharedIndexBibRecord(SharedIndexBibRecord sharedIndexBibRecord) {
        sharedIndexBibRecord.bibNotes = bibNotes;
        sharedIndexBibRecord.contents = contents;
        sharedIndexBibRecord.edition = edition;
        sharedIndexBibRecord.language = language;
        sharedIndexBibRecord.notes = notes;
        sharedIndexBibRecord.series = series;
        sharedIndexBibRecord.summary = summary;

        // Convert the agents
        if (agents != null) {
            sharedIndexBibRecord.agents = new ArrayList<SharedIndexBibRecordAgent>();
            agents.each { OpenRsClusterRecordAgent openRsClusterRecordAgent ->
                sharedIndexBibRecord.agents.add(new SharedIndexBibRecordAgent(openRsClusterRecordAgent.label, openRsClusterRecordAgent.subtype));
            }
        }

        // Convert the content types
        if (contentTypes != null) {
            sharedIndexBibRecord.contentTypes = new ArrayList<String>();
            contentTypes.each { OpenRsClusterRecordContentType openRsClusterRecordContentType ->
                sharedIndexBibRecord.contentTypes.add(openRsClusterRecordContentType.label);
            }
        }

        // Convert the identifiers
        if (identifiers != null) {
            sharedIndexBibRecord.identifiers = new ArrayList<SharedIndexBibRecordIdentifier>();
            identifiers.each { OpenRsClusterRecordIdentifier openRsClusterRecordIdentifier ->
                sharedIndexBibRecord.identifiers.add(new SharedIndexBibRecordIdentifier(openRsClusterRecordIdentifier.namespace, openRsClusterRecordIdentifier.value));
            }
        }

        // Convert the media types
        if (mediaTypes != null) {
            sharedIndexBibRecord.mediaTypes = new ArrayList<String>();
            mediaTypes.each { OpenRsClusterRecordMediaType openRsClusterRecordMediaType ->
                sharedIndexBibRecord.mediaTypes.add(openRsClusterRecordMediaType.label);
            }
        }

        // Convert the physical descriptions
        if (physicalDescriptions != null) {
            sharedIndexBibRecord.physicalDescriptions = new ArrayList<String>();
            physicalDescriptions.each { OpenRsClusterRecordPhysicalDescription openRsClusterRecordPhysicalDescription ->
                sharedIndexBibRecord.physicalDescriptions.add(openRsClusterRecordPhysicalDescription.label);
            }
        }

        // Convert the subjects
        if (subjects != null) {
            sharedIndexBibRecord.subjects = new ArrayList<SharedIndexBibRecordSubject>();
            subjects.each { OpenRsClusterRecordSubject openRsClusterRecordSubject ->
                sharedIndexBibRecord.subjects.add(new SharedIndexBibRecordSubject(openRsClusterRecordSubject.label, openRsClusterRecordSubject.subtype));
            }
        }
    }
}

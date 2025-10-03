package com.k_int.data.entity;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.Set;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor(onConstructor_ = @Creator())
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@Serdeable
@MappedEntity(value = "patron_request_document")
public class PatronRequestDocument {

    @Id
    @Column(name = "prd_id")
    private UUID id;

    @DateCreated
    @Column(name = "prd_date_created")
    private ZonedDateTime dateCreated;

    @Column(name = "prd_position")
    private int position;

    @Column(name = "prd_file_definition")
    private UUID fileDefinition; // Representing FileDefinition as UUID for now

    @Column(name = "prd_url", length = 512)
    private String uri;

    @ManyToOne
    @JoinColumn(name = "prd_patron_request")
    private PatronRequest patronRequest;

    // The audit relationship will be added later
    // @OneToMany(mappedBy = "patronRequestDocument")
    // private Set<PatronRequestDocumentAudit> audit;
}
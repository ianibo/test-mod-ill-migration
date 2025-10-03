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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@MappedEntity(value = "batch")
public class Batch {

    @Id
    @Column(name = "b_id")
    private UUID id;

    @Column(name = "b_description", length = 256)
    private String description;

    @Column(name = "b_context", length = 32)
    private String context;

    @DateCreated
    @Column(name = "b_date_created")
    private ZonedDateTime dateCreated;

    @Column(name = "b_is_requester")
    private boolean isRequester;

    @Column(name = "b_institution_id")
    private UUID institution; // Representing Institution as UUID for now

    @ManyToMany
    @JoinTable(
        name = "batch_patron_request",
        joinColumns = @JoinColumn(name = "bpr_batch_id"),
        inverseJoinColumns = @JoinColumn(name = "bpr_patron_request_id")
    )
    private Set<PatronRequest> patronRequests;
}
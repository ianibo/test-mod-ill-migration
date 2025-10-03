package com.k_int.data.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@MappedEntity(value = "patron_request_loan_condition")
public class PatronRequestLoanCondition {

    @Id
    @Column(name = "prlc_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "prlc_patron_request_fk")
    private PatronRequest patronRequest;

    @DateCreated
    @Column(name = "prlc_date_created")
    private ZonedDateTime dateCreated;

    @DateUpdated
    @Column(name = "prlc_last_updated")
    private ZonedDateTime lastUpdated;

    @Column(name = "prlc_accepted")
    @Builder.Default
    private Boolean accepted = false;

    @Column(name = "prlc_code")
    private String code;

    @Column(name = "prlc_note")
    private String note;

    @Column(name = "prlc_relevant_supplier_fk")
    private UUID relevantSupplier; // Representing Symbol as UUID for now
}
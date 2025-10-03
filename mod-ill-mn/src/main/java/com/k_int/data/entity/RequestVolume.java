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
@MappedEntity(value = "request_volume")
public class RequestVolume {

    @Id
    @Column(name = "rv_id")
    private UUID id;

    @Column(name = "rv_name")
    private String name;

    @Column(name = "rv_item_id")
    private String itemId;

    @ManyToOne
    @JoinColumn(name = "rv_patron_request_fk")
    private PatronRequest patronRequest;

    @DateCreated
    @Column(name = "rv_date_created")
    private ZonedDateTime dateCreated;

    @DateUpdated
    @Column(name = "rv_last_updated")
    private ZonedDateTime lastUpdated;

    @Column(name = "rv_temporary_item_barcode")
    private String temporaryItemBarcode;

    @Column(name = "rv_status_fk")
    private UUID status; // Representing RefdataValue as UUID for now
}
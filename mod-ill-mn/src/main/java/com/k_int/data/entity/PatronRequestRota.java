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
@MappedEntity(value = "patron_request_rota")
public class PatronRequestRota {

    @Id
    @Column(name = "prr_id")
    private UUID id;

    @DateCreated
    @Column(name = "prr_date_created")
    private ZonedDateTime dateCreated;

    @DateUpdated
    @Column(name = "prr_last_updated")
    private ZonedDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "prr_patron_request_fk")
    private PatronRequest patronRequest;

    @Column(name = "prr_rota_position")
    private Long rotaPosition;

    @Column(name = "prr_directory_id_fk", length = 36)
    private String directoryId;

    @Column(name = "prr_system_identifier")
    private String systemIdentifier;

    @Column(name = "prr_shelfmark")
    private String shelfmark;

    @Column(name = "prr_availability")
    private String availability;

    @Column(name = "prr_normalised_availability")
    private String normalisedAvailability; // Representing NormalisedAvailability enum as String

    @Column(name = "prr_available_from")
    private ZonedDateTime availableFrom;

    @Column(name = "prr_protocol_status")
    private Long protocolStatus;

    @Column(name = "prr_lb_score")
    private Long loadBalancingScore;

    @Column(name = "prr_lb_reason", columnDefinition = "text")
    private String loadBalancingReason;

    @Column(name = "prr_state_fk")
    private String state; // Representing Status as String for now

    @Column(name = "prr_peer_symbol_fk")
    private UUID peerSymbol; // Representing Symbol as UUID for now

    @Column(name = "prr_note")
    private String note;

    @Column(name = "prr_instance_identifier")
    private String instanceIdentifier;

    @Column(name = "prr_copy_identifier")
    private String copyIdentifier;

    @Column(name = "prr_protocol")
    private UUID protocol; // Representing Protocol as UUID for now
}
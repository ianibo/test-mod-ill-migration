package com.k_int.data.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.DateCreated;
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
@MappedEntity(value = "patron_request_audit")
public class PatronRequestAudit {

    @Id
    @Column(name = "pra_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pra_patron_request_fk")
    private PatronRequest patronRequest;

    @DateCreated
    @Column(name = "pra_date_created")
    private ZonedDateTime dateCreated;

    @Column(name = "pra_from_status_fk")
    private String fromStatus; // Will be replaced with Status entity later

    @Column(name = "pra_to_status_fk")
    private String toStatus; // Will be replaced with Status entity later

    @Column(name = "pra_duration")
    private Long duration;

    @Column(name = "pra_message", columnDefinition = "text")
    private String message;

    @Column(name = "pra_audit_data", columnDefinition = "text")
    private String auditData;

    @Column(name = "pra_user")
    private String user;

    @Column(name = "pra_audit_no")
    private Integer auditNo;

    @Column(name = "pra_action_event")
    private String actionEvent; // Will be replaced with ActionEvent entity later

    @Column(name = "pra_rota_position")
    private Long rotaPosition;

    @Column(name = "pra_undo_performed")
    private Boolean undoPerformed;

    @Column(name = "pra_message_sequence_no")
    private Integer messageSequenceNo;
}
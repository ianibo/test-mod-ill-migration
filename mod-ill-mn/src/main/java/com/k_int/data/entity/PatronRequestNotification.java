package com.k_int.data.entity;

import java.time.Instant;
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
@MappedEntity(value = "patron_request_notification")
public class PatronRequestNotification {

    @Id
    @Column(name = "prn_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "prn_patron_request_fk")
    private PatronRequest patronRequest;

    @DateCreated
    @Column(name = "prn_date_created")
    private ZonedDateTime dateCreated;

    @DateUpdated
    @Column(name = "prn_last_updated")
    private ZonedDateTime lastUpdated;

    @Column(name = "prn_timestamp")
    private Instant timestamp;

    @Column(name = "prn_seen")
    private Boolean seen;

    @Column(name = "prn_is_sender")
    private Boolean isSender;

    @Column(name = "prn_attached_action")
    private String attachedAction;

    @Column(name = "prn_action_status")
    private String actionStatus;

    @Column(name = "prn_action_data")
    private String actionData;

    @Column(name = "prn_message_sender_fk")
    private UUID messageSender; // Representing Symbol as UUID for now

    @Column(name = "prn_message_receiver_fk")
    private UUID messageReceiver; // Representing Symbol as UUID for now

    @Column(name = "prn_message_content")
    private String messageContent;
}
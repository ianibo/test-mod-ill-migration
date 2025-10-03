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
@MappedEntity(value = "protocol_audit")
public class ProtocolAudit {

    @Id
    @Column(name = "pa_id")
    private UUID id;

    @DateCreated
    @Column(name = "pa_date_created")
    private ZonedDateTime dateCreated;

    @ManyToOne
    @JoinColumn(name = "pa_patron_request")
    private PatronRequest patronRequest;

    @Column(name = "pa_protocol_type", length = 30)
    private String protocolType; // Representing ProtocolType enum as String

    @Column(name = "pa_protocol_method", length = 30)
    private String protocolMethod; // Representing ProtocolMethod enum as String

    @Column(name = "pa_uri")
    private String url;

    @Column(name = "pa_request_body", columnDefinition = "text")
    private String requestBody;

    @Column(name = "pa_response_status", length = 30)
    private String responseStatus;

    @Column(name = "pa_response_body", columnDefinition = "text")
    private String responseBody;

    @Column(name = "pa_duration")
    private Long duration;
}
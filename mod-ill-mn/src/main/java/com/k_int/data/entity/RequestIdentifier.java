package com.k_int.data.entity;

import java.util.UUID;

import io.micronaut.core.annotation.Creator;
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
@MappedEntity(value = "request_identifier")
public class RequestIdentifier {

    @Id
    @Column(name = "ri_id")
    private UUID id;

    @Column(name = "ri_identifier_type")
    private String identifierType;

    @Column(name = "ri_identifier")
    private String identifier;

    @ManyToOne
    @JoinColumn(name = "ri_patron_request")
    private PatronRequest patronRequest;
}
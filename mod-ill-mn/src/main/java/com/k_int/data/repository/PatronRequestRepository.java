package com.k_int.data.repository;

import com.k_int.data.entity.PatronRequest;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;
import java.util.UUID;

import io.micronaut.data.model.query.builder.sql.Dialect;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PatronRequestRepository extends CrudRepository<PatronRequest, UUID> {
}
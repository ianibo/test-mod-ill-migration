package com.k_int.data.repository;

import com.k_int.data.entity.ProtocolAudit;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ProtocolAuditRepository extends CrudRepository<ProtocolAudit, UUID> {
}
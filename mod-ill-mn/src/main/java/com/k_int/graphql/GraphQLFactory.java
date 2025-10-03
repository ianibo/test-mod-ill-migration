package com.k_int.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.errors.SchemaMissingError;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.ResourceResolver;
import jakarta.inject.Singleton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.scalars.ExtendedScalars;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Factory
public class GraphQLFactory {

  @Bean
  @Singleton
  public GraphQL graphQL() {
    SchemaParser schemaParser = new SchemaParser();
    SchemaGenerator schemaGenerator = new SchemaGenerator();

		/*
		EXAMPLE LAYOUT - UNCOMMENT AND REPLACE WITH LIVE DATA CLASSES

    // Load the schema
    InputStream schemaDefinition = resourceResolver.getResourceAsStream("classpath:schema.graphqls")
        .orElseThrow(SchemaMissingError::new);

    // Parse the schema and merge it into a type registry
    TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
    typeRegistry.merge(schemaParser.parse(new BufferedReader(new InputStreamReader(schemaDefinition))));

    log.debug("Add runtime wiring");

    RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
      .type(TypeRuntimeWiring.newTypeWiring("Query"))
      .type("Mutation", typeWiring -> typeWiring)
      .build();

    // Create the executable schema.
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

    log.debug("returning {}", graphQLSchema.toString());

    // Return the GraphQL bean.
    return GraphQL.newGraphQL(graphQLSchema).build();
		*/
		return null;
	}
}


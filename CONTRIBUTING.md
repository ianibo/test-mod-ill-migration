# Contributing

# Technical Standards

## General

The target project is micronaut 4 please follow the following rules
- Use micronaut-data-JDBC for persistence in the target repository
- The target java version is JDK21
- Use lombok for any POJOs that need serialisation
- Created domain classes should use the following annotations
  - @lombok.Builder
  - @lombok.Data
  - @lombok.NoArgsConstructor(onConstructor_ = @Creator())
  - @lombok.AllArgsConstructor
  - @lombok.experimental.Accessors(chain = true)
  - @lombok.ToString
  - @io.micronaut.serde.annotation.Serdeable
  - @io.micronaut.data.annotation.MappedEntity
- Created Repository classes should
  - extend the io.micronaut.data.repository.CrudRepository base
- Prefer graphql for CRUD operations over REST controllers
- You should Update the grqaphql.schemas file with Query and Mutation entries for each domain class
  - Geneate additional GraphQL Page and input objects for each domain resource
- Update mod-ill-mn/src/main/java/com/k_int/graphql/GraphQLFactory.java with factories for each domain class
- Additional rules
  - Use the lombok.Slf4j annotation to provide logging to any non pojo classes
    - Do not add Slf4j to
      - @io.micronaut.data.annotation.MappedEntity
      - @io.micronaut.data.jdbc.annotation.JdbcRepository
  - Prefer Lombok DTOs when creating POJOs

## Testing

- Prefer API-Driven end to end testing to white box testing - especially in complex scenarios
- Keep unit tests for small self contained functionality
- Don't use mocking with unit tests to set up complex preconditions that artificially hide preconditions
- Prefer using the published API to configure a system in the ready-state for any complex testing
- This ensures that if a dependent behaviour changes the test breaks in the right place
- Fixtures can be used to wind the system into a pre-test state, but go through the published API, don't directly insert the data you need for the test
- Each domain class and repository combo should be tested through a graphql call to create, retrieve, update and delete instances of that entity
  - Some tests may be more complex where foreign key dependencies are in play

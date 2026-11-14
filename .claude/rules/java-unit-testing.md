---
paths:
  - '**/*.java'
---

# Java Unit Testing Guidelines (Spring Boot 3)

This rule provides focused guidance for generating strong, idiomatic unit tests in Java-based Spring Boot 3 projects.

## Testing Style and Principles

- Write pure unit tests: avoid Spring context loading.
- Use mocking for all external dependencies (e.g., repositories, APIs).
- Do not use `@SpringBootTest` unless writing an integration test (see integration testing guidelines).

## Test File Naming Conventions

Name test files by test type:

- **Pure Unit Tests:** `ClassNameTest.java` - Use `@ExtendWith(MockitoExtension.class)`
- **Web Layer Tests:** `ClassNameWebTest.java` - Use `@WebMvcTest` + `MockMvc`
- **Integration Tests:** `ClassNameIntegrationTest.java` - Use `@SpringBootTest` + `@Tag("integration")`

**Examples:**

```
ApiKeyServiceTest.java              → Pure unit test for service layer
ApiKeyApiDelegateImplTest.java      → Pure unit test for API delegate logic
ApiKeyApiDelegateImplWebTest.java   → Web layer test with @WebMvcTest
UserServiceIntegrationTest.java     → Full integration test with @SpringBootTest
```

**Why This Convention:**

- Pure unit tests are most common, so they get the simple `Test.java` suffix
- Web layer tests get descriptive `WebTest.java` suffix to indicate HTTP testing
- Integration tests use established `IntegrationTest.java` pattern
- Clear distinction between testing approaches and scope

**When to Use Each Type:**

- **Pure Unit Tests (`ClassNameTest.java`)**: For service classes, utilities, entities, and API delegate business logic
- **Web Layer Tests (`ClassNameWebTest.java`)**: For Spring Boot controllers and API delegates testing HTTP endpoints
- **Integration Tests (`ClassNameIntegrationTest.java`)**: For full application testing with database and external services

## Use These Frameworks

When generating unit tests, use:

- JUnit 5 (Jupiter)
- Mockito for mocking
- AssertJ for fluent assertions

Include these annotations and utilities:

```java
@ExtendWith(MockitoExtension.class)
@InjectMocks
@Mock
```

For assertions:

```java
assertThat(result).isEqualTo(expected);
```

## Common Test Structures

Typical test class structure:

```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {

  @Mock
  private MyRepository repository;

  @InjectMocks
  private MyService service;

  @Test
  @DisplayName("should return data when repository returns value")
  void shouldReturnDataWhenRepositoryReturnsValue() {
    // given
    when(repository.getData()).thenReturn("value");

    // when
    String result = service.getData();

    // then
    assertThat(result).isEqualTo("value");
  }
}

```

### Method Naming Convention

Name test methods in CamelCase and keep @DisplayName in the same words, lowercase:

**Method Names:** Use the pattern `should<ExpectedBehavior>When<Condition>()`
**@DisplayName:** Use lowercase "should" with descriptive text

```java
@Test
@DisplayName("should return user when email is valid")
void shouldReturnUserWhenEmailIsValid() {
  // test implementation
}

@Test
@DisplayName("should throw exception when input is null")
void shouldThrowExceptionWhenInputIsNull() {
  // test implementation
}

```

Not `login_ShouldReturn_WhenValid()` (underscores) or `@DisplayName("Should …")` (capitalized).

## Project Execution (Gradle)

### Multi-Module Gradle Workspace

This Sage monorepo uses a multi-module Gradle setup. Run all Gradle tasks from the repository root using the project path notation.

**Examples:**

```bash
# Run from repository root /workspaces/sage-monorepo
./gradlew :bixarena-api:test --info
./gradlew :bixarena-auth-service:test --info
./gradlew :agora-api-next:test --info
```

### Running Specific Tests

When running tests in the multi-module workspace, use targeted commands with project path notation:

```bash
# RECOMMENDED: Start with specific test class to identify issues quickly
./gradlew :bixarena-api:test --tests "*YourTestClass*" --stacktrace --no-daemon --console=plain

# If tests fail, use --rerun-tasks to bypass cache
./gradlew :bixarena-api:test --tests "*YourTestClass*" --rerun-tasks --stacktrace --no-daemon --console=plain

# After fixes, verify all unit tests pass for the specific project
./gradlew :bixarena-api:testUnit --no-daemon --console=plain

# Finally, verify integration tests still work for the specific project
./gradlew :bixarena-api:testIntegration --no-daemon --console=plain
```

`--no-daemon --console=plain` avoids terminal-environment issues in this container; `test` compiles, so no separate compile step is needed.

**Test Separation:**

- **Unit tests** (`testUnit`): Fast execution, no Spring context, use `@ExtendWith(MockitoExtension.class)`
- **Integration tests** (`testIntegration`): Slower execution, full Spring context, use `@SpringBootTest` and `@Tag("integration")`

## Clean Code Rules

- Keep tests short and isolated
- Avoid real HTTP calls or database operations
- Use Arrange-Act-Assert (AAA) structure
- Prefer one assertion per test (unless logically grouped)

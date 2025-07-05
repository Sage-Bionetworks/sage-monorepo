---
applyTo: '**/*.java'
---

# Copilot Instructions: Unit Testing for Java (Spring Boot)

This file provides focused guidance for GitHub Copilot to generate strong, idiomatic unit tests in Java-based Spring Boot 3 projects.

## Testing Style and Principles

- Write pure unit tests: avoid Spring context loading.
- Use mocking for all external dependencies (e.g., repositories, APIs).
- Do not use `@SpringBootTest` unless writing an integration test (see `java-integration-testing.md`).

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

Use the pattern: `should<ExpectedBehavior>When<Condition>()`

Examples:

- `shouldReturnUserWhenEmailIsValid()`
- `shouldThrowExceptionWhenInputIsNull()`

## Project Execution (Gradle)

### Monorepo Structure

In this Sage monorepo, each Java project has its own `gradlew` wrapper. Navigate to the specific project directory before running tests.

**For OpenChallenges projects:**

```bash
cd /workspaces/sage-monorepo/apps/openchallenges/auth-service
./gradlew test --info
```

**For other projects, examples:**

```bash
cd /workspaces/sage-monorepo/apps/amp-als/user-service
./gradlew test --info
```

### Running Specific Tests

When running tests from a Java subproject, use targeted commands to minimize execution time and get meaningful feedback:

```bash
# RECOMMENDED: Start with specific test class to identify issues quickly
./gradlew test --tests "*YourTestClass*" --stacktrace --no-daemon --console=plain

# If tests fail, use --rerun-tasks to bypass cache
./gradlew test --tests "*YourTestClass*" --rerun-tasks --stacktrace --no-daemon --console=plain

# After fixes, verify all unit tests pass
./gradlew testUnit --no-daemon --console=plain

# Finally, verify integration tests still work
./gradlew testIntegration --no-daemon --console=plain
```

**Efficient Testing Strategy:**

1. **Target specific test class first** - Don't run all tests initially
2. **Use `--stacktrace` for failures** - Gets full error details immediately
3. **Use `--no-daemon --console=plain`** - Avoids terminal environment issues
4. **Only use `--rerun-tasks` when needed** - When you suspect cache issues
5. **Avoid redundant compilation checks** - `test` task includes compilation

**Test Separation:**

- **Unit tests** (`testUnit`): Fast execution, no Spring context, use `@ExtendWith(MockitoExtension.class)`
- **Integration tests** (`testIntegration`): Slower execution, full Spring context, use `@SpringBootTest` and `@Tag("integration")`

**Command Execution Tips:**

- **One targeted command is better than multiple broad ones**
- **Full stacktrace on first failure reveals root cause immediately**
- **Don't check compilation separately - test task will catch compile errors**
- **Use plain console output to avoid terminal formatting issues**

## Copilot Prompt Examples

Use inline comments or prompts like:

```java
// Generate a unit test for MyService that verifies getData() returns expected value when repository provides it

```

```java
// Unit test: should throw IllegalArgumentException when input is empty

```

```java
// Mock repository to return optional empty and verify service throws NotFoundException

```

## Clean Code Rules

- Keep tests short and isolated
- Avoid real HTTP calls or database operations
- Use Arrange-Act-Assert (AAA) structure
- Prefer one assertion per test (unless logically grouped)

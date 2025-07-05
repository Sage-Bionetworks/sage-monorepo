---
applyTo: '**/*.java'
---

# Copilot Instructions: Unit Testing for Java (Spring Boot)

This file provides focused guidance for GitHub Copilot to generate strong, idiomatic unit tests in Java-based Spring Boot 3 projects.

## Testing Style and Principles

- Write pure unit tests: avoid Spring context loading.
- Use mocking for all external dependencies (e.g., repositories, APIs).
- Do not use `@SpringBootTest` unless writing an integration test (see `java-integration-testing.md`).

## Test File Naming Conventions

**STRICT REQUIREMENT:** Follow consistent naming patterns for different types of tests.

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

**STRICT REQUIREMENT:** Use consistent CamelCase naming for both method names and @DisplayName annotations.

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

**❌ AVOID these inconsistent patterns:**

- Method names with underscores: `void login_ShouldReturn_WhenValid()`
- Uppercase "Should" in @DisplayName: `@DisplayName("Should return user...")`
- Mixed casing patterns within the same test class

**✅ CORRECT examples:**

- `shouldAuthenticateUserWhenCredentialsAreValid()`
- `shouldReturnEmptyWhenUserNotFound()`
- `shouldThrowExceptionWhenParameterIsNull()`

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

**When generating tests, ensure:**

- Method names use CamelCase: `shouldDoSomethingWhenConditionExists()`
- @DisplayName uses lowercase "should": `@DisplayName("should do something when condition exists")`
- Consistent naming patterns throughout the test class

## Clean Code Rules

- Keep tests short and isolated
- Avoid real HTTP calls or database operations
- Use Arrange-Act-Assert (AAA) structure
- Prefer one assertion per test (unless logically grouped)
- **CONSISTENCY:** Always use the same naming convention within a test class
- **NO MIXED PATTERNS:** Don't mix CamelCase and underscore naming in the same project

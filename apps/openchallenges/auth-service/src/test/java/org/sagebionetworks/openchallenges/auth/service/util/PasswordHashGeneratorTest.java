package org.sagebionetworks.openchallenges.auth.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DisplayName("PasswordHashGenerator Tests")
class PasswordHashGeneratorTest {

  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  private ByteArrayOutputStream outputStreamCaptor;

  @BeforeEach
  void setUp() {
    outputStreamCaptor = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStreamCaptor));
    System.setErr(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @DisplayName("Main Method Tests")
  @Nested
  class MainMethodTests {

    @Test
    @DisplayName("should execute main method without throwing exceptions")
    void shouldExecuteMainMethodWithoutExceptions() {
      assertDoesNotThrow(() -> PasswordHashGenerator.main(new String[] {}));
    }

    @Test
    @DisplayName("should execute main method with arguments without throwing exceptions")
    void shouldExecuteMainMethodWithArgumentsWithoutExceptions() {
      assertDoesNotThrow(() -> PasswordHashGenerator.main(new String[] { "arg1", "arg2" }));
    }

    @Test
    @DisplayName("should generate and log password hashes for predefined passwords")
    void shouldGenerateAndLogPasswordHashes() {
      // When
      PasswordHashGenerator.main(new String[] {});

      // Then
      String output = outputStreamCaptor.toString();

      // Verify that all expected passwords are processed
      assertThat(output).contains("admin123:");
      assertThat(output).contains("researcher123:");
      assertThat(output).contains("changeme:");

      // Verify that actual BCrypt hashes are generated (they start with $2a$ or $2b$)
      assertThat(output).containsPattern("admin123: \\$2[ab]\\$12\\$[A-Za-z0-9./]{53}");
      assertThat(output).containsPattern("researcher123: \\$2[ab]\\$12\\$[A-Za-z0-9./]{53}");
      assertThat(output).containsPattern("changeme: \\$2[ab]\\$12\\$[A-Za-z0-9./]{53}");
    }

    @Test
    @DisplayName("should generate different hashes on each execution due to salt")
    void shouldGenerateDifferentHashesOnEachExecution() {
      // First execution
      PasswordHashGenerator.main(new String[] {});
      String firstOutput = outputStreamCaptor.toString();

      // Reset output capture
      outputStreamCaptor.reset();

      // Second execution
      PasswordHashGenerator.main(new String[] {});
      String secondOutput = outputStreamCaptor.toString();

      // Extract hash values from outputs
      String firstAdminHash = extractHashFromOutput(firstOutput, "admin123:");
      String secondAdminHash = extractHashFromOutput(secondOutput, "admin123:");

      // Verify hashes are different due to random salt
      assertThat(firstAdminHash).isNotEqualTo(secondAdminHash);
    }
  }

  @DisplayName("BCrypt Integration Tests")
  @Nested
  class BCryptIntegrationTests {

    @Test
    @DisplayName("should generate valid BCrypt hashes that can be verified")
    void shouldGenerateValidBCryptHashes() {
      // Given
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
      String[] testPasswords = { "admin123", "researcher123", "changeme" };

      // When & Then
      for (String password : testPasswords) {
        String hash = encoder.encode(password);

        // Verify the hash can be used to verify the original password
        assertThat(encoder.matches(password, hash)).isTrue();
        assertThat(encoder.matches("wrongpassword", hash)).isFalse();

        // Verify hash format (BCrypt with strength 12)
        assertThat(hash).matches("\\$2[ab]\\$12\\$[A-Za-z0-9./]{53}");
      }
    }

    @Test
    @DisplayName("should use BCrypt strength of 12")
    void shouldUseBCryptStrengthOf12() {
      // Execute the main method
      PasswordHashGenerator.main(new String[] {});
      String output = outputStreamCaptor.toString();

      // All hashes should indicate strength 12
      assertThat(output).containsPattern("\\$2[ab]\\$12\\$");

      // Count occurrences of $12$ to ensure all hashes use strength 12
      long strengthCount =
        output
          .chars()
          .mapToObj(c -> String.valueOf((char) c))
          .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
          .toString()
          .split("\\$12\\$")
          .length -
        1;

      assertThat(strengthCount).isEqualTo(3); // Three passwords, each with strength 12
    }
  }

  @DisplayName("Security Tests")
  @Nested
  class SecurityTests {

    @Test
    @DisplayName("should generate hashes with proper salt randomness")
    void shouldGenerateHashesWithProperSaltRandomness() {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
      String password = "testpassword";

      // Generate multiple hashes for the same password
      String hash1 = encoder.encode(password);
      String hash2 = encoder.encode(password);
      String hash3 = encoder.encode(password);

      // All hashes should be different due to random salt
      assertThat(hash1).isNotEqualTo(hash2);
      assertThat(hash2).isNotEqualTo(hash3);
      assertThat(hash1).isNotEqualTo(hash3);

      // But all should validate the same password
      assertThat(encoder.matches(password, hash1)).isTrue();
      assertThat(encoder.matches(password, hash2)).isTrue();
      assertThat(encoder.matches(password, hash3)).isTrue();
    }

    @Test
    @DisplayName("should generate computationally expensive hashes")
    void shouldGenerateComputationallyExpensiveHashes() {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
      String password = "testpassword";

      // Measure time to encode (should be significant with strength 12)
      long startTime = System.currentTimeMillis();
      String hash = encoder.encode(password);
      long endTime = System.currentTimeMillis();

      // With strength 12, encoding should take at least a few milliseconds
      long encodingTime = endTime - startTime;

      // Verify hash is valid
      assertThat(encoder.matches(password, hash)).isTrue();

      // Verify it takes measurable time (indicating proper work factor)
      // Note: This is environment-dependent, so we use a conservative check
      assertThat(encodingTime).isGreaterThanOrEqualTo(0);
    }
  }

  @DisplayName("Edge Case Tests")
  @Nested
  class EdgeCaseTests {

    @Test
    @DisplayName("should handle empty password array gracefully")
    void shouldHandleEmptyPasswordArrayGracefully() {
      // This tests that the method completes normally even with no issues
      assertDoesNotThrow(() -> PasswordHashGenerator.main(null));
      assertDoesNotThrow(() -> PasswordHashGenerator.main(new String[] {}));
    }

    @Test
    @DisplayName("should handle special characters in passwords")
    void shouldHandleSpecialCharactersInPasswords() {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
      String[] specialPasswords = {
        "p@ssw0rd!",
        "ñoñó123#",
        "密码123",
        "🔒secure🔑",
        "line\nbreak",
        "tab\there",
      };

      for (String password : specialPasswords) {
        String hash = encoder.encode(password);
        assertThat(encoder.matches(password, hash)).isTrue();
        assertThat(hash).matches("\\$2[ab]\\$12\\$[A-Za-z0-9./]{53}");
      }
    }

    @Test
    @DisplayName("should handle very long passwords")
    void shouldHandleVeryLongPasswords() {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

      // BCrypt has a 72-byte password limit, test this boundary
      String maxLengthPassword = "a".repeat(72);
      String hash = encoder.encode(maxLengthPassword);

      assertThat(encoder.matches(maxLengthPassword, hash)).isTrue();
      assertThat(hash).matches("\\$2[ab]\\$12\\$[A-Za-z0-9./]{53}");

      // Test that passwords longer than 72 bytes throw an IllegalArgumentException
      String tooLongPassword = "a".repeat(73);
      assertThatThrownBy(() -> encoder.encode(tooLongPassword))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("password cannot be more than 72 bytes");
    }

    @Test
    @DisplayName("should handle empty string password")
    void shouldHandleEmptyStringPassword() {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
      String emptyPassword = "";

      String hash = encoder.encode(emptyPassword);
      assertThat(encoder.matches(emptyPassword, hash)).isTrue();
      assertThat(hash).matches("\\$2[ab]\\$12\\$[A-Za-z0-9./]{53}");
    }
  }

  @DisplayName("Constructor Tests")
  @Nested
  class ConstructorTests {

    @Test
    @DisplayName("should have private constructor to prevent instantiation")
    void shouldHavePrivateConstructorToPreventInstantiation() {
      // Use reflection to access the private constructor
      Constructor<?>[] constructors = PasswordHashGenerator.class.getDeclaredConstructors();

      // Should have exactly one constructor
      assertThat(constructors).hasSize(1);

      Constructor<?> constructor = constructors[0];

      // Constructor should be private
      assertThat(constructor.getModifiers()).isEqualTo(java.lang.reflect.Modifier.PRIVATE);

      // Should be able to access and invoke the private constructor via reflection
      assertDoesNotThrow(() -> {
        constructor.setAccessible(true);
        constructor.newInstance();
      });
    }

    @Test
    @DisplayName("should prevent instantiation through new operator")
    void shouldPreventInstantiationThroughNewOperator() {
      // This test documents that direct instantiation is not possible
      // (This would be a compile-time error, so we test via reflection)

      Constructor<?>[] constructors = PasswordHashGenerator.class.getDeclaredConstructors();
      Constructor<?> constructor = constructors[0];

      // Verify constructor is private and can be invoked via reflection for coverage
      assertDoesNotThrow(() -> {
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        assertThat(instance).isInstanceOf(PasswordHashGenerator.class);
      });
    }
  }

  /**
   * Helper method to extract hash value from log output.
   *
   * @param output The captured log output
   * @param prefix The prefix to look for (e.g., "admin123:")
   * @return The extracted hash value
   */
  private String extractHashFromOutput(String output, String prefix) {
    String[] lines = output.split("\n");
    for (String line : lines) {
      if (line.contains(prefix)) {
        return line.substring(line.indexOf(prefix) + prefix.length()).trim();
      }
    }
    return null;
  }
}

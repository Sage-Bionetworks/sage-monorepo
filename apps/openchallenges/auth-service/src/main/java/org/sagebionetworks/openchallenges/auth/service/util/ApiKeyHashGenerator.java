package org.sagebionetworks.openchallenges.auth.service.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class ApiKeyHashGenerator {

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private ApiKeyHashGenerator() {
    // Private constructor to prevent instantiation
  }

  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    if (args.length == 0) {
      // Generate hashes for default test API keys
      System.out.println("test-api-key-admin: " + encoder.encode("test-api-key-admin"));
      System.out.println("test-api-key-researcher: " + encoder.encode("test-api-key-researcher"));
      System.out.println("test-api-key-user: " + encoder.encode("test-api-key-user"));
      System.out.println("test-api-key-readonly: " + encoder.encode("test-api-key-readonly"));
      
      // Generate a few more for various testing scenarios
      System.out.println("jwt-test-key: " + encoder.encode("jwt-test-key"));
      System.out.println("gateway-test-key: " + encoder.encode("gateway-test-key"));
    } else {
      // Generate hashes for provided API keys
      for (String apiKey : args) {
        System.out.println(apiKey + ": " + encoder.encode(apiKey));
      }
    }
  }
}

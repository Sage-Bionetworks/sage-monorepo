package org.sagebionetworks.openchallenges.auth.service.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordHashGenerator {

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private PasswordHashGenerator() {
    // Private constructor to prevent instantiation
  }

  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    System.out.println("admin123: " + encoder.encode("admin123"));
    System.out.println("researcher123: " + encoder.encode("researcher123"));
    System.out.println("changeme: " + encoder.encode("changeme"));
  }
}

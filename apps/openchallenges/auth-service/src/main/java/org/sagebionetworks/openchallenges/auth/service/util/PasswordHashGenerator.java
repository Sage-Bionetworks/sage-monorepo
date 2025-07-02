package org.sagebionetworks.openchallenges.auth.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

  private static final Logger logger = LoggerFactory.getLogger(PasswordHashGenerator.class);

  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    logger.info("admin123: {}", encoder.encode("admin123"));
    logger.info("researcher123: {}", encoder.encode("researcher123"));
  }
}

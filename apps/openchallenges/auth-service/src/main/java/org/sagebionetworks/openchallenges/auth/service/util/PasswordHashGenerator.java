package org.sagebionetworks.openchallenges.auth.service.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("researcher123: " + encoder.encode("researcher123"));
    }
}

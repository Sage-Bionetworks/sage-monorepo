package org.sagebionetworks.openchallenges.auth.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple DTO for OAuth2 user information from external providers.
 * This is used internally by OAuth2Service and AuthenticationService
 * and should not be confused with any OpenAPI-generated DTOs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserInfo {
    private String sub;           // Subject identifier
    private String id;            // Alias for providerId/sub 
    private String providerId;    // External provider user ID
    private String email;
    private Boolean emailVerified;
    private String name;
    private String displayName;   // Display name for the user
    private String givenName;     // First name
    private String familyName;    // Last name
    private String picture;
    private String locale;
    private String username;      // For some providers like Synapse
}

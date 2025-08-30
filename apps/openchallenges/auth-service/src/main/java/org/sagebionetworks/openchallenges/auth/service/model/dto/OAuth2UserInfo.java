package org.sagebionetworks.openchallenges.auth.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OAuth2 user info response from providers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserInfo {

  @JsonProperty("id")
  private String id;

  @JsonProperty("sub") // OIDC standard claim
  private String sub;

  @JsonProperty("email")
  private String email;

  @JsonProperty("name")
  private String name;

  @JsonProperty("given_name")
  private String givenName;

  @JsonProperty("family_name")
  private String familyName;

  @JsonProperty("picture")
  private String picture;

  @JsonProperty("email_verified")
  private Boolean emailVerified;

  @JsonProperty("locale")
  private String locale;

  // Synapse-specific fields
  @JsonProperty("username")
  private String username;

  @JsonProperty("displayName")
  private String displayName;

  /**
   * Get the unique identifier for this user from the provider
   */
  public String getProviderId() {
    return sub != null ? sub : id;
  }

  /**
   * Get the display name from available fields
   */
  public String getDisplayName() {
    if (displayName != null) return displayName;
    if (name != null) return name;
    if (givenName != null && familyName != null) return givenName + " " + familyName;
    if (givenName != null) return givenName;
    if (email != null) return email;
    return username;
  }
}

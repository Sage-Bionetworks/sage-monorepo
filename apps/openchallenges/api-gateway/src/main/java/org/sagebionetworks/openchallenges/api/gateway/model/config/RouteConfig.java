package org.sagebionetworks.openchallenges.api.gateway.model.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the configuration for a specific route in the API Gateway.
 * Contains security requirements, audience information, and access control settings.
 */
public class RouteConfig {

  private List<String> scopes = new ArrayList<>();
  private String audience;
  private boolean anonymousAccess = false;

  public RouteConfig() {}

  public RouteConfig(List<String> scopes, String audience, boolean anonymousAccess) {
    this.scopes = scopes != null ? new ArrayList<>(scopes) : new ArrayList<>();
    this.audience = audience;
    this.anonymousAccess = anonymousAccess;
  }

  public List<String> getScopes() {
    return new ArrayList<>(scopes);
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes != null ? new ArrayList<>(scopes) : new ArrayList<>();
  }

  public String getAudience() {
    return audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  public boolean isAnonymousAccess() {
    return anonymousAccess;
  }

  public void setAnonymousAccess(boolean anonymousAccess) {
    this.anonymousAccess = anonymousAccess;
  }

  public boolean hasScopes() {
    return !scopes.isEmpty();
  }

  public boolean hasAudience() {
    return audience != null && !audience.trim().isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RouteConfig that = (RouteConfig) o;
    return (
      anonymousAccess == that.anonymousAccess &&
      Objects.equals(scopes, that.scopes) &&
      Objects.equals(audience, that.audience)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(scopes, audience, anonymousAccess);
  }

  @Override
  public String toString() {
    return (
      "RouteConfig{" +
      "scopes=" +
      scopes +
      ", audience='" +
      audience +
      '\'' +
      ", anonymousAccess=" +
      anonymousAccess +
      '}'
    );
  }
}

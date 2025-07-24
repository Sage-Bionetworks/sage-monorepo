package org.sagebionetworks.openchallenges.organization.service.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom user principal containing authenticated user information from the auth service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser implements UserDetails {

  private UUID userId;
  private String username;
  private String role;
  private List<String> scopes;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (scopes == null) {
      return List.of();
    }
    return scopes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  @Override
  public String getPassword() {
    return null; // Not needed for API key authentication
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  /**
   * Check if the user has a specific scope
   */
  public boolean hasScope(String scope) {
    return scopes != null && scopes.contains(scope);
  }

  /**
   * Check if the user has admin role
   */
  public boolean isAdmin() {
    return "admin".equalsIgnoreCase(role);
  }
}

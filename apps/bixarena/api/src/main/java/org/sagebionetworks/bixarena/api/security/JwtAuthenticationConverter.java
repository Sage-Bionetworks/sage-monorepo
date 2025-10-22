package org.sagebionetworks.bixarena.api.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Converts JWT tokens into Spring Security authentication tokens.
 * Extracts roles from the "roles" claim in the JWT and converts them to
 * Spring Security authorities with the "ROLE_" prefix.
 */
@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
    return new JwtAuthenticationToken(jwt, authorities);
  }

  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    // Extract roles from JWT claims
    List<String> roles = jwt.getClaimAsStringList("roles");
    if (roles == null || roles.isEmpty()) {
      return List.of();
    }

    // Convert roles to Spring Security authorities with ROLE_ prefix
    return roles
      .stream()
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
      .collect(Collectors.toList());
  }
}

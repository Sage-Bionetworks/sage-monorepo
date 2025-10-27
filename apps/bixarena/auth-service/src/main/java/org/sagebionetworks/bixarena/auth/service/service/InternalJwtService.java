package org.sagebionetworks.bixarena.auth.service.service;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.auth.service.configuration.AppProperties;
import org.sagebionetworks.bixarena.auth.service.security.key.JwkKeyStore;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InternalJwtService {

  private final JwkKeyStore keyStore;
  private final AppProperties appProperties;

  public record Minted(String token, Instant expiresAt) {}

  /**
   * Mints a JWT with the specified subject, roles, and audience.
   *
   * @param sub BixArena user ID (from auth.user.id)
   * @param roles User roles (from auth.user.role)
   * @param audience Target audience for the JWT (e.g., "urn:bixarena:api")
   * @return Minted JWT token and expiration time
   */
  public Minted mint(String sub, List<String> roles, String audience) {
    try {
      var key = keyStore.current();
      Instant now = Instant.now();
      Instant exp = now.plusSeconds(appProperties.auth().tokenTtlSeconds());
      JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .issuer(appProperties.auth().internalIssuer())
        .audience(audience != null ? audience : appProperties.auth().audience())
        .subject(sub)
        .claim("roles", roles)
        .issueTime(Date.from(now))
        .notBeforeTime(Date.from(now))
        .expirationTime(Date.from(exp))
        .build();
      JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
        .keyID(key.getKeyID())
        .type(JOSEObjectType.JWT)
        .build();
      SignedJWT jwt = new SignedJWT(header, claims);
      jwt.sign(new RSASSASigner(key));
      return new Minted(jwt.serialize(), exp);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to mint token", e);
    }
  }
}

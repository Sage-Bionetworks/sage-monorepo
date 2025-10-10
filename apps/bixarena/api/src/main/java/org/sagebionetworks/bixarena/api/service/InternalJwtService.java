package org.sagebionetworks.bixarena.api.service;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.springframework.stereotype.Service;

@Service
public class InternalJwtService {

  private final JwkKeyStore keyStore;
  private final AppProperties.Auth authProps;

  public record Minted(String token, Instant expiresAt) {}

  public InternalJwtService(JwkKeyStore keyStore, AppProperties appProperties) {
    this.keyStore = keyStore;
    this.authProps = appProperties.auth();
  }

  public Minted mint(String sub, List<String> roles) {
    try {
      var key = keyStore.current();
      Instant now = Instant.now();
      Instant exp = now.plusSeconds(authProps.tokenTtlSeconds());
      JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .issuer(authProps.internalIssuer())
        .audience(authProps.audience())
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

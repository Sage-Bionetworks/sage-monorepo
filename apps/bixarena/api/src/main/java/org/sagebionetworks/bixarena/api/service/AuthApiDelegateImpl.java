package org.sagebionetworks.bixarena.api.service;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.api.AuthApiDelegate;
import org.sagebionetworks.bixarena.api.model.dto.GetJwks200ResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.MintInternalToken200ResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.OidcCallback200ResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthApiDelegateImpl implements AuthApiDelegate {

  private final JwkKeyStore keyStore;
  private final InternalJwtService jwtService;

  public AuthApiDelegateImpl(JwkKeyStore keyStore, InternalJwtService jwtService) {
    this.keyStore = keyStore;
    this.jwtService = jwtService;
  }

  @Override
  public ResponseEntity<GetJwks200ResponseDto> getJwks() {
  JWKSet jwkSet = keyStore.publicJwkSet();
  var keys = jwkSet.getKeys().stream().map(JWK::toJSONObject).collect(Collectors.toList());
  // builder().keys expects List<Object>
  var body = GetJwks200ResponseDto.builder().keys((java.util.List<Object>)(java.util.List<?>)keys).build();
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @Override
  public ResponseEntity<MintInternalToken200ResponseDto> mintInternalToken() {
    // TODO: Replace with session user resolution; using placeholder subject for now
    String subject = "demo-user"; // placeholder until OIDC session implemented
    var minted = jwtService.mint(subject, java.util.List.of("user"));
    long expiresIn = Duration.between(java.time.Instant.now(), minted.expiresAt()).getSeconds();
    var body = MintInternalToken200ResponseDto
      .builder()
      .accessToken(minted.token())
      .tokenType(MintInternalToken200ResponseDto.TokenTypeEnum.BEARER)
      .expiresIn((int) expiresIn)
      .build();
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @Override
  public ResponseEntity<Void> startOidc() {
    // TODO: Implement state + nonce generation and redirect to Synapse authorize URL
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @Override
  public ResponseEntity<OidcCallback200ResponseDto> oidcCallback(String code, String state) {
    // TODO: Validate state, exchange code for tokens, verify ID token, establish session
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}

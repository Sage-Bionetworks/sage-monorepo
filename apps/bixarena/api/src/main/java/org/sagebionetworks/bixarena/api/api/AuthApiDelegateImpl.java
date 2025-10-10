package org.sagebionetworks.bixarena.api.api;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.sagebionetworks.bixarena.api.model.dto.GetJwks200ResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.MintInternalToken200ResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.OidcCallback200ResponseDto;
import org.sagebionetworks.bixarena.api.service.InternalJwtService;
import org.sagebionetworks.bixarena.api.service.JwkKeyStore;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthApiDelegateImpl implements AuthApiDelegate {

  private final JwkKeyStore keyStore;
  private final InternalJwtService jwtService;
  private final AppProperties.Auth authProps;

  public AuthApiDelegateImpl(JwkKeyStore keyStore, InternalJwtService jwtService, AppProperties appProperties) {
    this.keyStore = keyStore;
    this.jwtService = jwtService;
    this.authProps = appProperties.auth();
  }

  @Override
  public ResponseEntity<GetJwks200ResponseDto> getJwks() {
    JWKSet jwkSet = keyStore.publicJwkSet();
    List<Object> keys = jwkSet.getKeys().stream().map(JWK::toJSONObject).collect(Collectors.toList());
    var body = GetJwks200ResponseDto.builder().keys(keys).build();
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @Override
  public ResponseEntity<MintInternalToken200ResponseDto> mintInternalToken() {
    // TODO: Resolve subject + roles from authenticated session instead of placeholder.
    String subject = "demo-user"; // placeholder
    var minted = jwtService.mint(subject, List.of("user"));
    long expiresIn = Duration.between(Instant.now(), minted.expiresAt()).getSeconds();
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
    // Generate state & nonce (TODO: persist securely in session)
    String state = java.util.UUID.randomUUID().toString();
    String nonce = java.util.UUID.randomUUID().toString();
    HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    HttpSession session = req.getSession(true);
    session.setAttribute("OIDC_STATE", state);
    session.setAttribute("OIDC_NONCE", nonce);

    String redirect = authProps.authorizeUrl()
      + "?response_type=code"
      + "&client_id=" + url(authProps.clientId())
  + "&redirect_uri=" + url(authProps.redirectUri().toString())
      + "&scope=" + url("openid profile email")
      + "&state=" + url(state)
      + "&nonce=" + url(nonce);

    return ResponseEntity.status(302).header("Location", redirect).build();
  }

  @Override
  public ResponseEntity<OidcCallback200ResponseDto> oidcCallback(String code, String state) {
    // TODO: Validate state matches session, exchange code for tokens, validate ID token nonce, establish session principal.
    // Placeholder response
    return ResponseEntity.status(501).build();
  }

  private static String url(String v) {
    return URLEncoder.encode(v, StandardCharsets.UTF_8);
  }
}

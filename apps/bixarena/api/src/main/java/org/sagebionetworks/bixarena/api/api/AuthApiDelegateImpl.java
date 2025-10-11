package org.sagebionetworks.bixarena.api.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.sagebionetworks.bixarena.api.model.dto.GetJwks200ResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.MintInternalToken200ResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.OidcCallback200ResponseDto;
import org.sagebionetworks.bixarena.api.model.entity.ExternalAccountEntity;
import org.sagebionetworks.bixarena.api.model.entity.ExternalAccountEntity.Provider;
import org.sagebionetworks.bixarena.api.model.entity.UserEntity;
import org.sagebionetworks.bixarena.api.model.repository.ExternalAccountRepository;
import org.sagebionetworks.bixarena.api.model.repository.UserRepository;
import org.sagebionetworks.bixarena.api.service.InternalJwtService;
import org.sagebionetworks.bixarena.api.service.JwkKeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuthApiDelegateImpl implements AuthApiDelegate {

  private static final Logger log = LoggerFactory.getLogger(AuthApiDelegateImpl.class);

  private final JwkKeyStore keyStore;
  private final InternalJwtService jwtService;
  private final AppProperties.Auth authProps;
  private final AppProperties appProperties;
  private final UserRepository userRepository;
  private final ExternalAccountRepository externalAccountRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public AuthApiDelegateImpl(
    JwkKeyStore keyStore,
    InternalJwtService jwtService,
    AppProperties appProperties,
    UserRepository userRepository,
    ExternalAccountRepository externalAccountRepository
  ) {
    this.keyStore = keyStore;
    this.jwtService = jwtService;
    this.authProps = appProperties.auth();
    this.appProperties = appProperties;
    this.userRepository = userRepository;
    this.externalAccountRepository = externalAccountRepository;
  }

  @Override
  public ResponseEntity<GetJwks200ResponseDto> getJwks() {
    JWKSet jwkSet = keyStore.publicJwkSet();
    List<Object> keys = jwkSet
      .getKeys()
      .stream()
      .map(JWK::toJSONObject)
      .collect(Collectors.toList());
    var body = GetJwks200ResponseDto.builder().keys(keys).build();
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @Override
  public ResponseEntity<MintInternalToken200ResponseDto> mintInternalToken() {
    HttpServletRequest req =
      ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    HttpSession session = req.getSession(false);
    if (session == null) {
      return ResponseEntity.status(401).build();
    }
    String subject = (String) session.getAttribute("AUTH_SUBJECT");
    @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) session.getAttribute("AUTH_ROLES");
    if (subject == null || roles == null) {
      return ResponseEntity.status(401).build();
    }
    var minted = jwtService.mint(subject, roles);
    long expiresIn = Duration.between(Instant.now(), minted.expiresAt()).getSeconds();
    var body = MintInternalToken200ResponseDto.builder()
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
    HttpServletRequest req =
      ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    HttpSession session = req.getSession(true);
    session.setAttribute("OIDC_STATE", state);
    session.setAttribute("OIDC_NONCE", nonce);
    if (log.isInfoEnabled()) {
      log.info(
        "OIDC start: sessionId={} assigned state={} nonce={} host={} port={}",
        session.getId(),
        state,
        nonce,
        req.getServerName(),
        req.getServerPort()
      );
    }

    String redirect =
      authProps.authorizeUrl() +
      "?response_type=code" +
      "&client_id=" +
      url(authProps.clientId()) +
      "&redirect_uri=" +
      url(authProps.redirectUri().toString()) +
      "&scope=" +
      url("openid profile email") +
      "&state=" +
      url(state) +
      "&nonce=" +
      url(nonce);

    return ResponseEntity.status(302).header("Location", redirect).build();
  }

  @Override
  public ResponseEntity<OidcCallback200ResponseDto> oidcCallback(String code, String state) {
    HttpServletRequest req =
      ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    HttpSession session = req.getSession(false);
    if (session == null) {
      log.info(
        "OIDC callback: no session (cookie missing). host={} port={} code={} state={}",
        req.getServerName(),
        req.getServerPort(),
        code,
        state
      );
      return ResponseEntity.status(400)
        .contentType(MediaType.APPLICATION_JSON)
        .body(OidcCallback200ResponseDto.builder().status("error:no-session").build());
    }
    if (log.isInfoEnabled()) {
      log.info(
        "OIDC callback: sessionId={} received state={} code={} expectedState={} host={} port={}",
        session.getId(),
        state,
        code,
        session.getAttribute("OIDC_STATE"),
        req.getServerName(),
        req.getServerPort()
      );
    }
    String expectedState = (String) session.getAttribute("OIDC_STATE");
    if (expectedState == null || !expectedState.equals(state)) {
      log.info("OIDC callback: state mismatch expected={} received={}", expectedState, state);
      return ResponseEntity.status(400)
        .contentType(MediaType.APPLICATION_JSON)
        .body(OidcCallback200ResponseDto.builder().status("error:state-mismatch").build());
    }
    try {
      // Exchange code for tokens at Synapse
      var tokenResponse = exchangeCodeForTokens(code);
      if (tokenResponse == null) {
        log.info("OIDC callback: token exchange returned null");
        return ResponseEntity.status(400)
          .contentType(MediaType.APPLICATION_JSON)
          .body(OidcCallback200ResponseDto.builder().status("error:token-exchange").build());
      }
      String idToken = (String) tokenResponse.get("id_token");
      if (idToken == null) {
        log.info("OIDC callback: missing id_token in response keys={}", tokenResponse.keySet());
        return ResponseEntity.status(400)
          .contentType(MediaType.APPLICATION_JSON)
          .body(OidcCallback200ResponseDto.builder().status("error:missing-id-token").build());
      }
      Map<String, Object> idClaims = decodeJwt(idToken);
      String sub = (String) idClaims.get("sub");
      if (sub == null) {
        log.info("OIDC callback: missing sub claim in id token");
        return ResponseEntity.status(400)
          .contentType(MediaType.APPLICATION_JSON)
          .body(OidcCallback200ResponseDto.builder().status("error:missing-sub").build());
      }
      String email = (String) idClaims.get("email");
      String givenName = (String) idClaims.getOrDefault("given_name", null);
      String familyName = (String) idClaims.getOrDefault("family_name", null);
      String preferred = (String) idClaims.getOrDefault("preferred_username", sub);
      // Upsert user
      UserEntity existingOrNew = userRepository
        .findByUsername(preferred)
        .orElseGet(() ->
          UserEntity.builder()
            .username(preferred)
            .email(email)
            .firstName(givenName)
            .lastName(familyName)
            .build()
        );
      UserEntity persistedUser = existingOrNew.getId() == null
        ? userRepository.save(existingOrNew)
        : existingOrNew;
      // Link external account
      externalAccountRepository
        .findByProviderAndExternalId(Provider.synapse, sub)
        .orElseGet(() ->
          externalAccountRepository.save(
            ExternalAccountEntity.builder()
              .user(persistedUser)
              .provider(Provider.synapse)
              .externalId(sub)
              .externalUsername(preferred)
              .externalEmail(email)
              .build()
          )
        );
      // Establish authenticated session principal
      session.setAttribute("AUTH_SUBJECT", persistedUser.getUsername());
      session.setAttribute("AUTH_ROLES", List.of(persistedUser.getRole().name()));
      session.removeAttribute("OIDC_STATE");
      session.removeAttribute("OIDC_NONCE");
      // If browser navigation (prefers HTML) redirect to root instead of showing JSON
      String accept = req.getHeader("Accept");
      if (accept != null && accept.contains("text/html")) {
        String uiBase = appProperties.uiBaseUrl();
        return ResponseEntity.status(302).header("Location", uiBase).build();
      }
      var body = OidcCallback200ResponseDto.builder().status("ok").build();
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
    } catch (Exception e) {
      log.info("OIDC callback: exception {}", e.getMessage());
      return ResponseEntity.status(400)
        .contentType(MediaType.APPLICATION_JSON)
        .body(OidcCallback200ResponseDto.builder().status("error:exception").build());
    }
  }

  private Map<String, Object> exchangeCodeForTokens(String code) {
    try {
      String basic = Base64.getEncoder()
        .encodeToString(
          (authProps.clientId() + ":" + authProps.clientSecret()).getBytes(StandardCharsets.UTF_8)
        );
      java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
      String form =
        "grant_type=authorization_code" +
        "&redirect_uri=" +
        url(authProps.redirectUri().toString()) +
        "&code=" +
        url(code);
      java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder(
        java.net.URI.create(authProps.tokenUrl())
      )
        .header("Authorization", "Basic " + basic)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(form))
        .build();
      java.net.http.HttpResponse<String> response = client.send(
        request,
        java.net.http.HttpResponse.BodyHandlers.ofString()
      );
      if (response.statusCode() != 200) {
        log.debug(
          "OIDC token exchange non-200 status={} body={}",
          response.statusCode(),
          response.body()
        );
        return null;
      }
      @SuppressWarnings("unchecked")
      Map<String, Object> parsed = objectMapper.readValue(response.body(), Map.class);
      return parsed;
    } catch (Exception e) {
      log.debug("OIDC token exchange exception {}", e.getMessage());
      return null;
    }
  }

  private Map<String, Object> decodeJwt(String jwt) throws Exception {
    String[] parts = jwt.split("\\.");
    if (parts.length != 3) throw new IllegalArgumentException("Invalid JWT");
    String payloadJson = new String(
      Base64.getUrlDecoder().decode(parts[1]),
      StandardCharsets.UTF_8
    );
    @SuppressWarnings("unchecked")
    Map<String, Object> parsed = objectMapper.readValue(payloadJson, Map.class);
    return parsed;
  }

  private static String url(String v) {
    return URLEncoder.encode(v, StandardCharsets.UTF_8);
  }

  @Override
  public ResponseEntity<Void> logout() {
    HttpServletRequest req =
      ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    HttpSession session = req.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    return ResponseEntity.noContent().build();
  }
}

package org.sagebionetworks.bixarena.auth.service.api;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.auth.service.configuration.AppProperties;
import org.sagebionetworks.bixarena.auth.service.model.dto.Callback200ResponseDto;
import org.sagebionetworks.bixarena.auth.service.model.dto.GetJwks200ResponseDto;
import org.sagebionetworks.bixarena.auth.service.model.dto.Token200ResponseDto;
import org.sagebionetworks.bixarena.auth.service.model.dto.UserInfoDto;
import org.sagebionetworks.bixarena.auth.service.model.entity.ExternalAccountEntity.Provider;
import org.sagebionetworks.bixarena.auth.service.model.entity.UserEntity;
import org.sagebionetworks.bixarena.auth.service.security.key.JwkKeyStore;
import org.sagebionetworks.bixarena.auth.service.service.InternalJwtService;
import org.sagebionetworks.bixarena.auth.service.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthApiDelegateImpl implements AuthApiDelegate {

  private final JwkKeyStore keyStore;
  private final InternalJwtService jwtService;
  private final AppProperties appProperties;
  private final UserService userService;
  private final ObjectMapper objectMapper = new ObjectMapper();

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
  public ResponseEntity<Token200ResponseDto> token() {
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
    var body = Token200ResponseDto.builder()
      .accessToken(minted.token())
      .tokenType(Token200ResponseDto.TokenTypeEnum.BEARER)
      .expiresIn((int) expiresIn)
      .build();
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @Override
  public ResponseEntity<UserInfoDto> getUserInfo() {
    HttpServletRequest req =
      ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    HttpSession session = req.getSession(false);

    if (session == null) {
      return ResponseEntity.status(401).build();
    }

    String subject = (String) session.getAttribute("AUTH_SUBJECT");
    String email = (String) session.getAttribute("AUTH_EMAIL");
    String preferredUsername = (String) session.getAttribute("AUTH_PREFERRED_USERNAME");
    Boolean emailVerified = (Boolean) session.getAttribute("AUTH_EMAIL_VERIFIED");
    @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) session.getAttribute("AUTH_ROLES");

    if (subject == null) {
      return ResponseEntity.status(401).build();
    }

    // Convert string roles to enum
    List<UserInfoDto.RolesEnum> roleEnums = (roles != null && !roles.isEmpty())
      ? roles
        .stream()
        .map(r -> {
          try {
            return UserInfoDto.RolesEnum.fromValue(r.toLowerCase());
          } catch (IllegalArgumentException e) {
            return UserInfoDto.RolesEnum.USER;
          }
        })
        .collect(Collectors.toList())
      : List.of(UserInfoDto.RolesEnum.USER);

    var body = UserInfoDto.builder()
      .sub(subject)
      .email(email)
      .preferredUsername(preferredUsername != null ? preferredUsername : subject)
      .emailVerified(emailVerified != null ? emailVerified : false)
      .roles(roleEnums)
      .build();

    return ResponseEntity.ok()
      .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
      .header("Pragma", "no-cache")
      .contentType(MediaType.APPLICATION_JSON)
      .body(body);
  }

  @Override
  public ResponseEntity<Void> login() {
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
      appProperties.auth().authorizeUrl() +
      "?response_type=code" +
      "&client_id=" +
      url(appProperties.auth().clientId()) +
      "&redirect_uri=" +
      url(appProperties.auth().redirectUri().toString()) +
      "&scope=" +
      url("openid profile email") +
      "&state=" +
      url(state) +
      "&nonce=" +
      url(nonce);

    return ResponseEntity.status(302).header("Location", redirect).build();
  }

  @Override
  public ResponseEntity<Callback200ResponseDto> callback(String code, String state) {
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
        .body(Callback200ResponseDto.builder().status("error:no-session").build());
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
        .body(Callback200ResponseDto.builder().status("error:state-mismatch").build());
    }
    try {
      // Exchange code for tokens at Synapse
      var tokenResponse = exchangeCodeForTokens(code);
      if (tokenResponse == null) {
        log.info("OIDC callback: token exchange returned null");
        return ResponseEntity.status(400)
          .contentType(MediaType.APPLICATION_JSON)
          .body(Callback200ResponseDto.builder().status("error:token-exchange").build());
      }
      String idToken = (String) tokenResponse.get("id_token");
      if (idToken == null) {
        log.info("OIDC callback: missing id_token in response keys={}", tokenResponse.keySet());
        return ResponseEntity.status(400)
          .contentType(MediaType.APPLICATION_JSON)
          .body(Callback200ResponseDto.builder().status("error:missing-id-token").build());
      }

      Map<String, Object> idClaims = decodeJwt(idToken);

      // Log all claims to see what's available
      log.info("OIDC callback: All ID token claims: {}", idClaims.keySet());
      log.debug("OIDC callback: Full ID token claims: {}", idClaims);

      String sub = (String) idClaims.get("sub");
      if (sub == null) {
        log.info("OIDC callback: missing sub claim in id token");
        return ResponseEntity.status(400)
          .contentType(MediaType.APPLICATION_JSON)
          .body(Callback200ResponseDto.builder().status("error:missing-sub").build());
      }

      String email = (String) idClaims.get("email");
      String givenName = (String) idClaims.getOrDefault("given_name", null);
      String familyName = (String) idClaims.getOrDefault("family_name", null);
      String preferredUsername = (String) idClaims.getOrDefault("preferred_username", sub);
      String userName = (String) idClaims.getOrDefault("user_name", null);

      // Use user_name claim if available (this is the actual Synapse username)
      // Otherwise fall back to preferred_username
      String synapseUsername = userName != null ? userName : preferredUsername;

      log.info(
        "OIDC callback: ID token claims - sub={}, user_name={}, preferred_username={}, email={}, given_name={}, family_name={}",
        sub,
        userName,
        preferredUsername,
        email,
        givenName,
        familyName
      );

      log.info("OIDC callback: Using username={} for user creation/update", synapseUsername);

      // Use UserService to handle user creation/update and login tracking
      UserEntity persistedUser = userService.handleUserLogin(
        Provider.synapse,
        sub,
        synapseUsername,
        email,
        givenName,
        familyName
      );

      // Establish authenticated session principal
      session.setAttribute("AUTH_SUBJECT", persistedUser.getUsername());
      session.setAttribute("AUTH_PREFERRED_USERNAME", persistedUser.getUsername());
      session.setAttribute("AUTH_EMAIL", persistedUser.getEmail());
      session.setAttribute("AUTH_ROLES", List.of(persistedUser.getRole().name()));
      session.removeAttribute("OIDC_STATE");
      session.removeAttribute("OIDC_NONCE");
      // If browser navigation (prefers HTML) redirect to root instead of showing JSON
      String accept = req.getHeader("Accept");
      if (accept != null && accept.contains("text/html")) {
        String uiBase = appProperties.uiBaseUrl();
        return ResponseEntity.status(302).header("Location", uiBase).build();
      }
      var body = Callback200ResponseDto.builder().status("ok").build();
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
    } catch (Exception e) {
      log.info("OIDC callback: exception {}", e.getMessage());
      return ResponseEntity.status(400)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Callback200ResponseDto.builder().status("error:exception").build());
    }
  }

  private Map<String, Object> exchangeCodeForTokens(String code) {
    try {
      String basic = Base64.getEncoder()
        .encodeToString(
          (appProperties.auth().clientId() + ":" + appProperties.auth().clientSecret()).getBytes(
              StandardCharsets.UTF_8
            )
        );
      java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
      String form =
        "grant_type=authorization_code" +
        "&redirect_uri=" +
        url(appProperties.auth().redirectUri().toString()) +
        "&code=" +
        url(code);
      java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder(
        java.net.URI.create(appProperties.auth().tokenUrl())
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
    if (log.isDebugEnabled()) {
      log.debug(
        "Logout request: raw Cookie header='{}' secure={} remoteAddr={} ua='{}'",
        req.getHeader("Cookie"),
        req.isSecure(),
        req.getRemoteAddr(),
        req.getHeader("User-Agent")
      );
    }
    if (session != null) {
      if (log.isInfoEnabled()) {
        log.info("Logout: invalidating sessionId={}", session.getId());
      }
      session.invalidate();
    }
    // Clear SecurityContext (defensive)
    org.springframework.security.core.context.SecurityContextHolder.clearContext();
    // Re-issue cookie deletion mirroring attributes (Secure only if original would have been secure)
    boolean secure = req.isSecure();
    StringBuilder cookie = new StringBuilder(
      "JSESSIONID=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/; HttpOnly; SameSite=Lax"
    );
    if (secure) cookie.append("; Secure");
    // Optional: add domain if explicitly configured (else rely on host-only cookie)
    // String domain = req.getServerName(); // only if you earlier set Domain attribute
    if (log.isDebugEnabled()) {
      log.debug(
        "Logout: issuing clearing cookie attrs secure={} path=/ sameSite=Lax host={}",
        secure,
        req.getServerName()
      );
      // Provide a short hash of clearing cookie for correlation
      log.debug(
        "Logout: clearing Set-Cookie hash={} value='{}'",
        Integer.toHexString(cookie.toString().hashCode()),
        cookie
      );
    }
    return ResponseEntity.noContent()
      .header("Set-Cookie", cookie.toString())
      .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
      .header("Pragma", "no-cache")
      .build();
  }
}

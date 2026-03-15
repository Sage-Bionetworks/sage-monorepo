package org.sagebionetworks.bixarena.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.springframework.stereotype.Component;

/**
 * Manages service-to-service authentication tokens for calling the AI service.
 *
 * <p>Obtains an OAuth2 service token from the auth service using client credentials
 * and caches it in memory until near expiration.
 */
@Component
@Slf4j
public class ServiceTokenProvider {

  private static final long TOKEN_REFRESH_MARGIN_SECONDS = 30;

  private final AppProperties appProperties;
  private final ObjectMapper objectMapper;

  private final HttpClient httpClient = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_1_1)
    .build();

  private volatile String cachedToken;
  private volatile Instant tokenExpiresAt = Instant.EPOCH;

  private record ServiceTokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") int expiresIn
  ) {}

  public ServiceTokenProvider(AppProperties appProperties, ObjectMapper objectMapper) {
    this.appProperties = appProperties;
    this.objectMapper = objectMapper;
  }

  public synchronized String obtainServiceToken() throws Exception {
    if (cachedToken != null && Instant.now().isBefore(tokenExpiresAt)) {
      return cachedToken;
    }

    String clientId = appProperties.authService().serviceClientId();
    String clientSecret = appProperties.authService().serviceClientSecret();
    String credentials = clientId + ":" + clientSecret;
    String encoded = Base64.getEncoder()
      .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

    String url = appProperties.authService().baseUrl()
      + "/oauth2/service-token?audience=urn:bixarena:ai";

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
      .header("Authorization", "Basic " + encoded)
      .POST(HttpRequest.BodyPublishers.noBody())
      .build();

    HttpResponse<String> httpResponse = httpClient.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (httpResponse.statusCode() != 200) {
      throw new IllegalStateException(
        "Auth service returned " + httpResponse.statusCode() + ": " + httpResponse.body()
      );
    }

    ServiceTokenResponse response = objectMapper.readValue(
      httpResponse.body(),
      ServiceTokenResponse.class
    );

    cachedToken = response.accessToken();
    tokenExpiresAt = Instant.now()
      .plusSeconds(response.expiresIn() - TOKEN_REFRESH_MARGIN_SECONDS);
    log.debug("Cached service token, expires at {}", tokenExpiresAt);

    return cachedToken;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }
}

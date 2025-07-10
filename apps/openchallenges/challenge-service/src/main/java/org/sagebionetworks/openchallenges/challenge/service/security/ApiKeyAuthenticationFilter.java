package org.sagebionetworks.openchallenges.challenge.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.sagebionetworks.openchallenges.challenge.service.client.AuthServiceClient;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.auth.ValidateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.auth.ValidateApiKeyResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to authenticate requests using API keys via the auth service
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthServiceClient authServiceClient;
  private final ObjectMapper objectMapper;

  public ApiKeyAuthenticationFilter(
    AuthServiceClient authServiceClient,
    ObjectMapper objectMapper
  ) {
    this.authServiceClient = authServiceClient;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String authHeader = request.getHeader(AUTHORIZATION_HEADER);

    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      String apiKey = authHeader.substring(BEARER_PREFIX.length());

      try {
        // Validate API key with auth service
        ValidateApiKeyRequestDto validateRequest = new ValidateApiKeyRequestDto(apiKey);
        ValidateApiKeyResponseDto validateResponse = authServiceClient.validateApiKey(
          validateRequest
        );

        if (validateResponse.getValid() != null && validateResponse.getValid()) {
          // Create authenticated user
          AuthenticatedUser user = new AuthenticatedUser(
            validateResponse.getUserId(),
            validateResponse.getUsername(),
            validateResponse.getRole(),
            validateResponse.getScopes()
          );

          // Set authentication in security context
          UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authentication);

          logger.debug(
            "Successfully authenticated user: {} with scopes: {}",
            user.getUsername(),
            user.getScopes()
          );
        } else {
          logger.warn("API key validation failed: invalid key");
          sendUnauthorizedResponse(response, "Invalid API key");
          return;
        }
      } catch (FeignException.Unauthorized e) {
        logger.warn("API key validation failed: unauthorized");
        sendUnauthorizedResponse(response, "Invalid API key");
        return;
      } catch (FeignException e) {
        logger.error("Auth service communication failed: {}", e.getMessage());
        sendServiceUnavailableResponse(response, "Authentication service unavailable");
        return;
      } catch (Exception e) {
        logger.error("Unexpected error during API key validation", e);
        sendServiceUnavailableResponse(response, "Authentication service error");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, String message)
    throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", "Unauthorized");
    errorResponse.put("message", message);
    errorResponse.put("status", 401);

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }

  private void sendServiceUnavailableResponse(HttpServletResponse response, String message)
    throws IOException {
    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    response.setContentType("application/json");

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", "Service Unavailable");
    errorResponse.put("message", message);
    errorResponse.put("status", 503);

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}

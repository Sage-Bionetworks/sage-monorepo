package org.sagebionetworks.openchallenges.auth.service.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.dto.*;
import org.sagebionetworks.openchallenges.auth.service.model.entity.*;
import org.sagebionetworks.openchallenges.auth.service.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationService {

  private final UserRepository userRepository;
  private final ExternalAccountRepository externalAccountRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final OAuth2ConfigurationService oAuth2ConfigurationService;
  private final OAuth2Service oAuth2Service;

  /**
   * Authenticate user with username/password and generate JWT tokens
   */
  public LoginResponseDto authenticateUser(String username, String password) {
    log.debug("Authenticating user: {}", username);

    Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(username);
    if (userOpt.isEmpty()) {
      log.debug("User not found: {}", username);
      throw new RuntimeException("Invalid credentials");
    }

    User user = userOpt.get();
    if (!user.isActive()) {
      log.debug("User account is disabled: {}", username);
      throw new RuntimeException("Account is disabled");
    }

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      log.debug("Invalid password for user: {}", username);
      throw new RuntimeException("Invalid credentials");
    }

    log.info("User successfully authenticated: {}", username);
    return generateTokenResponse(user);
  }

  /**
   * Generate OAuth2 authorization URL for provider
   */
  public OAuth2AuthorizeResponseDto authorizeOAuth2(OAuth2AuthorizeRequestDto request) {
    log.debug("Generating OAuth2 authorization URL for provider: {}", request.getProvider());

    ExternalAccount.Provider provider;
    try {
      provider = ExternalAccount.Provider.valueOf(request.getProvider().getValue());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Unsupported OAuth2 provider: " + request.getProvider());
    }

    // Generate state for CSRF protection with provider information
    String baseState = request.getState() != null ? request.getState() : UUID.randomUUID().toString();
    String state = provider.name() + ":" + baseState; // Encode provider in state

    // Get authorization URL from OAuth2 configuration service  
    String authorizationUrl = oAuth2ConfigurationService.getAuthorizationUrl(provider, state);

    try {
      return OAuth2AuthorizeResponseDto.builder()
        .authorizationUrl(new java.net.URI(authorizationUrl))
        .state(state)
        .build();
    } catch (java.net.URISyntaxException e) {
      throw new RuntimeException("Invalid authorization URL", e);
    }
  }

  /**
   * Handle OAuth2 callback and generate JWT tokens (extract provider from state)
   */
  public LoginResponseDto handleOAuth2Callback(String code, String state) {
    log.debug("Handling OAuth2 callback with state: {}", state);

    // Extract provider from state (format: "provider:originalState")
    String provider;
    if (state != null && state.contains(":")) {
      provider = state.split(":", 2)[0];
    } else {
      throw new RuntimeException("Invalid state parameter - provider information missing");
    }

    return handleOAuth2Callback(provider, code, state);
  }

  /**
   * Handle OAuth2 callback and generate JWT tokens
   */
  public LoginResponseDto handleOAuth2Callback(String provider, String code, String state) {
    log.debug("Handling OAuth2 callback for provider: {}", provider);

    ExternalAccount.Provider oauthProvider;
    try {
      oauthProvider = ExternalAccount.Provider.valueOf(provider.toLowerCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Unsupported OAuth2 provider: " + provider);
    }

    // 1. Generate redirect URI for this provider
    String redirectUri = oAuth2ConfigurationService.getRedirectUri(oauthProvider);

    // 2. Exchange authorization code for access token
    var tokenResponse = oAuth2Service.exchangeCodeForToken(oauthProvider, code, redirectUri);
    log.debug("Successfully exchanged code for access token");

    // 3. Fetch user info from OAuth provider
    var userInfo = oAuth2Service.getUserInfo(oauthProvider, tokenResponse.getAccessToken());
    log.debug("Successfully fetched user info for provider ID: {}", userInfo.getProviderId());

    // 4. Find or create user account
    User user = findOrCreateOAuth2User(oauthProvider, userInfo);

    // 5. Create or update external account linking
    createOrUpdateExternalAccount(user, oauthProvider, userInfo);

    // 6. Generate JWT tokens
    return generateLoginResponse(user);
  }

  /**
   * Validate JWT token
   */
  public ValidateJwtResponseDto validateJwt(String token) {
    JwtService.JwtValidationResult result = jwtService.validateToken(token);

    ValidateJwtResponseDto response = new ValidateJwtResponseDto()
      .valid(result.isValid())
      .userId(result.getUserId())
      .username(result.getUsername());

    // Add role and expiresAt if token is valid
    if (result.isValid() && result.getRole() != null) {
      // Convert User.Role to ValidateJwtResponseDto.RoleEnum
      ValidateJwtResponseDto.RoleEnum roleEnum = switch (result.getRole()) {
        case admin -> ValidateJwtResponseDto.RoleEnum.ADMIN;
        case user -> ValidateJwtResponseDto.RoleEnum.USER;
        case readonly -> ValidateJwtResponseDto.RoleEnum.READONLY;
        case service -> ValidateJwtResponseDto.RoleEnum.SERVICE;
      };
      response.role(roleEnum);
    }

    if (result.isValid() && result.getExpiresAt() != null) {
      response.expiresAt(result.getExpiresAt());
    }

    return response;
  }

  /**
   * Refresh JWT tokens using refresh token with rotation for enhanced security
   */
  @Transactional
  public LoginResponseDto refreshToken(String refreshToken) {
    log.debug("Refreshing JWT token with rotation");

    String tokenHash = hashToken(refreshToken);
    UUID userId = extractUserIdFromRefreshToken(refreshToken);

    // Find and validate refresh token
    Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByTokenHashAndUserId(
      tokenHash,
      userId
    );

    if (tokenEntity.isEmpty()) {
      log.warn("Refresh token not found for user: {}", userId);
      // Potential token theft - revoke all refresh tokens for this user
      revokeAllUserRefreshTokens(userId);
      throw new RuntimeException("Invalid refresh token");
    }

    RefreshToken token = tokenEntity.get();
    
    // Check if token is already revoked (possible replay attack)
    if (token.getRevoked()) {
      log.warn("Attempted reuse of revoked refresh token for user: {}", userId);
      // Definite token theft - revoke all refresh tokens for this user
      revokeAllUserRefreshTokens(userId);
      throw new RuntimeException("Refresh token has been revoked due to security concerns");
    }
    
    if (token.isExpired()) {
      log.debug("Refresh token is expired for user: {}", userId);
      // Clean up expired token
      refreshTokenRepository.delete(token);
      throw new RuntimeException("Refresh token is expired");
    }

    User user = token.getUser();
    if (!user.isActive()) {
      log.debug("User account is disabled: {}", user.getUsername());
      throw new RuntimeException("User account is disabled");
    }

    log.info("Refresh token successfully validated for user: {}", user.getUsername());

    // CRITICAL: Immediately revoke the old refresh token to prevent reuse
    token.revoke();
    token.markAsUsed();
    refreshTokenRepository.save(token);

    // Generate new tokens (this creates a new refresh token)
    LoginResponseDto response = generateTokenResponse(user);

    log.debug("Token rotation completed for user: {}", user.getUsername());
    return response;
  }

  /**
   * Revoke all refresh tokens for a user (used when token theft is detected)
   */
  @Transactional
  private void revokeAllUserRefreshTokens(UUID userId) {
    log.warn("Revoking all refresh tokens for user: {} due to security concerns", userId);
    
    // Find user and revoke all tokens
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isPresent()) {
      refreshTokenRepository.revokeAllTokensForUser(userOpt.get());
    }
  }

  /**
   * Generate JWT token response with access and refresh tokens
   */
  private LoginResponseDto generateTokenResponse(User user) {
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    // Store refresh token hash
    RefreshToken refreshTokenEntity = RefreshToken.builder()
      .user(user)
      .tokenHash(hashToken(refreshToken))
      .expiresAt(OffsetDateTime.now().plusSeconds(jwtService.getRefreshTokenExpirationSeconds()))
      .revoked(false)
      .build();

    refreshTokenRepository.save(refreshTokenEntity);

    return LoginResponseDto.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .tokenType("Bearer")
      .expiresIn((int) jwtService.getAccessTokenExpirationSeconds())
      .build();
  }

  /**
   * Find existing user by external account or create new user for OAuth2
   */
  private User findOrCreateOAuth2User(ExternalAccount.Provider provider, OAuth2UserInfo userInfo) {
    log.debug(
      "Finding or creating OAuth2 user for provider: {} and ID: {}",
      provider,
      userInfo.getProviderId()
    );

    // 1. Check if external account already exists
    Optional<ExternalAccount> existingAccount =
      externalAccountRepository.findByProviderAndExternalId(provider, userInfo.getProviderId());

    if (existingAccount.isPresent()) {
      User user = existingAccount.get().getUser();
      log.debug("Found existing user: {} for external account", user.getUsername());

      if (!user.isActive()) {
        throw new RuntimeException("User account is disabled");
      }

      return user;
    }

    // 2. Check if user exists by email
    if (userInfo.getEmail() != null) {
      Optional<User> existingUserByEmail = userRepository.findByEmail(userInfo.getEmail());
      if (existingUserByEmail.isPresent()) {
        User user = existingUserByEmail.get();
        log.debug("Found existing user by email: {}", user.getEmail());

        if (!user.isActive()) {
          throw new RuntimeException("User account is disabled");
        }

        return user;
      }
    }

    // 3. Create new user account
    return createNewOAuth2User(provider, userInfo);
  }

  /**
   * Create new user account from OAuth2 user info
   */
  private User createNewOAuth2User(ExternalAccount.Provider provider, OAuth2UserInfo userInfo) {
    log.debug("Creating new user account for OAuth2 provider: {}", provider);

    // Generate unique username if needed
    String username = generateUniqueUsername(userInfo);

    User newUser = User.builder()
      .username(username)
      .email(userInfo.getEmail())
      .firstName(extractFirstName(userInfo))
      .lastName(extractLastName(userInfo))
      .emailVerified(userInfo.getEmailVerified() != null ? userInfo.getEmailVerified() : false)
      .role(User.Role.user) // Default role for new OAuth2 users
      .enabled(true)
      .build();

    User savedUser = userRepository.save(newUser);
    log.info(
      "Created new user account: {} for OAuth2 provider: {}",
      savedUser.getUsername(),
      provider
    );

    return savedUser;
  }

  /**
   * Generate unique username from OAuth2 user info
   */
  private String generateUniqueUsername(OAuth2UserInfo userInfo) {
    // Start with preferred username sources
    String baseUsername = null;

    if (userInfo.getUsername() != null) {
      baseUsername = userInfo.getUsername();
    } else if (userInfo.getEmail() != null) {
      baseUsername = userInfo.getEmail().split("@")[0];
    } else if (userInfo.getName() != null) {
      baseUsername = userInfo.getName().toLowerCase().replace(" ", ".");
    } else {
      baseUsername = "user" + System.currentTimeMillis();
    }

    // Clean username (alphanumeric, dots, underscores, hyphens only)
    baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9._-]", "");

    // Check for uniqueness and add suffix if needed
    String username = baseUsername;
    int suffix = 1;

    while (userRepository.findByUsernameIgnoreCase(username).isPresent()) {
      username = baseUsername + suffix;
      suffix++;
    }

    return username;
  }

  /**
   * Extract first name from OAuth2 user info
   */
  private String extractFirstName(OAuth2UserInfo userInfo) {
    if (userInfo.getGivenName() != null) {
      return userInfo.getGivenName();
    } else if (userInfo.getName() != null) {
      String[] nameParts = userInfo.getName().split(" ");
      return nameParts.length > 0 ? nameParts[0] : null;
    }
    return null;
  }

  /**
   * Extract last name from OAuth2 user info
   */
  private String extractLastName(OAuth2UserInfo userInfo) {
    if (userInfo.getFamilyName() != null) {
      return userInfo.getFamilyName();
    } else if (userInfo.getName() != null) {
      String[] nameParts = userInfo.getName().split(" ");
      return nameParts.length > 1 ? nameParts[nameParts.length - 1] : null;
    }
    return null;
  }

  /**
   * Create or update external account linking
   */
  private void createOrUpdateExternalAccount(
    User user,
    ExternalAccount.Provider provider,
    OAuth2UserInfo userInfo
  ) {
    log.debug(
      "Creating or updating external account for user: {} and provider: {}",
      user.getUsername(),
      provider
    );

    Optional<ExternalAccount> existingAccount =
      externalAccountRepository.findByProviderAndExternalId(provider, userInfo.getProviderId());

    if (existingAccount.isPresent()) {
      // Update existing account
      ExternalAccount account = existingAccount.get();
      account.setExternalEmail(userInfo.getEmail());
      account.setExternalUsername(userInfo.getUsername());
      account.setUpdatedAt(OffsetDateTime.now());

      externalAccountRepository.save(account);
      log.debug("Updated existing external account for user: {}", user.getUsername());
    } else {
      // Create new external account
      ExternalAccount newAccount = ExternalAccount.builder()
        .user(user)
        .provider(provider)
        .externalId(userInfo.getProviderId())
        .externalEmail(userInfo.getEmail())
        .externalUsername(userInfo.getUsername())
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();

      externalAccountRepository.save(newAccount);
      log.info(
        "Created new external account for user: {} and provider: {}",
        user.getUsername(),
        provider
      );
    }
  }

  /**
   * Generate LoginResponse with JWT tokens for user
   */
  private LoginResponseDto generateLoginResponse(User user) {
    log.debug("Generating login response for user: {}", user.getUsername());

    // Generate JWT tokens
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    // Save refresh token
    RefreshToken refreshTokenEntity = RefreshToken.builder()
      .user(user)
      .tokenHash(hashToken(refreshToken))
      .expiresAt(OffsetDateTime.now().plusSeconds(jwtService.getRefreshTokenExpirationSeconds()))
      .revoked(false)
      .build();

    refreshTokenRepository.save(refreshTokenEntity);

    return LoginResponseDto.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .tokenType("Bearer")
      .expiresIn((int) jwtService.getAccessTokenExpirationSeconds())
      .userId(user.getId())
      .username(user.getUsername())
      .role(LoginResponseDto.RoleEnum.fromValue(user.getRole().name().toLowerCase()))
      .build();
  }

  /**
   * Hash token for secure storage
   */
  private String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not available", e);
    }
  }

  /**
   * Extract user ID from refresh token (simplified implementation)
   */
  private UUID extractUserIdFromRefreshToken(String refreshToken) {
    return jwtService.extractUserIdFromToken(refreshToken);
  }
}

package org.sagebionetworks.openchallenges.auth.service.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2CallbackResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2TokenResponse;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2UserInfo;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.ExternalAccountRepository;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Authentication service for OpenChallenges.
 * 
 * Handles OAuth2 authentication flows with external providers (Google, Synapse)
 * and generates OpenChallenges JWT tokens for accessing OC resources.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

  private final UserRepository userRepository;
  private final ExternalAccountRepository externalAccountRepository;
  private final JwtService jwtService;
  private final OAuth2ConfigurationService oAuth2ConfigurationService;
  private final OAuth2Service oAuth2Service;

  /**
   * Handle OAuth2 callback from external providers.
   * Exchanges authorization code for tokens, creates/finds user, and generates OC JWT tokens.
   * 
   * @param provider The OAuth2 provider (google, synapse)
   * @param code Authorization code from OAuth2 provider
   * @param state State parameter for security
   * @return OAuth2CallbackResponseDto with OpenChallenges JWT tokens
   */
  public OAuth2CallbackResponseDto handleOAuth2Callback(String provider, String code, String state) {
    log.info("Handling OAuth2 callback for provider: {}", provider);
    
    // Parse provider
    ExternalAccount.Provider providerEnum;
    try {
      providerEnum = ExternalAccount.Provider.valueOf(provider.toLowerCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Unsupported OAuth2 provider: " + provider);
    }
    
    // Exchange authorization code for tokens
    String redirectUri = oAuth2ConfigurationService.getRedirectUri(providerEnum);
    OAuth2TokenResponse tokenResponse = oAuth2Service.exchangeCodeForToken(providerEnum, code, redirectUri);
    
    // Get user info from OAuth2 provider
    OAuth2UserInfo userInfo = oAuth2Service.getUserInfo(providerEnum, tokenResponse.getAccessToken());
    
    // Find or create user account
    User user = findOrCreateUser(providerEnum, userInfo);
    
    // Check if user is enabled
    if (!user.getEnabled()) {
      throw new RuntimeException("User account is disabled");
    }
    
    // Generate OpenChallenges JWT tokens
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    
    log.info("Successfully generated OC tokens for user: {}", user.getUsername());
    
    // Return login response with OC tokens
    return OAuth2CallbackResponseDto.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .tokenType("Bearer")
      .expiresIn(Math.toIntExact(jwtService.getAccessTokenExpirationSeconds()))
      .userId(user.getId())
      .username(user.getUsername())
      .role(OAuth2CallbackResponseDto.RoleEnum.fromValue(user.getRole().name().toLowerCase()))
      .build();
  }
  
  /**
   * Find existing user by external account or email, or create new user.
   */
  private User findOrCreateUser(ExternalAccount.Provider provider, OAuth2UserInfo userInfo) {
    // First, try to find by external account
    Optional<ExternalAccount> existingAccount = externalAccountRepository
      .findByProviderAndExternalId(provider, userInfo.getId());
      
    if (existingAccount.isPresent()) {
      log.debug("Found existing external account for provider: {}, external ID: {}", 
               provider, userInfo.getId());
      
      // Update external account with latest info
      ExternalAccount account = existingAccount.get();
      account.setExternalEmail(userInfo.getEmail());
      externalAccountRepository.save(account);
      
      return account.getUser();
    }
    
    // Try to find by email to link accounts
    Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());
    if (existingUser.isPresent()) {
      log.debug("Found existing user by email, linking external account: {}", userInfo.getEmail());
      
      User user = existingUser.get();
      createExternalAccount(provider, userInfo, user);
      return user;
    }
    
    // Create new user
    log.debug("Creating new user from OAuth2 info: {}", userInfo.getEmail());
    
    String username = generateUniqueUsername(userInfo);
    
    User newUser = User.builder()
      .username(username)
      .email(userInfo.getEmail())
      .firstName(userInfo.getGivenName())
      .lastName(userInfo.getFamilyName())
      .role(User.Role.user)
      .enabled(true)
      .createdAt(OffsetDateTime.now())
      .updatedAt(OffsetDateTime.now())
      .build();
      
    User savedUser = userRepository.save(newUser);
    createExternalAccount(provider, userInfo, savedUser);
    
    return savedUser;
  }
  
  /**
   * Create external account linking.
   */
  private void createExternalAccount(ExternalAccount.Provider provider, OAuth2UserInfo userInfo, User user) {
    ExternalAccount externalAccount = ExternalAccount.builder()
      .provider(provider)
      .externalId(userInfo.getId())
      .externalEmail(userInfo.getEmail())
      .user(user)
      .build();
      
    externalAccountRepository.save(externalAccount);
  }
  
  /**
   * Generate unique username from OAuth2 user info.
   */
  private String generateUniqueUsername(OAuth2UserInfo userInfo) {
    // Try email prefix first
    String baseUsername = userInfo.getEmail().split("@")[0];
    
    // Clean username (remove dots, etc.)
    baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", ".");
    
    if (!userRepository.findByUsernameIgnoreCase(baseUsername).isPresent()) {
      return baseUsername;
    }
    
    // If taken, add number suffix
    int counter = 1;
    String username;
    do {
      username = baseUsername + counter;
      counter++;
    } while (userRepository.findByUsernameIgnoreCase(username).isPresent());
    
    return username;
  }
}

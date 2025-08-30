package org.sagebionetworks.openchallenges.auth.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2TokenResponse;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2UserInfo;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.sagebionetworks.openchallenges.auth.service.model.entity.RefreshToken;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.ExternalAccountRepository;
import org.sagebionetworks.openchallenges.auth.service.repository.RefreshTokenRepository;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceOAuth2Test {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExternalAccountRepository externalAccountRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OAuth2ConfigurationService oAuth2ConfigurationService;

    @Mock
    private OAuth2Service oAuth2Service;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
            userRepository,
            externalAccountRepository,
            refreshTokenRepository,
            jwtService,
            passwordEncoder,
            oAuth2ConfigurationService,
            oAuth2Service
        );
    }

    @Test
    void handleOAuth2Callback_NewGoogleUser_ShouldCreateUserAndExternalAccount() {
        // Given
        String provider = "google";
        String code = "auth-code-123";
        String state = "random-state";
        
        OAuth2TokenResponse tokenResponse = OAuth2TokenResponse.builder()
            .accessToken("google-access-token")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .build();
            
        OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
            .id("google-user-id")
            .email("john.doe@gmail.com")
            .name("John Doe")
            .givenName("John")
            .familyName("Doe")
            .emailVerified(true)
            .build();

        User savedUser = User.builder()
            .id(UUID.randomUUID())
            .username("john.doe")
            .email("john.doe@gmail.com")
            .firstName("John")
            .lastName("Doe")
            .role(User.Role.user)
            .enabled(true)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

        // Mock OAuth2 flow
        when(oAuth2ConfigurationService.getRedirectUri(ExternalAccount.Provider.google))
            .thenReturn("https://example.com/callback");
        when(oAuth2Service.exchangeCodeForToken(ExternalAccount.Provider.google, code, "https://example.com/callback"))
            .thenReturn(tokenResponse);
        when(oAuth2Service.getUserInfo(ExternalAccount.Provider.google, "google-access-token"))
            .thenReturn(userInfo);

        // Mock user creation
        when(externalAccountRepository.findByProviderAndExternalId(ExternalAccount.Provider.google, "google-user-id"))
            .thenReturn(Optional.empty());
        when(userRepository.findByEmail("john.doe@gmail.com"))
            .thenReturn(Optional.empty());
        when(userRepository.findByUsernameIgnoreCase("john.doe"))
            .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
            .thenReturn(savedUser);

        // Mock JWT generation
        when(jwtService.generateAccessToken(savedUser))
            .thenReturn("jwt-access-token");
        when(jwtService.generateRefreshToken(savedUser))
            .thenReturn("jwt-refresh-token");
        when(jwtService.getAccessTokenExpirationSeconds())
            .thenReturn(3600L);
        when(jwtService.getRefreshTokenExpirationSeconds())
            .thenReturn(604800L);

        // When
        LoginResponseDto result = authenticationService.handleOAuth2Callback(provider, code, state);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("jwt-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("jwt-refresh-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isEqualTo(3600);
        assertThat(result.getUserId()).isEqualTo(savedUser.getId());
        assertThat(result.getUsername()).isEqualTo("john.doe");
        assertThat(result.getRole()).isEqualTo(LoginResponseDto.RoleEnum.USER);

        // Verify user creation
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo("john.doe");
        assertThat(capturedUser.getEmail()).isEqualTo("john.doe@gmail.com");
        assertThat(capturedUser.getFirstName()).isEqualTo("John");
        assertThat(capturedUser.getLastName()).isEqualTo("Doe");
        assertThat(capturedUser.getRole()).isEqualTo(User.Role.user);
        assertThat(capturedUser.getEnabled()).isTrue();

        // Verify external account creation
        ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
        verify(externalAccountRepository).save(accountCaptor.capture());
        ExternalAccount capturedAccount = accountCaptor.getValue();
        assertThat(capturedAccount.getProvider()).isEqualTo(ExternalAccount.Provider.google);
        assertThat(capturedAccount.getExternalId()).isEqualTo("google-user-id");
        assertThat(capturedAccount.getExternalEmail()).isEqualTo("john.doe@gmail.com");
        assertThat(capturedAccount.getExternalUsername()).isNull(); // Google doesn't provide username

        // Verify refresh token storage
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void handleOAuth2Callback_ExistingUserByExternalAccount_ShouldReturnLoginResponse() {
        // Given
        String provider = "google";
        String code = "auth-code-123";
        String state = "random-state";
        
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
            .id(userId)
            .username("existing.user")
            .email("existing@gmail.com")
            .firstName("Existing")
            .lastName("User")
            .role(User.Role.user)
            .enabled(true)
            .build();

        ExternalAccount existingAccount = ExternalAccount.builder()
            .id(UUID.randomUUID())
            .user(existingUser)
            .provider(ExternalAccount.Provider.google)
            .externalId("google-user-id")
            .externalEmail("existing@gmail.com")
            .build();

        OAuth2TokenResponse tokenResponse = OAuth2TokenResponse.builder()
            .accessToken("google-access-token")
            .build();
            
        OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
            .id("google-user-id")
            .email("existing@gmail.com")
            .name("Existing User")
            .build();

        // Mock OAuth2 flow
        when(oAuth2ConfigurationService.getRedirectUri(ExternalAccount.Provider.google))
            .thenReturn("https://example.com/callback");
        when(oAuth2Service.exchangeCodeForToken(ExternalAccount.Provider.google, code, "https://example.com/callback"))
            .thenReturn(tokenResponse);
        when(oAuth2Service.getUserInfo(ExternalAccount.Provider.google, "google-access-token"))
            .thenReturn(userInfo);

        // Mock existing account lookup
        when(externalAccountRepository.findByProviderAndExternalId(ExternalAccount.Provider.google, "google-user-id"))
            .thenReturn(Optional.of(existingAccount));

        // Mock JWT generation
        when(jwtService.generateAccessToken(existingUser))
            .thenReturn("jwt-access-token-existing");
        when(jwtService.generateRefreshToken(existingUser))
            .thenReturn("jwt-refresh-token-existing");
        when(jwtService.getAccessTokenExpirationSeconds())
            .thenReturn(3600L);

        // When
        LoginResponseDto result = authenticationService.handleOAuth2Callback(provider, code, state);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("jwt-access-token-existing");
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("existing.user");

        // Verify no new user creation
        verify(userRepository, never()).save(any(User.class));
        
        // Verify external account update
        verify(externalAccountRepository).save(existingAccount);
    }

    @Test
    void handleOAuth2Callback_ExistingUserByEmail_ShouldLinkAccount() {
        // Given
        String provider = "google";
        String code = "auth-code-123";
        String state = "random-state";
        
        User existingUser = User.builder()
            .id(UUID.randomUUID())
            .username("email.user")
            .email("shared@example.com")
            .role(User.Role.user)
            .enabled(true)
            .build();

        OAuth2TokenResponse tokenResponse = OAuth2TokenResponse.builder()
            .accessToken("google-access-token")
            .build();
            
        OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
            .id("google-user-id-new")
            .email("shared@example.com")
            .name("Email User")
            .build();

        // Mock OAuth2 flow
        when(oAuth2ConfigurationService.getRedirectUri(ExternalAccount.Provider.google))
            .thenReturn("https://example.com/callback");
        when(oAuth2Service.exchangeCodeForToken(ExternalAccount.Provider.google, code, "https://example.com/callback"))
            .thenReturn(tokenResponse);
        when(oAuth2Service.getUserInfo(ExternalAccount.Provider.google, "google-access-token"))
            .thenReturn(userInfo);

        // Mock user lookup by email
        when(externalAccountRepository.findByProviderAndExternalId(ExternalAccount.Provider.google, "google-user-id-new"))
            .thenReturn(Optional.empty());
        when(userRepository.findByEmail("shared@example.com"))
            .thenReturn(Optional.of(existingUser));

        // Mock JWT generation
        when(jwtService.generateAccessToken(existingUser))
            .thenReturn("jwt-access-token-linked");
        when(jwtService.generateRefreshToken(existingUser))
            .thenReturn("jwt-refresh-token-linked");
        when(jwtService.getAccessTokenExpirationSeconds())
            .thenReturn(3600L);

        // When
        LoginResponseDto result = authenticationService.handleOAuth2Callback(provider, code, state);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("jwt-access-token-linked");
        assertThat(result.getUsername()).isEqualTo("email.user");

        // Verify no new user creation
        verify(userRepository, never()).save(any(User.class));
        
        // Verify new external account creation for linking
        ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
        verify(externalAccountRepository).save(accountCaptor.capture());
        ExternalAccount capturedAccount = accountCaptor.getValue();
        assertThat(capturedAccount.getUser()).isEqualTo(existingUser);
        assertThat(capturedAccount.getProvider()).isEqualTo(ExternalAccount.Provider.google);
        assertThat(capturedAccount.getExternalId()).isEqualTo("google-user-id-new");
    }

    @Test
    void handleOAuth2Callback_DisabledUser_ShouldThrowException() {
        // Given
        String provider = "google";
        String code = "auth-code-123";
        String state = "random-state";
        
        User disabledUser = User.builder()
            .id(UUID.randomUUID())
            .username("disabled.user")
            .email("disabled@example.com")
            .enabled(false) // User is disabled
            .build();

        ExternalAccount existingAccount = ExternalAccount.builder()
            .user(disabledUser)
            .provider(ExternalAccount.Provider.google)
            .externalId("google-user-id")
            .build();

        OAuth2TokenResponse tokenResponse = OAuth2TokenResponse.builder()
            .accessToken("google-access-token")
            .build();
            
        OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
            .id("google-user-id")
            .email("disabled@example.com")
            .build();

        // Mock OAuth2 flow
        when(oAuth2ConfigurationService.getRedirectUri(ExternalAccount.Provider.google))
            .thenReturn("https://example.com/callback");
        when(oAuth2Service.exchangeCodeForToken(ExternalAccount.Provider.google, code, "https://example.com/callback"))
            .thenReturn(tokenResponse);
        when(oAuth2Service.getUserInfo(ExternalAccount.Provider.google, "google-access-token"))
            .thenReturn(userInfo);

        // Mock disabled user lookup
        when(externalAccountRepository.findByProviderAndExternalId(ExternalAccount.Provider.google, "google-user-id"))
            .thenReturn(Optional.of(existingAccount));

        // When/Then
        assertThatThrownBy(() -> authenticationService.handleOAuth2Callback(provider, code, state))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User account is disabled");
    }

    @Test
    void handleOAuth2Callback_UnsupportedProvider_ShouldThrowException() {
        // Given
        String provider = "facebook"; // Unsupported provider
        String code = "auth-code-123";
        String state = "random-state";

        // When/Then
        assertThatThrownBy(() -> authenticationService.handleOAuth2Callback(provider, code, state))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unsupported OAuth2 provider: facebook");
    }
}

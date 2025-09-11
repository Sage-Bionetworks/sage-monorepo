package org.sagebionetworks.openchallenges.auth.service.util;

/**
 * Constants for OAuth2 scope strings used throughout the authentication system.
 *
 * This class centralizes all scope definitions to ensure consistency across
 * the application and make maintenance easier. Scopes follow the format:
 * "{action}:{resource}" where action is typically read, create, update, or delete.
 */
public final class ScopeConstants {

  // Prevent instantiation
  private ScopeConstants() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  // ========== Profile Scopes ==========

  /** Read user profile information */
  public static final String READ_PROFILE = "read:profile";

  /** Update user profile information */
  public static final String UPDATE_PROFILE = "update:profile";

  // ========== API Key Scopes ==========

  /** Read API key information */
  public static final String READ_API_KEY = "read:api-key";

  /** Create new API keys */
  public static final String CREATE_API_KEY = "create:api-key";

  /** Update existing API keys */
  public static final String UPDATE_API_KEY = "update:api-key";

  /** Delete API keys */
  public static final String DELETE_API_KEY = "delete:api-key";

  /** Rotate API keys */
  public static final String ROTATE_API_KEY = "rotate:api-key";

  // ========== Organization Scopes ==========

  /** Read organization information */
  public static final String READ_ORGANIZATIONS = "read:organizations";

  /** Create new organizations */
  public static final String CREATE_ORGANIZATIONS = "create:organizations";

  /** Update existing organizations */
  public static final String UPDATE_ORGANIZATIONS = "update:organizations";

  /** Delete organizations */
  public static final String DELETE_ORGANIZATIONS = "delete:organizations";

  // ========== Challenge Scopes ==========

  /** Read challenge information */
  public static final String READ_CHALLENGES = "read:challenges";

  /** Create new challenges */
  public static final String CREATE_CHALLENGES = "create:challenges";

  /** Update existing challenges */
  public static final String UPDATE_CHALLENGES = "update:challenges";

  /** Delete challenges */
  public static final String DELETE_CHALLENGES = "delete:challenges";

  /** Read challenge analytics data */
  public static final String READ_CHALLENGES_ANALYTICS = "read:challenges-analytics";

  // ========== Challenge Platform Scopes ==========

  /** Read challenge platform information */
  public static final String READ_CHALLENGE_PLATFORMS = "read:challenge-platforms";

  /** Create new challenge platforms */
  public static final String CREATE_CHALLENGE_PLATFORMS = "create:challenge-platforms";

  /** Update existing challenge platforms */
  public static final String UPDATE_CHALLENGE_PLATFORMS = "update:challenge-platforms";

  /** Delete challenge platforms */
  public static final String DELETE_CHALLENGE_PLATFORMS = "delete:challenge-platforms";

  // ========== EDAM Concepts Scopes ==========

  /** Read EDAM (Ontology) concepts */
  public static final String READ_EDAM_CONCEPTS = "read:edam-concepts";

  // ========== Admin Panel Scopes ==========

  /** Access to admin panel functionality */
  public static final String ADMIN_PANEL = "admin:panel";

  // ========== OpenID Connect Standard Scopes ==========

  /** OpenID Connect standard scope */
  public static final String OPENID = "openid";

  /** Profile information scope */
  public static final String PROFILE = "profile";

  /** Email information scope */
  public static final String EMAIL = "email";
}

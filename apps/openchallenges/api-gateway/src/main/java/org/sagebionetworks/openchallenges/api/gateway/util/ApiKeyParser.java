package org.sagebionetworks.openchallenges.api.gateway.util;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing API keys with environment-specific prefixes.
 * Supports formats: oc_dev_<suffix>.<secret>, oc_stage_<suffix>.<secret>, oc_prod_<suffix>.<secret>
 *
 * Format (regex):
 *   ^(oc_(dev|stage|prod)_)([A-Za-z0-9_-]+)\\.([A-Za-z0-9._~-]{8,})$
 *
 * Groups:
 *   1: environment prefix (e.g., "oc_dev_")
 *   2: environment name   (e.g., "dev")
 *   3: suffix             (>=1 char, [A-Za-z0-9_-]+)
 *   4: secret             (>=8 chars, [A-Za-z0-9._~-]+)
 */
public final class ApiKeyParser {

  private ApiKeyParser() {} // utility

  // Single compiled pattern: validates and extracts in one shot.
  // Secret allows dot/tilde to be more flexible; adjust as needed.
  private static final Pattern KEY_PATTERN = Pattern.compile(
    "^(oc_(dev|stage|prod)_)([A-Za-z0-9_-]+)\\.([A-Za-z0-9._~-]{8,})$"
  );

  public enum Environment {
    DEV("oc_dev_"),
    STAGE("oc_stage_"),
    PROD("oc_prod_");

    private final String prefix;

    Environment(String prefix) {
      this.prefix = prefix;
    }

    public String prefix() {
      return prefix;
    }

    static Environment fromString(String value) {
      return switch (value.toLowerCase(Locale.ROOT)) {
        case "dev" -> DEV;
        case "stage" -> STAGE;
        case "prod" -> PROD;
        default -> throw new IllegalArgumentException("Unknown environment: " + value);
      };
    }
  }

  /**
   * Immutable parsed representation of an API key.
   */
  public record ParsedApiKey(
    Environment environment,
    String environmentPrefix,
    String suffix,
    String secret
  ) {
    public ParsedApiKey {
      if (environment == null) throw new IllegalArgumentException("environment cannot be null");
      if (
        environmentPrefix == null || environmentPrefix.isBlank()
      ) throw new IllegalArgumentException("environmentPrefix cannot be blank");
      if (suffix == null || suffix.isBlank()) throw new IllegalArgumentException(
        "suffix cannot be blank"
      );
      if (secret == null || secret.isBlank()) throw new IllegalArgumentException(
        "secret cannot be blank"
      );
    }

    @Override
    public String toString() {
      return (
        "ParsedApiKey{" +
        "environment=" +
        environment +
        ", environmentPrefix='" +
        environmentPrefix +
        '\'' +
        ", suffix='" +
        suffix +
        '\'' +
        ", secret='[REDACTED]'" +
        '}'
      );
    }
  }

  /**
   * Parse and validate an API key; throws on invalid format.
   */
  public static ParsedApiKey parse(String apiKey) {
    return tryParse(apiKey).orElseThrow(() ->
      new IllegalArgumentException(
        "Invalid API key format. Expected: oc_{dev|stage|prod}_{suffix}.{secret} " +
        "(suffix: [A-Za-z0-9_-]+, secret: [A-Za-z0-9._~-]{8,})"
      )
    );
  }

  /**
   * Attempt to parse without throwing. Returns Optional.empty() for invalid input.
   */
  public static Optional<ParsedApiKey> tryParse(String apiKey) {
    if (apiKey == null) return Optional.empty();

    // Disallow leading/trailing whitespace—common copy/paste issue.
    String trimmed = apiKey.trim();
    if (!trimmed.equals(apiKey)) return Optional.empty();

    Matcher m = KEY_PATTERN.matcher(apiKey);
    if (!m.matches()) return Optional.empty();

    String envPrefix = m.group(1); // e.g. "oc_dev_"
    String envName = m.group(2); // e.g. "dev"
    String suffix = m.group(3);
    String secret = m.group(4);

    Environment env = Environment.fromString(envName);
    return Optional.of(new ParsedApiKey(env, envPrefix, suffix, secret));
  }

  /**
   * Quick validation check.
   */
  public static boolean isValid(String apiKey) {
    return tryParse(apiKey).isPresent();
  }
}

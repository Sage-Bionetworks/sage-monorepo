package org.sagebionetworks.openchallenges.api.gateway.service;

import java.util.List;

/**
 * Utility class for parsing API keys with environment-specific prefixes.
 * Supports formats: oc_dev_<suffix>.<secret>, oc_stage_<suffix>.<secret>, oc_prod_<suffix>.<secret>
 */
public class ApiKeyParser {

    private static final List<String> VALID_PREFIXES = List.of("oc_dev_", "oc_stage_", "oc_prod_");

    /**
     * Parses an API key into its components.
     *
     * @param apiKey The full API key string
     * @return ParsedApiKey containing environment prefix, suffix, and secret
     * @throws IllegalArgumentException if API key format is invalid
     */
    public static ParsedApiKey parseApiKey(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("API key cannot be null");
        }

        // Find which environment prefix this key uses
        String envPrefix = VALID_PREFIXES.stream()
            .filter(apiKey::startsWith)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid API key format: must start with oc_dev_, oc_stage_, or oc_prod_"));

        String remaining = apiKey.substring(envPrefix.length());
        int dotIndex = remaining.indexOf('.');
        
        if (dotIndex == -1 || dotIndex == 0 || dotIndex == remaining.length() - 1) {
            throw new IllegalArgumentException("Invalid API key format: missing suffix or secret");
        }

        String suffix = remaining.substring(0, dotIndex);
        String secret = remaining.substring(dotIndex + 1);

        return new ParsedApiKey(envPrefix, suffix, secret);
    }

    /**
     * Represents a parsed API key with its components.
     */
    public static class ParsedApiKey {
        private final String environmentPrefix;
        private final String suffix;
        private final String secret;

        public ParsedApiKey(String environmentPrefix, String suffix, String secret) {
            this.environmentPrefix = environmentPrefix;
            this.suffix = suffix;
            this.secret = secret;
        }

        public String getEnvironmentPrefix() {
            return environmentPrefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public String getSecret() {
            return secret;
        }
        
        /**
         * @deprecated Use getSuffix() instead
         */
        @Deprecated
        public String getPrefix() {
            return suffix;
        }

        @Override
        public String toString() {
            return "ParsedApiKey{" +
                    "environmentPrefix='" + environmentPrefix + '\'' +
                    ", suffix='" + suffix + '\'' +
                    ", secret='[REDACTED]'" +
                    '}';
        }
    }
}

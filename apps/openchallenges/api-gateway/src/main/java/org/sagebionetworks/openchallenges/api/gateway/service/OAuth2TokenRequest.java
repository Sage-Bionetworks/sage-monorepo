package org.sagebionetworks.openchallenges.api.gateway.service;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for OAuth2 token endpoint using client credentials flow.
 * Based on RFC 6749 Section 4.4.
 */
public class OAuth2TokenRequest {

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("scope")
    private String scope;

    // Default constructor for JSON serialization
    public OAuth2TokenRequest() {}

    public OAuth2TokenRequest(String grantType, String scope) {
        this.grantType = grantType;
        this.scope = scope;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "OAuth2TokenRequest{" +
                "grantType='" + grantType + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}

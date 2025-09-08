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

    @JsonProperty("resource")
    private String resource;

    // Default constructor for JSON serialization
    public OAuth2TokenRequest() {}

    public OAuth2TokenRequest(String grantType, String scope) {
        this.grantType = grantType;
        this.scope = scope;
    }

    public OAuth2TokenRequest(String grantType, String scope, String resource) {
        this.grantType = grantType;
        this.scope = scope;
        this.resource = resource;
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

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "OAuth2TokenRequest{" +
                "grantType='" + grantType + '\'' +
                ", scope='" + scope + '\'' +
                ", resource='" + resource + '\'' +
                '}';
    }
}

/**
 * Synapse REST API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v1
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

/**
 * OpenID Provider Configuration, described by <a href=\"https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig\">OpenID Connect Core 1.0</a>. Fields are defined by the <a href=\"https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata\">OpenID Provider Metadata</a>.
 */
export interface OrgSagebionetworksRepoModelOauthOIDConnectConfiguration {
  issuer?: string;
  authorization_endpoint?: string;
  token_endpoint?: string;
  revocation_endpoint?: string;
  userinfo_endpoint?: string;
  jwks_uri?: string;
  registration_endpoint?: string;
  /**
   * List of the OAuth 2.0 scope values that Synapse supports.
   */
  scopes_supported?: Array<string>;
  /**
   * List of the OAuth 2.0 response types that Synapse supports.
   */
  response_types_supported?: Array<string>;
  /**
   * List of the OAuth 2.0 grant types that Synapse supports.
   */
  grant_types_supported?: Array<string>;
  /**
   * List of the subject identified types that Synapse supports.
   */
  subject_types_supported?: Array<string>;
  /**
   * List of the JWS signing algorithms (alg values) supported by Synapse for the ID Token to encode the Claims in a JWT
   */
  id_token_signing_alg_values_supported?: Array<string>;
  /**
   * List of the JWS signing algorithms (alg values) supported by the UserInfo Endpoint to encode the Claims in a JWT
   */
  userinfo_signing_alg_values_supported?: Array<string>;
  /**
   * List of signing algorithms supported for signing request objects, or \'[\"none\"]\' if passing by \'request\' parameter is unsupported.  See <a href=\"https://openid.net/specs/openid-connect-core-1_0.html#RequestObject\">OpenID Connect Core 1.0</a>
   */
  request_object_signing_alg_values_supported?: Array<string>;
  /**
   * List of the Claim Names of the Claims that the Synapse MAY be able to supply values for.
   */
  claims_supported?: Array<string>;
  service_documentation?: string;
  claims_parameter_supported?: boolean;
  /**
   * a list of Client Authentication methods supported by Synapse\'s Token Endpoint.  See <a href=\"https://openid.net/specs/openid-connect-core-1_0.html#ClientAuthentication\">OpenID Connect Core 1.0 Client Authentication</a>
   */
  token_endpoint_auth_methods_supported?: Array<string>;
}

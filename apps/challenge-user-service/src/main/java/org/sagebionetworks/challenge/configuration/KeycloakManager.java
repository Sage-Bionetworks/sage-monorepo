package org.sagebionetworks.challenge.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakManager {

  // private final KeycloakProperties keycloakProperties;

  private final KeycloakManagerProperties keycloakProperties;
  private static Keycloak keycloakInstance = null;

  public RealmResource getKeyCloakInstanceWithRealm() {
    return getInstance().realm(keycloakProperties.getRealm());
  }

  public Keycloak getInstance() {

    log.info("realm: " + keycloakProperties.getRealm());

    if (keycloakInstance == null) {
      keycloakInstance = KeycloakBuilder.builder().serverUrl(keycloakProperties.getServerUrl())
          .realm(keycloakProperties.getRealm()).grantType("client_credentials")
          .clientId(keycloakProperties.getClientId())
          .clientSecret(keycloakProperties.getClientSecret()).build();
    }
    return keycloakInstance;
  }

  public String getRealm() {
    return keycloakProperties.getRealm();
  }

  // public KeycloakProperties getProperties() {
  // return this.keycloakProperties;
  // }
}

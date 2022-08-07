package org.sagebionetworks.challenge.configuration;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeycloakManager {

  private final KeycloakManagerProperties keycloakProperties;
  private static Keycloak keycloakInstance = null;

  public RealmResource getKeycloakInstanceWithRealm() {
    return getInstance().realm(keycloakProperties.getRealm());
  }

  public Keycloak getInstance() {
    if (keycloakInstance == null) {
      keycloakInstance = KeycloakBuilder.builder().serverUrl(keycloakProperties.getServerUrl())
          .realm(keycloakProperties.getRealm()).grantType("client_credentials")
          .clientId(keycloakProperties.getClientId())
          .clientSecret(keycloakProperties.getClientSecret()).build();
    }
    return keycloakInstance;
  }
}

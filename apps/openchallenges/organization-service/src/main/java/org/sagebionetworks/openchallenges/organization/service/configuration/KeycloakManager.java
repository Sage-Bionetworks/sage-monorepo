package org.sagebionetworks.openchallenges.organization.service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KeycloakManager {

  @Autowired
  private KeycloakManagerProperties keycloakProperties;

  private static Keycloak keycloakInstance = null;

  public RealmResource getKeycloakInstanceWithRealm() {
    return getInstance().realm(keycloakProperties.getRealm());
  }

  public Keycloak getInstance() {
    if (keycloakInstance == null) {
      log.info("KC SERVER URL: {}", keycloakProperties.getServerUrl());
      keycloakInstance = KeycloakBuilder.builder()
        .serverUrl(keycloakProperties.getServerUrl())
        .realm(keycloakProperties.getRealm())
        .grantType("client_credentials")
        .clientId(keycloakProperties.getClientId())
        .clientSecret(keycloakProperties.getClientSecret())
        .build();
    }
    return keycloakInstance;
  }
}

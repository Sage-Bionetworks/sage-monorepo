package org.sagebionetworks.challenge.service;

import org.sagebionetworks.challenge.configuration.KeycloakManager;
import lombok.RequiredArgsConstructor;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

  private final KeycloakManager keycloakManager;

  public Integer createUser(UserRepresentation userRepresentation) {
    Response response = keycloakManager.getKeycloakInstanceWithRealm().users().create(userRepresentation);
    return response.getStatus();
  }

  public void updateUser(UserRepresentation userRepresentation) {
    keycloakManager.getKeycloakInstanceWithRealm().users().get(userRepresentation.getId())
        .update(userRepresentation);
  }

  public Optional<UserRepresentation> getUserByUsername(String username) {
    return keycloakManager.getKeycloakInstanceWithRealm().users().search(username).stream()
        .filter(userRep -> username.equals(userRep.getUsername()))
        .findFirst();
  }

  public UserRepresentation getUser(String authId) {
    try {
      UserResource userResource = keycloakManager.getKeycloakInstanceWithRealm().users().get(authId);
      return userResource.toRepresentation();
    } catch (Exception e) {
      throw new RuntimeException("User not found under given ID");
    }
  }
}

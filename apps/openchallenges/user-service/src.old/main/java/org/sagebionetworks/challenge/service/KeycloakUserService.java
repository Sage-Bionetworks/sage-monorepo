package org.sagebionetworks.challenge.service;

import java.util.Optional;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.sagebionetworks.challenge.configuration.KeycloakManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserService {

  @Autowired private KeycloakManager keycloakManager;

  public Integer createUser(UserRepresentation userRepresentation) {
    Response response =
        keycloakManager.getKeycloakInstanceWithRealm().users().create(userRepresentation);
    return response.getStatus();
  }

  public void updateUser(UserRepresentation userRepresentation) {
    keycloakManager
        .getKeycloakInstanceWithRealm()
        .users()
        .get(userRepresentation.getId())
        .update(userRepresentation);
  }

  public Optional<UserRepresentation> getUserByUsername(String username) {
    return keycloakManager.getKeycloakInstanceWithRealm().users().search(username).stream()
        .filter(userRep -> username.equals(userRep.getUsername()))
        .findFirst();
  }

  public UserRepresentation getUser(String authId) {
    try {
      UserResource userResource =
          keycloakManager.getKeycloakInstanceWithRealm().users().get(authId);
      return userResource.toRepresentation();
    } catch (Exception e) {
      throw new RuntimeException("User not found under given ID");
    }
  }
}

package org.sagebionetworks.challenge.service;

import org.sagebionetworks.challenge.model.dto.LoginRequest;
import org.sagebionetworks.challenge.model.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService {

  @Value("${app.keycloak.login.url}")
  private String loginUrl;

  @Value("${app.keycloak.client-id}")
  private String clientId;

  @Value("${app.keycloak.client-secret}")
  private String clientSecret;

  @Value("${app.keycloak.grant-type}")
  private String grantType;

  @Autowired
  RestTemplate restTemplate;

  public ResponseEntity<LoginResponse> login(LoginRequest request) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("username", request.getUsername());
    map.add("password", request.getPassword());
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    map.add("grant_type", grantType);

    log.info("client_id: {}", clientId);

    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
    ResponseEntity<LoginResponse> loginResponse =
        restTemplate.postForEntity(loginUrl, httpEntity, LoginResponse.class);

    log.info(loginResponse.getBody().toString());

    return ResponseEntity.status(200).body(loginResponse.getBody());

  }
}

package org.sagebionetworks.challenge.model.dto;

import lombok.Data;

@Data
public class User {
  private Long id;
  private String email;
  private String password;
  private String authId;
  private UserStatus status;
}

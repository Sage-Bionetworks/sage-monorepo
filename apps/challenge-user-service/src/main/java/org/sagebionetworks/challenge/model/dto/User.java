package org.sagebionetworks.challenge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private Long id;
  private String username;
  private String email;
  private String password;
  private String authId;
  private UserStatus status;

  // public User(String username, String email, String password) {
  // this.username = username;
  // this.email = email;
  // this.password = password;
  // }
}

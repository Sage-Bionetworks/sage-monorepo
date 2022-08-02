package org.sagebionetworks.challenge.model.entity;

import org.sagebionetworks.challenge.model.dto.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "challenge_user")
@NoArgsConstructor
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String username;
  private String authId;
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  public UserEntity(String username, String authId, UserStatus status) {
    this.username = username;
    this.authId = authId;
    this.status = status;
  }
}

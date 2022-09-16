package org.sagebionetworks.challenge.model.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sagebionetworks.challenge.model.dto.UserStatus;

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

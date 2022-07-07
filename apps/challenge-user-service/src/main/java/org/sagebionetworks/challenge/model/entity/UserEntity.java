package org.sagebionetworks.challenge.model.entity;

import org.sagebionetworks.challenge.model.dto.UserStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "challenge_user")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String login;
  private String authId;

  @Enumerated(EnumType.STRING)
  private UserStatus status;
}

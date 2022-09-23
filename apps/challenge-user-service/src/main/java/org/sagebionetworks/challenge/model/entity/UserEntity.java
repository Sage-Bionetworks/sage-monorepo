package org.sagebionetworks.challenge.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sagebionetworks.challenge.model.dto.UserStatusDto;

/**
 * The User information saved to DB.
 *
 * <p>The following properties are saved in Keycloak: email, password (hash).
 */
@Entity
@Table(name = "challenge_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String authId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatusDto status;
}

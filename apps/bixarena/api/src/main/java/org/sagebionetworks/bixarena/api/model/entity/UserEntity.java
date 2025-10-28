package org.sagebionetworks.bixarena.api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Read-only entity for querying user data from the auth schema.
 * This entity is used only for statistics and does not support write operations.
 */
@Entity
@Table(name = "user", schema = "auth")
@Immutable
@Getter
@Setter
public class UserEntity {

  @Id
  private UUID id;
}

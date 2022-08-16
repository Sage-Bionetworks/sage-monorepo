package org.sagebionetworks.challenge.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.sagebionetworks.challenge.model.dto.UserStatus;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;

@AutoConfigureWebClient
@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  UserRepository repository;

  @Test
  public void shouldFindNoUsersIfRepositoryIsEmpty() {
    Iterable<UserEntity> users = repository.findAll();
    assertThat(users).isEmpty();
  }

  @Test
  public void shouldStoreGivenUser() {
    UserEntity user = repository
        .save(new UserEntity("test", "1212f921-6ab0-444f-a5ea-9dc154199a3c", UserStatus.PENDING));
    assertThat(user).hasFieldOrProperty("id");
    assertThat(user).hasFieldOrPropertyWithValue("username", "test");
    assertThat(user).hasFieldOrPropertyWithValue("authId", "1212f921-6ab0-444f-a5ea-9dc154199a3c");
    assertThat(user).hasFieldOrPropertyWithValue("status", UserStatus.PENDING);
  }

  @Test
  public void shouldFindAllUsers() {
    UserEntity user1 =
        new UserEntity("test1", "1212f921-6ab0-444f-a5ea-9dc154199a31", UserStatus.PENDING);
    UserEntity user2 =
        new UserEntity("test2", "1212f921-6ab0-444f-a5ea-9dc154199a32", UserStatus.PENDING);
    entityManager.persist(user1);
    entityManager.persist(user2);
    Iterable<UserEntity> users = repository.findAll();
    assertThat(users).hasSize(2).contains(user1, user2);
  }

  @Test
  public void shouldDeleteAllUsers() {
    entityManager.persist(
        new UserEntity("test1", "1212f921-6ab0-444f-a5ea-9dc154199a31", UserStatus.PENDING));
    entityManager.persist(
        new UserEntity("test2", "1212f921-6ab0-444f-a5ea-9dc154199a32", UserStatus.PENDING));
    repository.deleteAll();
    assertThat(repository.findAll()).isEmpty();
  }
}

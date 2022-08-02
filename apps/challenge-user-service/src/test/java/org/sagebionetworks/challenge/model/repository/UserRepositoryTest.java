package org.sagebionetworks.challenge.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureWebClient
@ActiveProfiles("unit")
public class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  UserRepository repository;

  @Test
  public void shouldFindNoUsersIfRepositoryIsEmpty() {
    Iterable<UserEntity> users = repository.findAll();
    assertThat(users).isEmpty();
  }
}

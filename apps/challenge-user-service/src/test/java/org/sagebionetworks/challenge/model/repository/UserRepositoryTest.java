package org.sagebionetworks.challenge.model.repository;

// import static org.junit.jupiter.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  UserRepository repository;

  // @Test
  // public void shouldFindNoTutorialsIfRepositoryIsEmpty() {
  // // Iterable<UserEntity> tutorials = repository.findAll();

  // // assertThat(tutorials).isEmpty();
  // }
}

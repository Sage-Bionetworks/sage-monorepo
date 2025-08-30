package org.sagebionetworks.openchallenges.auth.service.repository;

import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByUsername(String username);

  Optional<User> findByUsernameIgnoreCase(String username);

  Optional<User> findByUsernameAndEnabled(String username, Boolean enabled);
  
  Optional<User> findByEmail(String email);
  
  Optional<User> findByEmailAndEnabled(String email, Boolean enabled);

  boolean existsByUsername(String username);
  
  boolean existsByEmail(String email);
}

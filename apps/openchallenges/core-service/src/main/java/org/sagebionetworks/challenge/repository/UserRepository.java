package org.sagebionetworks.challenge.repository;

import java.util.Optional;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByIdentificationNumber(String identificationNumber);
}

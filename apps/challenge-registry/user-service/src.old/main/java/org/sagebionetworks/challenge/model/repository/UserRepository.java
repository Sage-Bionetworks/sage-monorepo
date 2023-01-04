package org.sagebionetworks.challenge.model.repository;

import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {}

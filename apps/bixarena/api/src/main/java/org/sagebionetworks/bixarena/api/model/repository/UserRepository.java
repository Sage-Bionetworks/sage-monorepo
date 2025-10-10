package org.sagebionetworks.bixarena.api.model.repository;

import org.sagebionetworks.bixarena.api.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {}

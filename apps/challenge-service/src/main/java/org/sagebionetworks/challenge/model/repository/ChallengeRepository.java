package org.sagebionetworks.challenge.model.repository;

import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {

  // Optional<ChallengeEntity> findByLogin(String login);
}

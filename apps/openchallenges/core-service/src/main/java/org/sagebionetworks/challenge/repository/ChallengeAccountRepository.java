package org.sagebionetworks.challenge.repository;

import org.sagebionetworks.challenge.model.entity.ChallengeAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeAccountRepository extends JpaRepository<ChallengeAccountEntity, Long> {
  Optional<ChallengeAccountEntity> findByNumber(String accountNumber);
}

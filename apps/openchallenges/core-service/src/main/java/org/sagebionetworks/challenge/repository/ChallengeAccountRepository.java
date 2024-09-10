package org.sagebionetworks.challenge.repository;

import java.util.Optional;
import org.sagebionetworks.challenge.model.entity.ChallengeAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeAccountRepository extends JpaRepository<ChallengeAccountEntity, Long> {
  Optional<ChallengeAccountEntity> findByNumber(String accountNumber);
}

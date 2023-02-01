package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import java.util.Optional;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeInputDataTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeInputDataTypeRepository
    extends JpaRepository<ChallengeInputDataTypeEntity, Long> {

  Optional<ChallengeInputDataTypeEntity> findByName(String name);
}

package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeInputDataTypeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeInputDataTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomChallengeInputDataTypeRepository {

  Page<ChallengeInputDataTypeEntity> findAll(
      Pageable pageable, ChallengeInputDataTypeSearchQueryDto query, String... fields);
}

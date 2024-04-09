package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.EdamConceptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomEdamConceptRepository {

  Page<EdamConceptEntity> findAll(
      Pageable pageable, EdamConceptSearchQueryDto query, String... fields);
}

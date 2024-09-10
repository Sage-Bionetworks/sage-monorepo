package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengePlatformEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomChallengePlatformRepository {
  Page<ChallengePlatformEntity> findAll(
    Pageable pageable,
    ChallengePlatformSearchQueryDto query,
    String... fields
  );
}

package org.sagebionetworks.challenge.model.repository;

import java.util.List;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.springframework.data.domain.Pageable;

public interface ChallengeRepositoryCustom {

  List<ChallengeEntity> findAll(Pageable pageable, ChallengeFilter filter);
}

package org.sagebionetworks.challenge.model.repository;

import java.util.List;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;

public interface ChallengeRepositoryCustom {

  List<ChallengeEntity> findAll(ChallengeFilter filter);
}

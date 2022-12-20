package org.sagebionetworks.challenge.model.repository;

import java.util.ArrayList;
import java.util.List;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ChallengeRepositoryCustomImpl extends QuerydslRepositorySupport
    implements ChallengeRepositoryCustom {

  public ChallengeRepositoryCustomImpl() {
    super(ChallengeEntity.class);
  }

  @Override
  public List<ChallengeEntity> findAll(ChallengeFilter filter) {
    List<ChallengeEntity> challenges = new ArrayList<>();
    return challenges;
  }
}

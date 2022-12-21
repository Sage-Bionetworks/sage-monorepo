package org.sagebionetworks.challenge.model.repository;

import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.challenge.model.entity.QChallengeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ChallengeRepositoryCustomImpl extends QuerydslRepositorySupport
    implements ChallengeRepositoryCustom {

  public ChallengeRepositoryCustomImpl() {
    super(ChallengeEntity.class);
  }

  @Override
  public List<ChallengeEntity> findAll(Pageable pageable, ChallengeFilter filter) {
    QChallengeEntity challenge = QChallengeEntity.challengeEntity;

    JPQLQuery<ChallengeEntity> query = from(challenge);

    if (filter.getStatus() != null && filter.getStatus().size() > 0) {
      query = query.where(challenge.status.in(filter.getStatus()));
    }

    if (filter.getDifficulty() != null && filter.getDifficulty().size() > 0) {
      query = query.where(challenge.difficulty.in(filter.getDifficulty()));
    }

    query = super.getQuerydsl().applyPagination(pageable, query);

    // List<ChallengeEntity> challenges = new ArrayList<>();
    return query.fetch();
  }
}

package org.sagebionetworks.challenge.model.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.challenge.model.entity.QChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class CustomChallengeRepositoryImpl extends QuerydslRepositorySupport
    implements CustomChallengeRepository {

  public CustomChallengeRepositoryImpl() {
    super(ChallengeEntity.class);
  }

  @Override
  public Page<ChallengeEntity> findAll(Pageable pageable, ChallengeFilter filter) {
    QChallengeEntity challenge = QChallengeEntity.challengeEntity;

    JPQLQuery<ChallengeEntity> query = from(challenge);

    if (filter.getStatus() != null && filter.getStatus().size() > 0) {
      query = query.where(challenge.status.in(filter.getStatus()));
    }
    if (filter.getPlatforms() != null && filter.getPlatforms().size() > 0) {
      query = query.where(challenge.platform.name.in(filter.getPlatforms()));
    }
    if (filter.getDifficulties() != null && filter.getDifficulties().size() > 0) {
      query = query.where(challenge.difficulty.in(filter.getDifficulties()));
    }
    if (filter.getSubmissionTypes() != null && filter.getSubmissionTypes().size() > 0) {
      query = query.where(challenge.submissionTypes.any().name.in(filter.getSubmissionTypes()));
    }
    if (filter.getIncentives() != null && filter.getIncentives().size() > 0) {
      query = query.where(challenge.incentives.any().name.in(filter.getIncentives()));
    }
    query = super.getQuerydsl().applyPagination(pageable, query);

    QueryResults<ChallengeEntity> results = query.fetchResults();
    return new PageImpl<ChallengeEntity>(results.getResults(), pageable, results.getTotal());
  }
}

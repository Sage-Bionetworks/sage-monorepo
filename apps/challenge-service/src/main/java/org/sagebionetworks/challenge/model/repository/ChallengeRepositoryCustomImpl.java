package org.sagebionetworks.challenge.model.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.challenge.model.entity.QChallengeEntity;
import org.sagebionetworks.challenge.model.entity.QSimpleChallengePlatformEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
  public Page<ChallengeEntity> findAll(Pageable pageable, ChallengeFilter filter) {
    QChallengeEntity challenge = QChallengeEntity.challengeEntity;
    QSimpleChallengePlatformEntity platform =
        QSimpleChallengePlatformEntity.simpleChallengePlatformEntity;

    JPQLQuery<ChallengeEntity> query =
        from(challenge).leftJoin(platform).on(challenge.platform.id.eq(platform.id));

    if (filter.getStatus() != null && filter.getStatus().size() > 0) {
      query = query.where(challenge.status.in(filter.getStatus()));
    }
    if (filter.getPlatforms() != null && filter.getPlatforms().size() > 0) {
      query = query.where(challenge.platform.name.in(filter.getPlatforms()));
    }
    if (filter.getDifficulties() != null && filter.getDifficulties().size() > 0) {
      query = query.where(challenge.difficulty.in(filter.getDifficulties()));
    }

    query = super.getQuerydsl().applyPagination(pageable, query);

    // SearchResults<Employees> entitys = query.listResults(QEmployees.employees);
    // return new PageImpl<Employees>(entitys.getResults(), pageable, entitys.getTotal());
    // SearchResults
    QueryResults<ChallengeEntity> results = query.fetchResults();

    return new PageImpl<ChallengeEntity>(results.getResults(), pageable, results.getTotal());
  }
}

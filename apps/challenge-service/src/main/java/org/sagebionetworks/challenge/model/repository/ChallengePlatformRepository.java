package org.sagebionetworks.challenge.model.repository;

import org.sagebionetworks.challenge.model.entity.ChallengePlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ChallengePlatformRepository
    extends JpaRepository<ChallengePlatformEntity, Long>,
        QuerydslPredicateExecutor<ChallengePlatformEntity> {}

package org.sagebionetworks.challenge.model.repository;

import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ChallengeRepository
    extends JpaRepository<ChallengeEntity, Long>,
        QuerydslPredicateExecutor<ChallengeEntity>,
        CustomChallengeRepository,
        SearchRepository<ChallengeEntity, Long> {}

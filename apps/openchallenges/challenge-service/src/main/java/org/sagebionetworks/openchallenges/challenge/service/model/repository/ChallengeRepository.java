package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.projection.YearlyChallengeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChallengeRepository
  extends JpaRepository<ChallengeEntity, Long>, CustomChallengeRepository {
  @Query(
    value = "SELECT EXTRACT(YEAR FROM start_date) AS year, COUNT(*) AS count " +
    "FROM challenge " +
    "WHERE start_date IS NOT NULL " +
    "GROUP BY year ORDER BY year",
    nativeQuery = true
  )
  List<YearlyChallengeCount> findChallengeCountsByYear();

  @Query(value = "SELECT COUNT(*) FROM challenge WHERE start_date IS NULL", nativeQuery = true)
  int countUndatedChallenges();
}

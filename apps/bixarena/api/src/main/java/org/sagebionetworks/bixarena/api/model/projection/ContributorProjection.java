package org.sagebionetworks.bixarena.api.model.projection;

/**
 * Projection interface for quest contributor query results.
 * Used by BattleRepository.findContributorsByDateRange()
 */
public interface ContributorProjection {

  String getUsername();

  Integer getBattleCount();
}

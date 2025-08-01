/*
 * OpenChallenges API
 * Discover, explore, and contribute to open biomedical challenges.
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package org.sagebionetworks.openchallenges.api.client.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.api.client.model.BasicError;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContribution;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionCreateRequest;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionRole;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionsPage;

/**
 * API tests for ChallengeContributionApi
 */
@Disabled
public class ChallengeContributionApiTest {

  private final ChallengeContributionApi api = new ChallengeContributionApi();

  /**
   * Create a new contribution for a challenge
   *
   * Creates a new contribution record associated with a challenge ID.
   */
  @Test
  public void createChallengeContributionTest() {
    Long challengeId = null;
    ChallengeContributionCreateRequest challengeContributionCreateRequest = null;
    ChallengeContribution response = api.createChallengeContribution(
      challengeId,
      challengeContributionCreateRequest
    );
    // TODO: test validations
  }

  /**
   * Delete a specific challenge contribution
   *
   * Delete a specific challenge contribution.
   */
  @Test
  public void deleteChallengeContributionTest() {
    Long challengeId = null;
    Long organizationId = null;
    ChallengeContributionRole role = null;
    api.deleteChallengeContribution(challengeId, organizationId, role);
    // TODO: test validations
  }

  /**
   * Get a specific challenge contribution
   *
   * Retrieves a specific contribution record for a challenge, identified by its ID.
   */
  @Test
  public void getChallengeContributionTest() {
    Long challengeId = null;
    Long organizationId = null;
    ChallengeContributionRole role = null;
    ChallengeContribution response = api.getChallengeContribution(
      challengeId,
      organizationId,
      role
    );
    // TODO: test validations
  }

  /**
   * List challenge contributions
   *
   * List challenge contributions
   */
  @Test
  public void listChallengeContributionsTest() {
    Long challengeId = null;
    ChallengeContributionsPage response = api.listChallengeContributions(challengeId);
    // TODO: test validations
  }
}

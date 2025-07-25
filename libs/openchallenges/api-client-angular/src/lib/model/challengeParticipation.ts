/**
 * OpenChallenges REST API
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { ChallengeParticipationRole } from './challengeParticipationRole';

/**
 * An challenge participation.
 */
export interface ChallengeParticipation {
  /**
   * The unique identifier of a challenge participation
   */
  id: number;
  /**
   * The unique identifier of the challenge.
   */
  challengeId: number;
  /**
   * The unique identifier of an organization
   */
  organizationId: number;
  role: ChallengeParticipationRole;
}
export namespace ChallengeParticipation {}

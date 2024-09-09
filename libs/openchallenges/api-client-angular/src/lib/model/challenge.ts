/**
 * OpenChallenges REST API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { ChallengeCategory } from './challengeCategory';
import { EdamConcept } from './edamConcept';
import { SimpleChallengePlatform } from './simpleChallengePlatform';
import { ChallengeStatus } from './challengeStatus';
import { ChallengeIncentive } from './challengeIncentive';
import { ChallengeSubmissionType } from './challengeSubmissionType';

/**
 * A challenge
 */
export interface Challenge {
  /**
   * The unique identifier of the challenge.
   */
  id: number;
  /**
   * The unique slug of the challenge.
   */
  slug: string;
  /**
   * The name of the challenge.
   */
  name: string;
  /**
   * The headline of the challenge.
   */
  headline?: string | null;
  /**
   * The description of the challenge.
   */
  description: string;
  /**
   * The DOI of the challenge.
   */
  doi?: string | null;
  status: ChallengeStatus;
  platform?: SimpleChallengePlatform | null;
  /**
   * A URL to the website or image.
   */
  websiteUrl?: string | null;
  /**
   * A URL to the website or image.
   */
  avatarUrl?: string | null;
  incentives: Array<ChallengeIncentive>;
  submissionTypes: Array<ChallengeSubmissionType>;
  inputDataTypes?: Array<EdamConcept>;
  categories: Array<ChallengeCategory>;
  /**
   * The start date of the challenge.
   */
  startDate?: string | null;
  /**
   * The end date of the challenge.
   */
  endDate?: string | null;
  /**
   * The number of times the challenge has been starred by users.
   */
  starredCount: number;
  operation?: EdamConcept | null;
  /**
   * Datetime when the object was added to the database.
   */
  createdAt: string;
  /**
   * Datetime when the object was last modified in the database.
   */
  updatedAt: string;
}

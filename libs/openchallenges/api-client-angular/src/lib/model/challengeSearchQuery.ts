/**
 * OpenChallenges API
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { ChallengeDirection } from './challengeDirection';
import { ChallengeCategory } from './challengeCategory';
import { ChallengeSort } from './challengeSort';
import { ChallengeStatus } from './challengeStatus';
import { ChallengeIncentive } from './challengeIncentive';
import { ChallengeSubmissionType } from './challengeSubmissionType';

/**
 * A challenge search query.
 */
export interface ChallengeSearchQuery {
  /**
   * The page number.
   */
  pageNumber?: number;
  /**
   * The number of items in a single page.
   */
  pageSize?: number;
  sort?: ChallengeSort;
  /**
   * The seed that initializes the random sorter.
   */
  sortSeed?: number | null;
  direction?: ChallengeDirection | null;
  /**
   * An array of challenge incentive types used to filter the results.
   */
  incentives?: Array<ChallengeIncentive>;
  /**
   * Keep the challenges that start at this date or later.
   */
  minStartDate?: string | null;
  /**
   * Keep the challenges that start at this date or sooner.
   */
  maxStartDate?: string | null;
  /**
   * An array of challenge platform ids used to filter the results.
   */
  platforms?: Array<string>;
  /**
   * An array of organization ids used to filter the results.
   */
  organizations?: Array<number>;
  /**
   * An array of challenge status used to filter the results.
   */
  status?: Array<ChallengeStatus>;
  /**
   * An array of challenge submission types used to filter the results.
   */
  submissionTypes?: Array<ChallengeSubmissionType>;
  /**
   * An array of EDAM concept ID used to filter the results.
   */
  inputDataTypes?: Array<number>;
  /**
   * An array of EDAM concept ID used to filter the results.
   */
  operations?: Array<number>;
  /**
   * The array of challenge categories used to filter the results.
   */
  categories?: Array<ChallengeCategory>;
  /**
   * A string of search terms used to filter the results.
   */
  searchTerms?: string;
}
export namespace ChallengeSearchQuery {}

/**
 * The role of a challenge sponsor
 */
export type ChallengeSponsorRole =
  | 'ChallengeOrganizer'
  | 'ComputeProvider'
  | 'DataProvider'
  | 'Funder'
  | 'Other';

/**
 * A challenge sponsor
 */
export interface ChallengeSponsor {
  /**
   * The unique identifier of a challenge sponsor
   */
  id: string;
  name: string;
  /**
   * The user or organization account name
   */
  login?: string;
  roles?: Array<ChallengeSponsorRole>;
}

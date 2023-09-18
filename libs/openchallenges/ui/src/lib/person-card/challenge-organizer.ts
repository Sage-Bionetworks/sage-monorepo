/**
 * The role of the challenge organizer
 */
export type ChallengeOrganizerRole = 'ChallengeLead' | 'InfrastructureLead';

/**
 * A challenge organizer
 */
export interface ChallengeOrganizer {
  /**
   * The unique identifier of a challenge organizer
   */
  id: string;
  name: string;
  challengeId: string;
  /**
   * The user or organization account name
   */
  login?: string;
  avatarUrl?: string;
  roles?: Array<ChallengeOrganizerRole>;
}

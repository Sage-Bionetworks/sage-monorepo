import { License } from '@sagebionetworks/bixarena/api-client';

export interface LeaderboardFilters {
  readonly license: License | null;
}

export const DEFAULT_LEADERBOARD_FILTERS: LeaderboardFilters = {
  license: null,
};

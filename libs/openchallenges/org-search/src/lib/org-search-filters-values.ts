import { FilterValue } from '@sagebionetworks/openchallenges/ui';

export const challengeContributionRolesFilterValues: FilterValue[] = [
  {
    value: 'challenge_organizer',
    label: 'Challenge Organizer',
  },
  {
    value: 'data_contributor',
    label: 'Data Contributor',
  },
  {
    value: 'sponsor',
    label: 'Sponsor',
  },
];

export const organizationSortFilterValues: FilterValue[] = [
  {
    value: 'relevance',
    label: 'Relevance',
  },
  {
    value: 'challenge_count',
    label: 'Challenge Count',
  },
];

import { Filter } from '@sagebionetworks/openchallenges/ui';

export const challengeContributionRolesFilter: Filter[] = [
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

export const organizationCategoriesFilter: Filter[] = [
  {
    value: 'featured',
    label: 'Featured',
  },
];

export const organizationSortFilter: Filter[] = [
  {
    value: 'relevance',
    label: 'Relevance',
  },
  {
    value: 'challenge_count',
    label: 'Challenge Count',
  },
];

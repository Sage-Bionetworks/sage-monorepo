import { FilterValue } from '@sagebionetworks/openchallenges/ui';

export const challengeStatusFilterValues: FilterValue[] = [
  {
    value: 'active',
    label: 'Active',
  },
  {
    value: 'upcoming',
    label: 'Upcoming',
  },
  {
    value: 'completed',
    label: 'Completed',
  },
];

export const organizationSortFilterValues: FilterValue[] = [
  {
    value: 'relevance',
    label: 'Relevance',
  },
];

import {
  ChallengeContributionRole,
  OrganizationCategory,
  OrganizationSort,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Filter } from '@sagebionetworks/openchallenges/ui';

export const challengeContributionRolesFilter: Filter[] = [
  {
    value: ChallengeContributionRole.ChallengeOrganizer,
    label: 'Challenge Organizer',
  },
  {
    value: ChallengeContributionRole.DataContributor,
    label: 'Data Contributor',
  },
  {
    value: ChallengeContributionRole.Sponsor,
    label: 'Sponsor',
  },
];

export const organizationCategoriesFilter: Filter[] = [
  {
    value: OrganizationCategory.Featured,
    label: 'Featured',
  },
];

export const organizationSortFilter: Filter[] = [
  {
    value: OrganizationSort.Relevance,
    label: 'Relevance',
  },
  {
    value: OrganizationSort.ChallengeCount,
    label: 'Challenge Count',
  },
];

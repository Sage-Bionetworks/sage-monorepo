import {
  ChallengeParticipationRole,
  OrganizationCategory,
  OrganizationSort,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Filter } from '@sagebionetworks/openchallenges/ui';

export const challengeParticipationRolesFilter: Filter[] = [
  {
    value: ChallengeParticipationRole.ChallengeOrganizer,
    label: 'Challenge Organizer',
  },
  {
    value: ChallengeParticipationRole.DataContributor,
    label: 'Data Contributor',
  },
  {
    value: ChallengeParticipationRole.Sponsor,
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

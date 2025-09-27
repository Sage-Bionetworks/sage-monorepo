import { Challenge } from '@sagebionetworks/openchallenges/api-client';
import { MOCK_PLATFORMS } from './mock-platforms';

export const MOCK_CHALLENGES: Challenge[] = [
  {
    id: 1,
    slug: 'awesome_challenge',
    name: 'Awesome Challenge',
    headline: 'Example headline',
    description: 'Example description',
    status: 'active',
    platform: MOCK_PLATFORMS[0],
    incentives: [],
    submissionTypes: [],
    inputDataTypes: [],
    startDate: '2017-01-01',
    endDate: '2030-01-01',
    starredCount: 99,
    createdAt: '',
    updatedAt: '',
    categories: [],
  },
];

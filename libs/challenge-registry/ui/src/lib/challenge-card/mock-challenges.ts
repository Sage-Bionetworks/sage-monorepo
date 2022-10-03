import { Challenge } from '@sagebionetworks/api-client-angular-deprecated';

export const MOCK_CHALLENGES: Challenge[] = [
  {
    id: '',
    name: 'awesome-challenge',
    displayName: 'Awesome Challenge',
    description:
      'This is the challenge tagline. It should be a short descriptive summary of the challenge',
    websiteUrl: '',
    status: undefined,
    startDate: '2000-01-01',
    endDate: '2030-01-01',
    readmeId: '61b26e5ee98893556289e1b3',
    platformId: '61b26e5ee98893556289e1a8',
    ownerId: '61b26e5ce98893556289df29',
    topics: ['breast', 'cancer'],
    fullName: 'dream/awesome-challenge',
    inputDataTypes: [
      'demographic',
      'clinical',
      'genomic',
      'survival',
      'mutation',
      'copy-number-variation',
      'gene-expression',
      'microarray',
    ],
    submissionTypes: ['PredictionFile'],
    incentiveTypes: ['Publication', 'SpeakingEngagement'],
    featured: true,
    starredCount: 99,
    viewCount: 9999,
    difficulty: 'GoodForBeginners',
    doi: null,
    createdAt: '',
    updatedAt: '',
  },
  {
    id: '',
    name: 'active-challenge',
    displayName: 'Active Challenge',
    description:
      'This is the challenge tagline. It should be a short descriptive summary of the challenge',
    websiteUrl: '',
    status: 'active',
    startDate: '2000-01-01',
    endDate: '2030-01-01',
    readmeId: '61b26e5ee98893556289e1b3',
    platformId: '61b26e5ee98893556289e1a8',
    ownerId: '61b26e5ce98893556289df29',
    topics: ['breast', 'cancer'],
    fullName: 'dream/active-challenge',
    inputDataTypes: [
      'demographic',
      'clinical',
      'genomic',
      'survival',
      'mutation',
      'copy-number-variation',
      'gene-expression',
      'microarray',
    ],
    submissionTypes: ['PredictionFile'],
    incentiveTypes: ['Publication', 'SpeakingEngagement'],
    featured: true,
    starredCount: 99,
    viewCount: 9999,
    difficulty: 'Intermediate',
    doi: null,
    createdAt: '',
    updatedAt: '',
  },
  {
    id: '',
    name: 'completed-challenge',
    displayName: 'Completed Challenge',
    description:
      'This is the challenge tagline. It should be a short descriptive summary of the challenge',
    websiteUrl: '',
    status: 'completed',
    startDate: '2000-01-01',
    endDate: '2030-01-01',
    readmeId: '61b26e5ee98893556289e1b3',
    platformId: '61b26e5ee98893556289e1a8',
    ownerId: '61b26e5ce98893556289df29',
    topics: ['breast', 'cancer'],
    fullName: 'dream/completed-challenge',
    inputDataTypes: [
      'demographic',
      'clinical',
      'genomic',
      'survival',
      'mutation',
      'copy-number-variation',
      'gene-expression',
      'microarray',
    ],
    submissionTypes: ['PredictionFile'],
    incentiveTypes: ['Publication', 'SpeakingEngagement'],
    featured: true,
    starredCount: 99,
    viewCount: 9999,
    difficulty: 'Intermediate',
    doi: null,
    createdAt: '',
    updatedAt: '',
  },
  {
    id: '',
    name: 'upcoming-challenge',
    displayName: 'Upcoming Challenge',
    description:
      'This is the challenge tagline. It should be a short descriptive summary of the challenge',
    websiteUrl: '',
    status: 'upcoming',
    startDate: '2000-01-01',
    endDate: '2030-01-01',
    readmeId: '61b26e5ee98893556289e1b3',
    platformId: '61b26e5ee98893556289e1a8',
    ownerId: '61b26e5ce98893556289df29',
    topics: ['breast', 'cancer'],
    fullName: 'dream/upcoming-challenge',
    inputDataTypes: [
      'demographic',
      'clinical',
      'genomic',
      'survival',
      'mutation',
      'copy-number-variation',
      'gene-expression',
      'microarray',
    ],
    submissionTypes: ['PredictionFile'],
    incentiveTypes: ['Publication', 'SpeakingEngagement'],
    featured: true,
    starredCount: 99,
    viewCount: 9999,
    difficulty: 'Advanced',
    doi: null,
    createdAt: '',
    updatedAt: '',
  },
];

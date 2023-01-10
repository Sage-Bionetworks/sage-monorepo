import { ChallengeOrganizer } from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';

export const MOCK_CHALLENGE_ORGANIZERS: ChallengeOrganizer[] = [
  {
    id: '',
    challengeId: '1',
    login: 'awesome-lead',
    name: 'Awesome Lead',
    roles: ['ChallengeLead'],
  },
  {
    id: '',
    challengeId: '2',
    name: 'Awesome Engineer',
    login: 'awesome-engineer',
    roles: ['InfrastructureLead'],
  },
];

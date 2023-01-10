import { ChallengeOrganizer } from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';

export const MOCK_CHALLENGE_ORGANIZERS: ChallengeOrganizer[] = [
  {
    id: '',
    challengeIds: ['1', '2'],
    login: 'awesome-lead',
    name: 'Awesome Lead',
    roles: ['ChallengeLead'],
  },
  {
    id: '',
    challengeIds: ['2', '4'],
    name: 'Awesome Engineer',
    login: 'awesome-engineer',
    roles: ['InfrastructureLead'],
  },
];

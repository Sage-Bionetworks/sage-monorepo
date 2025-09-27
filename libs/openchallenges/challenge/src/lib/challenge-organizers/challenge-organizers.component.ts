import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client';
import {
  MOCK_CHALLENGE_ORGANIZERS,
  MOCK_ORGANIZATION_CARDS,
  PersonCardComponent,
} from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-challenge-organizers',
  imports: [PersonCardComponent],
  templateUrl: './challenge-organizers.component.html',
  styleUrls: ['./challenge-organizers.component.scss'],
})
export class ChallengeOrganizersComponent {
  @Input({ required: true }) challenge!: Challenge;
  // TODO: Replace with challenge's organizers
  organizers = MOCK_CHALLENGE_ORGANIZERS;
  organizationCard = MOCK_ORGANIZATION_CARDS[0];
}

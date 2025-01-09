import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular';
import {
  MOCK_CHALLENGE_ORGANIZERS,
  MOCK_ORGANIZATION_CARDS,
  PersonCardComponent,
} from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-challenge-organizers',
  imports: [CommonModule, PersonCardComponent],
  templateUrl: './challenge-organizers.component.html',
  styleUrls: ['./challenge-organizers.component.scss'],
})
export class ChallengeOrganizersComponent {
  @Input({ required: true }) challenge!: Challenge;
  // TODO: Replace with challenge's organizers
  organizers = MOCK_CHALLENGE_ORGANIZERS;
  organizationCard = MOCK_ORGANIZATION_CARDS[0];
}

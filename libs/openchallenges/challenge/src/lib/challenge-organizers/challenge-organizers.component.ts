import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import {
  MOCK_CHALLENGE_ORGANIZERS,
  MOCK_ORGANIZATIONS,
} from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-challenge-organizers',
  templateUrl: './challenge-organizers.component.html',
  styleUrls: ['./challenge-organizers.component.scss'],
})
export class ChallengeOrganizersComponent {
  @Input() challenge!: Challenge;
  // TODO: Replace with challenge's organizers
  organizers = MOCK_CHALLENGE_ORGANIZERS;
  organization = MOCK_ORGANIZATIONS[0];
}

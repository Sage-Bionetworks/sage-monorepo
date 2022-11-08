import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/api-client-angular-deprecated';
import { MOCK_ORG_MEMBERS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-challenge-organizers',
  templateUrl: './challenge-organizers.component.html',
  styleUrls: ['./challenge-organizers.component.scss'],
})
export class ChallengeOrganizersComponent {
  @Input() challenge!: Challenge;
  // TODO: Replace with challenge's organizers
  organizers = MOCK_ORG_MEMBERS;
}

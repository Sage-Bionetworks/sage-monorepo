import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/api-client-angular-deprecated';
import { MOCK_ORG_MEMBERS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-challenge-sponsors',
  templateUrl: './challenge-sponsors.component.html',
  styleUrls: ['./challenge-sponsors.component.scss'],
})
export class ChallengeSponsorsComponent {
  @Input() challenge!: Challenge;
  members = MOCK_ORG_MEMBERS;
}

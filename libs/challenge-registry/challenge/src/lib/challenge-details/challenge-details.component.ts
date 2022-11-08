import { Component, Input } from '@angular/core';
import {
  Challenge,
  Organization,
} from '@sagebionetworks/api-client-angular-deprecated';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-challenge-details',
  templateUrl: './challenge-details.component.html',
  styleUrls: ['./challenge-details.component.scss'],
})
export class ChallengeDetailsComponent {
  @Input() challenge!: Challenge;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
}

import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';
import { Organization } from '@sagebionetworks/challenge-registry/api-client-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-challenge-overview',
  templateUrl: './challenge-overview.component.html',
  styleUrls: ['./challenge-overview.component.scss'],
})
export class ChallengeOverviewComponent {
  @Input() challenge!: Challenge;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
}

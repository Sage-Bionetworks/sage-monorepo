import { Component, Input } from '@angular/core';
import {
  Challenge,
  Organization,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-challenge-overview',
  templateUrl: './challenge-overview.component.html',
  styleUrls: ['./challenge-overview.component.scss'],
})
export class ChallengeOverviewComponent {
  @Input() challenge!: Challenge;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
  mockTopics = ['breast', 'cancer'];
}

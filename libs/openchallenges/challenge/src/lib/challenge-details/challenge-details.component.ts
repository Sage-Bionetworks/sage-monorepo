import { Component, Input } from '@angular/core';
import {
  Challenge,
  // ChallengePlatform,
  // Organization,
  Organization,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-challenge-details',
  templateUrl: './challenge-details.component.html',
  styleUrls: ['./challenge-details.component.scss'],
})
export class ChallengeDetailsComponent {
  @Input() challenge!: Challenge;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
  mockTopics = ['breast', 'cancer'];
  mockDoi = '09.1937/09219137';

  printCamel(camel: string | undefined) {
    return camel ? camel.replace(/([a-z])([A-Z])/g, '$1 $2') : undefined;
  }
}

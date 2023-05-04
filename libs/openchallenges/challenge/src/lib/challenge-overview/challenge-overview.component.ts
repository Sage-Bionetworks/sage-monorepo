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
  mockDoi = '09.1937/09219137';

  printCamel(camel: string | undefined) {
    return camel ? camel.replace(/([a-z])([A-Z])/g, '$1 $2') : undefined;
  }
}

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
  // mockTopics = ['breast', 'cancer'];
  mockDoi = '09.1937/09219137';

  use_default(str: string) {
    return str === '' ? 'Not available' : str;
  }

  prettify(camel: string | undefined) {
    return camel
      ? camel.charAt(0).toUpperCase() +
          camel.slice(1).replace(/_/g, ' ').toLowerCase()
      : undefined;
  }
}

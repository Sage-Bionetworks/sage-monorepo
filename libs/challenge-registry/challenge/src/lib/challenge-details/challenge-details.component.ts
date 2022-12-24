import { Component, Input } from '@angular/core';
import {
  Challenge,
  // ChallengePlatform,
  // Organization,
} from '@sagebionetworks/api-client-angular-deprecated';
import { Organization } from '@sagebionetworks/challenge-registry/api-client-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-challenge-details',
  templateUrl: './challenge-details.component.html',
  styleUrls: ['./challenge-details.component.scss'],
})
export class ChallengeDetailsComponent {
  @Input() challenge!: Challenge;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
  // platform!: ChallengePlatform;
  // mock up platform
  platform = 'Awesome Platform';

  printCamel(camel: string | undefined) {
    return camel ? camel.replace(/([a-z])([A-Z])/g, '$1 $2') : undefined;
  }
}

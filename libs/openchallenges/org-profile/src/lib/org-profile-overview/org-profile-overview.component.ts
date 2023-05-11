import { Component, Input } from '@angular/core';
import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-org-profile-overview',
  templateUrl: './org-profile-overview.component.html',
  styleUrls: ['./org-profile-overview.component.scss'],
})
export class OrgProfileOverviewComponent {
  @Input() organization!: Organization;
  organizations: Organization[] = MOCK_ORGANIZATIONS;

  use_default(str: string) {
    return str === '' ? 'Not available' : str;
  }
}

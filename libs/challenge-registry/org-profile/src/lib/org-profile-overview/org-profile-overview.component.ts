import { Component, Input } from '@angular/core';
import { Organization } from '@sagebionetworks/api-client-angular-deprecated';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-org-profile-overview',
  templateUrl: './org-profile-overview.component.html',
  styleUrls: ['./org-profile-overview.component.scss'],
})
export class OrgProfileOverviewComponent {
  @Input() organization!: Organization;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
}

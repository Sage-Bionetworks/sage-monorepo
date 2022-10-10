import { Component, Input } from '@angular/core';
import { Organization } from '@sagebionetworks/api-client-angular-deprecated';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-organization-overview',
  templateUrl: './organization-overview.component.html',
  styleUrls: ['./organization-overview.component.scss'],
})
export class OrganizationOverviewComponent {
  @Input() organization!: Organization;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
}

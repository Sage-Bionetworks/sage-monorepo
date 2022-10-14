import { Component, Input } from '@angular/core';
import { Organization } from '@sagebionetworks/api-client-angular-deprecated';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-organization-members',
  templateUrl: './organization-members.component.html',
  styleUrls: ['./organization-members.component.scss'],
})
export class OrganizationMembersComponent {
  @Input() organization!: Organization;
  members = MOCK_ORGANIZATIONS;
}

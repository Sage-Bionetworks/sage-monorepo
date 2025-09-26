import { Component, Input } from '@angular/core';
import { Organization } from '@sagebionetworks/openchallenges/api-client';
import { MOCK_ORG_MEMBERS, PersonCardComponent } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-org-profile-members',
  imports: [PersonCardComponent],
  templateUrl: './org-profile-members.component.html',
  styleUrls: ['./org-profile-members.component.scss'],
})
export class OrgProfileMembersComponent {
  @Input({ required: true }) organization!: Organization;
  members = MOCK_ORG_MEMBERS;
}

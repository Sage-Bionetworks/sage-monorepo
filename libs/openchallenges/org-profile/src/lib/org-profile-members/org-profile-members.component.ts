import { Component, Input } from '@angular/core';
import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
import { MOCK_ORG_MEMBERS } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-org-profile-members',
  templateUrl: './org-profile-members.component.html',
  styleUrls: ['./org-profile-members.component.scss'],
})
export class OrgProfileMembersComponent {
  @Input() organization!: Organization;
  members = MOCK_ORG_MEMBERS;
}

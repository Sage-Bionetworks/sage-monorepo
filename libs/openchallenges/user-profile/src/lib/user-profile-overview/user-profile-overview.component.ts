import { Component, Input } from '@angular/core';
import { User } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-user-profile-overview',
  templateUrl: './user-profile-overview.component.html',
  styleUrls: ['./user-profile-overview.component.scss'],
})
export class UserProfileOverviewComponent {
  @Input() user!: User;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
}

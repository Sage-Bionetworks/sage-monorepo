import { Component, Input } from '@angular/core';
import { User } from '@sagebionetworks/api-client-angular-deprecated';
import { Organization } from '@sagebionetworks/api-client-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-user-profile-overview',
  templateUrl: './user-profile-overview.component.html',
  styleUrls: ['./user-profile-overview.component.scss'],
})
export class UserProfileOverviewComponent {
  @Input() user!: User;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
}

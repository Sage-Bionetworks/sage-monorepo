import { Component, Input } from '@angular/core';
import { Organization, User } from '@sagebionetworks/api-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-user-profile-overview',
  templateUrl: './user-profile-overview.component.html',
  styleUrls: ['./user-profile-overview.component.scss'],
})
export class UserProfileOverviewComponent {
  @Input() user!: User;
  @Input() organizations: Organization[] = MOCK_ORGANIZATIONS;
}

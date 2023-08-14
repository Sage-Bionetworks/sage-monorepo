import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { User } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import {
  MOCK_ORGANIZATION_CARDS,
  OrganizationCard,
  UiModule,
} from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-user-profile-overview',
  standalone: true,
  imports: [CommonModule, UiModule],
  templateUrl: './user-profile-overview.component.html',
  styleUrls: ['./user-profile-overview.component.scss'],
})
export class UserProfileOverviewComponent {
  @Input() user!: User;
  organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;
}

import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
import {
  MOCK_ORGANIZATION_CARDS,
  OrganizationCard,
} from '@sagebionetworks/openchallenges/ui';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'openchallenges-org-profile-overview',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './org-profile-overview.component.html',
  styleUrls: ['./org-profile-overview.component.scss'],
})
export class OrgProfileOverviewComponent {
  @Input() organization!: Organization;
  organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;

  use_default(str: string | undefined) {
    return str === '' ? 'Not available' : str;
  }
}

import { NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Organization } from '@sagebionetworks/openchallenges/api-client';
import { MOCK_ORGANIZATION_CARDS, OrganizationCard } from '@sagebionetworks/openchallenges/ui';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'openchallenges-org-profile-overview',
  imports: [NgClass, MatIconModule],
  templateUrl: './org-profile-overview.component.html',
  styleUrls: ['./org-profile-overview.component.scss'],
})
export class OrgProfileOverviewComponent {
  @Input({ required: true }) organization!: Organization;
  organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;

  useNaIfFalsey(str: string | undefined | null) {
    return str || 'Not available';
  }
}

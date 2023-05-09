import { Component, Input, OnInit } from '@angular/core';
import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-org-profile-overview',
  templateUrl: './org-profile-overview.component.html',
  styleUrls: ['./org-profile-overview.component.scss'],
})
export class OrgProfileOverviewComponent implements OnInit {
  @Input() organization!: Organization;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
  email!: string;

  ngOnInit(): void {
    this.email =
      this.organization.email === ''
        ? 'N/A (no general email found)'
        : this.organization.email;
  }
}

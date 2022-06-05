import { Component, OnInit } from '@angular/core';
import { Organization } from '@sagebionetworks/api-angular';
import { OrgProfileDataServiceService } from '../org-profile-data-service.service';

@Component({
  selector: 'challenge-registry-org-profile-overview',
  templateUrl: './org-profile-overview.component.html',
  styleUrls: ['./org-profile-overview.component.scss'],
})
export class OrgProfileOverviewComponent implements OnInit {
  public org: Organization | undefined;

  constructor(private orgProfileDataService: OrgProfileDataServiceService) {}

  ngOnInit(): void {
    this.orgProfileDataService.getOrg().subscribe((org) => (this.org = org));
  }
}

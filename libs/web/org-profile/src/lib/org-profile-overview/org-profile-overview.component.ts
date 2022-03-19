import { Component, Input } from '@angular/core';
import { Organization } from '@challenge-registry/api-angular';

@Component({
  selector: 'challenge-registry-org-profile-overview',
  templateUrl: './org-profile-overview.component.html',
  styleUrls: ['./org-profile-overview.component.scss'],
})
export class OrgProfileOverviewComponent {
  @Input() org!: Organization;
}

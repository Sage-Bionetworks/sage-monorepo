import { Component, Input } from '@angular/core';

@Component({
  selector: 'challenge-registry-org-profile-stats',
  templateUrl: './org-profile-stats.component.html',
  styleUrls: ['./org-profile-stats.component.scss'],
})
export class OrgProfileStatsComponent {
  @Input() loggedIn = false;
}

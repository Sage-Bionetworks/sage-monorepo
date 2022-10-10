import { Component, Input } from '@angular/core';

@Component({
  selector: 'challenge-registry-organization-stats',
  templateUrl: './organization-stats.component.html',
  styleUrls: ['./organization-stats.component.scss'],
})
export class OrganizationStatsComponent {
  @Input() loggedIn = false;
}

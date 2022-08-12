import { Component, Input } from '@angular/core';

@Component({
  selector: 'challenge-registry-user-profile-stats',
  templateUrl: './user-profile-stats.component.html',
  styleUrls: ['./user-profile-stats.component.scss'],
})
export class UserProfileStatsComponent {
  @Input() loggedIn = false;
}

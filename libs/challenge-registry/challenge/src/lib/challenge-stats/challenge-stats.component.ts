import { Component, Input } from '@angular/core';

@Component({
  selector: 'challenge-registry-challenge-stats',
  templateUrl: './challenge-stats.component.html',
  styleUrls: ['./challenge-stats.component.scss'],
})
export class ChallengeStatsComponent {
  @Input() loggedIn = false;
}

import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'bixarena-leaderboard-section',
  templateUrl: './leaderboard-section.component.html',
  styleUrl: './leaderboard-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LeaderboardSectionComponent {}

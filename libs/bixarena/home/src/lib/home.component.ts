import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ComposerSectionComponent } from './composer-section/composer-section.component';
import { LeaderboardSectionComponent } from './leaderboard-section/leaderboard-section.component';
import { StatsSectionComponent } from './stats-section/stats-section.component';
import { TrendingSectionComponent } from './trending-section/trending-section.component';

@Component({
  selector: 'bixarena-home',
  imports: [
    ComposerSectionComponent,
    StatsSectionComponent,
    TrendingSectionComponent,
    LeaderboardSectionComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent {}

import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { LeaderboardEntry } from '@sagebionetworks/bixarena/api-client';
import { ModelOrgLogoService } from '@sagebionetworks/bixarena/services';
import { AvatarComponent } from '@sagebionetworks/bixarena/ui';
import { LEADERBOARD_BAR_CHART_TOP_N } from '../leaderboard.constants';
import { ChartBar } from './chart-bar';

// Top-N bar chart visualization for the leaderboard. Pure HTML/SCSS — no chart library.
// Sibling of LeaderboardTableComponent; both consume the same `entries` signal from
// LeaderboardFacadeService. The parent toolbar drives which view is rendered.
@Component({
  selector: 'bixarena-leaderboard-bar-chart',
  imports: [AvatarComponent],
  templateUrl: './leaderboard-bar-chart.component.html',
  styleUrl: './leaderboard-bar-chart.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LeaderboardBarChartComponent {
  private readonly orgLogoService = inject(ModelOrgLogoService);

  readonly entries = input.required<LeaderboardEntry[]>();

  // Map raw entries → ChartBar view-models. Slices to top-N, normalizes heights to
  // a [MIN_HEIGHT_PCT, 100] range, and assigns brand vs silver gradient by rank.
  readonly bars = computed<ChartBar[]>(() => {
    const visible = this.entries().slice(0, LEADERBOARD_BAR_CHART_TOP_N);
    if (visible.length === 0) return [];

    let minScore = Infinity;
    let maxScore = -Infinity;
    for (const e of visible) {
      if (e.btScore < minScore) minScore = e.btScore;
      if (e.btScore > maxScore) maxScore = e.btScore;
    }
    // Floor keeps even the shortest bar tall enough to fit the logo.
    const MIN_HEIGHT_PCT = 32;
    const scoreRange = maxScore - minScore;

    // Top 3 ranks get the warm brand gradient (podium); the rest get a neutral silver.
    const podiumGradient = 'linear-gradient(to bottom, var(--p-primary-300), var(--p-primary-500))';
    const silverGradient = 'linear-gradient(to bottom, var(--p-slate-300), var(--p-slate-500))';
    return visible.map((entry) => {
      // When all visible entries share the same score, render at full height instead of the floor.
      const heightPct =
        scoreRange === 0
          ? 100
          : MIN_HEIGHT_PCT + ((entry.btScore - minScore) / scoreRange) * (100 - MIN_HEIGHT_PCT);
      return {
        id: entry.id,
        rank: entry.rank,
        slug: entry.modelId,
        modelName: entry.modelName,
        modelOrganization: entry.modelOrganization ?? null,
        score: Math.round(entry.btScore),
        heightPct,
        barGradient: entry.rank <= 3 ? podiumGradient : silverGradient,
        orgLogoUrl: this.orgLogoService.getLogoUrl(entry.modelOrganization),
        orgLogoMono: this.orgLogoService.isMonoLogo(entry.modelOrganization),
      };
    });
  });
}

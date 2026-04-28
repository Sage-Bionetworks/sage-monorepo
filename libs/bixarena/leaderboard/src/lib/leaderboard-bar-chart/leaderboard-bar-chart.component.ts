import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { LeaderboardEntry } from '@sagebionetworks/bixarena/api-client';
import { AvatarComponent } from '@sagebionetworks/bixarena/ui';
import { LEADERBOARD_BAR_CHART_TOP_N } from '../leaderboard.constants';
import { getOrgLogoUrl, isMonoOrgLogo } from '../model-org-logo';
import { ChartBar } from './chart-bar';

@Component({
  selector: 'bixarena-leaderboard-bar-chart',
  imports: [AvatarComponent],
  templateUrl: './leaderboard-bar-chart.component.html',
  styleUrl: './leaderboard-bar-chart.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LeaderboardBarChartComponent {
  readonly entries = input.required<LeaderboardEntry[]>();

  readonly bars = computed<ChartBar[]>(() => {
    const visible = this.entries().slice(0, LEADERBOARD_BAR_CHART_TOP_N);
    if (visible.length === 0) return [];

    let minScore = Infinity;
    let maxScore = -Infinity;
    for (const e of visible) {
      if (e.btScore < minScore) minScore = e.btScore;
      if (e.btScore > maxScore) maxScore = e.btScore;
    }
    const floor = minScore - 10;
    const range = maxScore - floor || 1;

    return visible.map((entry) => ({
      id: entry.id,
      rank: entry.rank,
      modelName: entry.modelName,
      modelOrganization: entry.modelOrganization ?? null,
      score: Math.round(entry.btScore),
      heightPct: Math.max(8, ((entry.btScore - floor) / range) * 100),
      orgLogoUrl: getOrgLogoUrl(entry.modelOrganization),
      orgLogoMono: isMonoOrgLogo(entry.modelOrganization),
    }));
  });
}
